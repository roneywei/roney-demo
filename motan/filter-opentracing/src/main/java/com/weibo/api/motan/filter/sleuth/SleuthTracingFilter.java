/*
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weibo.api.motan.filter.sleuth;

import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.util.LoggerUtil;
import com.weibo.api.motan.util.MotanFrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanInjector;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders;
import org.springframework.cloud.sleuth.instrument.web.HttpTraceKeysInjector;
import org.springframework.cloud.sleuth.instrument.web.TraceRequestAttributes;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.sleuth.trace.DefaultTracer;
import org.springframework.context.annotation.Bean;

import java.util.Map.Entry;

/**
 * @author zhanglei
 * @Description This filter enables distributed tracing in Motan clients and servers via @see <a
 * href="http://opentracing.io">The OpenTracing Project </a> : a set of consistent,
 * expressive, vendor-neutral APIs for distributed tracing and context propagation.
 * @date Dec 8, 2016
 */
@SpiMeta(name = "sleuth-tracing")
@Activation(sequence = 30)

public class SleuthTracingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SleuthTracingFilter.class);

    public static final String MOTAN_TAG = "motan";


    @Override
    public Response filter(Caller<?> caller, Request request) {
        Tracer tracer = null;
        try {
            tracer = getTracer();

        } catch (Exception e) {
            log.error("链路监控异常", e);
        }
        if (tracer == null || !(tracer instanceof DefaultTracer)) {
            return caller.call(request);
        }
        if (caller instanceof Provider) { // server end
            return processProviderTrace(tracer, caller, request);
        } else { // client end
            return processRefererTrace(tracer, caller, request);
        }

    }

    @Bean
    public AlwaysSampler defaultSampler() {
        return new AlwaysSampler();
    }

    protected Tracer getTracer() {
        return SleuthTracingContext.getTracer();
    }

    /**
     * process trace in client end
     *
     * @param caller
     * @param request
     * @return
     */
    protected Response processRefererTrace(Tracer tracer, Caller<?> caller, Request request) {
        Span span = extractTraceInfo(request, tracer);
        span.logEvent(Span.CLIENT_SEND);
        if (span.getSavedSpan() != null && span.getSavedSpan().tags() != null) {
            for (Entry<String, String> stringStringEntry : span.getSavedSpan().tags().entrySet()) {
                setHeader(request, stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }
        span.tag("requestId", String.valueOf(request.getRequestId()));
        span.tag(Span.SPAN_LOCAL_COMPONENT_TAG_NAME, MOTAN_TAG);
        attachTraceInfo(tracer, span, request);
        return process(tracer, caller, request, span);

    }

    protected Response process(Tracer tracer, Caller<?> caller, Request request, Span span) {
        Exception ex = null;
        boolean exception = true;
        try {
            Response response = caller.call(request);
            if (response.getException() != null) {
                ex = response.getException();
            } else {
                exception = false;
            }
            return response;
        } catch (RuntimeException e) {
            ex = e;
            throw e;
        } finally {
            try {
                if (exception) {

                    span.logEvent("motan request fail." + (ex == null ? "unknown exception" : ex.getMessage()));
                } else {

                    if (caller instanceof Provider) {
                        span.logEvent(Span.SERVER_SEND);

                    } else {
                        span.logEvent(Span.CLIENT_RECV);

                    }
                }
                tracer.close(span);
            } catch (Exception e) {
                LoggerUtil.error("opentracing span finish error!", e);
            }
        }
    }

    protected String buildOperationName(Request request) {
        return "Motan_" + MotanFrameworkUtil.getGroupMethodString(request);
    }

    SpanInjector spanInjector;
    HttpTraceKeysInjector httpTraceKeysInjector;

    private void setHeader(Request request, String key, Object value) {
        if (value != null) {
            request.setAttachment(key, value.toString());
        }
    }

    private Long getParentId(Span span) {
        return !span.getParents().isEmpty() ? span.getParents().get(0) : null;
    }

    protected void attachTraceInfo(Tracer tracer, Span span, final Request request) {

        if (span == null) {
            setHeader(request, Span.SAMPLED_NAME, Span.SPAN_NOT_SAMPLED);
            return;
        }
        setHeader(request, TraceRequestAttributes.HANDLED_SPAN_REQUEST_ATTR, "true");
        setHeader(request, Span.SPAN_ID_NAME, Span.idToHex(span.getSpanId()));
        setHeader(request, Span.TRACE_ID_NAME, span.traceIdString());
        setHeader(request, Span.SPAN_NAME_NAME, span.getName());
        setHeader(request, Span.SAMPLED_NAME, span.isExportable() ?
                Span.SPAN_SAMPLED : Span.SPAN_NOT_SAMPLED);
        setHeader(request, Span.PARENT_ID_NAME, Span.idToHex(getParentId(span)));
        setHeader(request, Span.PROCESS_ID_NAME, span.getProcessId());

        if (span.getSavedSpan() != null && span.getSavedSpan().tags() != null) {
            for (Entry<String, String> stringStringEntry : span.getSavedSpan().tags().entrySet()) {
                setHeader(request, stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }

    }

    /**
     * process trace in server end
     *
     * @param caller
     * @param request
     * @return
     */
    protected Response processProviderTrace(Tracer tracer, Caller<?> caller, Request request) {
        Span span = extractTraceInfo(request, tracer);
        span.tag("requestId", String.valueOf(request.getRequestId()));
        span.logEvent(Span.SERVER_RECV);
        SleuthTracingContext.setActiveSpan(span);
        return process(tracer, caller, request, span);
    }

    protected Span extractTraceInfo(Request request, Tracer tracer) {
        Span parentSpan = tracer.getCurrentSpan();
        if (parentSpan == null) {
            Span.SpanBuilder spanBuilder = Span.builder();
            if (request.getAttachments().get(TraceMessageHeaders.TRACE_ID_NAME) != null &&
                    !"".equals(request.getAttachments().get(TraceMessageHeaders.TRACE_ID_NAME))) {
                spanBuilder.traceId(Span.hexToId(request.getAttachments().get(TraceMessageHeaders.TRACE_ID_NAME)));
                spanBuilder.spanId(Span.hexToId(request.getAttachments().get(TraceMessageHeaders.SPAN_ID_NAME)));
                spanBuilder.exportable(Span.SPAN_SAMPLED.equals(request.getAttachments().get(TraceMessageHeaders.SAMPLED_NAME)));
                spanBuilder.processId(request.getAttachments().get(TraceMessageHeaders.PROCESS_ID_NAME));
                spanBuilder.parent(Span.hexToId(request.getAttachments().get(TraceMessageHeaders.PARENT_ID_NAME)));
                spanBuilder.name(request.getAttachments().get(TraceMessageHeaders.SPAN_NAME_NAME));
                spanBuilder.remote(true);
                parentSpan = spanBuilder.build();
            } else if (request.getAttachments().get(Span.TRACE_ID_NAME) != null &&
                    !"".equals(request.getAttachments().get(Span.TRACE_ID_NAME))) {
                spanBuilder.traceId(Span.hexToId(request.getAttachments().get(Span.TRACE_ID_NAME)));
                spanBuilder.spanId(Span.hexToId(request.getAttachments().get(Span.SPAN_ID_NAME)));
                spanBuilder.exportable(Span.SPAN_SAMPLED.equals(request.getAttachments().get(Span.SAMPLED_NAME)));
                spanBuilder.processId(request.getAttachments().get(Span.PROCESS_ID_NAME));
                if (request.getAttachments().get(Span.PARENT_ID_NAME) != null) {
                    spanBuilder.parent(Span.hexToId(request.getAttachments().get(Span.PARENT_ID_NAME)));
                }
                spanBuilder.name(request.getAttachments().get(Span.SPAN_NAME_NAME));
                spanBuilder.remote(true);
                parentSpan = spanBuilder.build();
            }
        }
        String operationName = buildOperationName(request);
        Span newSpan = tracer.createSpan("motan:" + request.getMethodName(), parentSpan);
        newSpan.tag("motan_method", operationName);

        newSpan.tag("requestId", String.valueOf(request.getRequestId()));
        newSpan.tag(Span.SPAN_LOCAL_COMPONENT_TAG_NAME, MOTAN_TAG);

        return newSpan;

    }

}
