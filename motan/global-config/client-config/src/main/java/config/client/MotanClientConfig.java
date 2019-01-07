package config.client;

import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import com.weibo.api.motan.filter.sleuth.SleuthTracerFactory;
import com.weibo.api.motan.filter.sleuth.SleuthTracingContext;
import config.common.MotanCommonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;


/**
 * Created by xiaoqian on 2016/9/27.
 */
@ImportAutoConfiguration(MotanCommonConfig.class)
@Configuration
public class MotanClientConfig {

    @Bean(name = "motanClientBasicConfig")
    public BasicRefererConfigBean baseRefererConfig(@Value("${motan.client.group:sinomall}")
                                                            String motanClientGroup, @Value("${motan.client.access.log:false}") Boolean motanClientAccessLog, @Value("${spring.sleuth.enabled:false}") Boolean tracing) {
        BasicRefererConfigBean config = new BasicRefererConfigBean();
        config.setProtocol("motan");
        if (StringUtils.isEmpty(motanClientGroup)) {
            config.setGroup("sinomall");
        } else {
            config.setGroup(motanClientGroup);
        }
        if (StringUtils.isEmpty(motanClientAccessLog)) {
            config.setAccessLog(false);
        } else {
            config.setAccessLog(motanClientAccessLog);
        }
        config.setRegistry("registry");
        config.setCheck(false);
        config.setRequestTimeout(360 * 1000);
        config.setThrowException(true);
        config.setDefault(true);
        if (tracing) {
            config.setFilter("sleuth-tracing");
        }
        return config;
    }

    /**
     * 不进行重试的Motan客户端配置
     *
     * @return
     */
    @Bean(name = "motanClientFastFailConfig")
    public BasicRefererConfigBean motanClientFastFailConfig(@Value("${motan.client.group:sinomall}")
                                                                    String motanClientGroup, @Value("${motan.client.access.log:false}") Boolean motanClientAccessLog, @Value("${spring.sleuth.enabled:false}") Boolean tracing) {
        BasicRefererConfigBean config = new BasicRefererConfigBean();
        config.setProtocol("motan");
        if (StringUtils.isEmpty(motanClientGroup)) {
            config.setGroup("sinomall");
        } else {
            config.setGroup(motanClientGroup);
        }
        if (StringUtils.isEmpty(motanClientAccessLog)) {
            config.setAccessLog(false);
        } else {
            config.setAccessLog(motanClientAccessLog);
        }
        config.setRegistry("registry");
        config.setCheck(false);
        config.setRequestTimeout(60 * 1000);
        config.setThrowException(true);
        config.setDefault(true);
        if (tracing) {
            config.setFilter("sleuth-tracing");
        }
        return config;
    }

    @Bean
    @ConditionalOnExpression(value = "${spring.sleuth.enabled:false}")
    SleuthTracingContext sleuthTracingContext(@Autowired(required = false) org.springframework.cloud.sleuth.Tracer tracer) {
        SleuthTracingContext context = new SleuthTracingContext();
        context.setTracerFactory(new SleuthTracerFactory() {
            @Override
            public org.springframework.cloud.sleuth.Tracer getTracer() {
                return tracer;
            }
        });

        return context;
    }

}
