package config.service;

import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import com.weibo.api.motan.filter.sleuth.SleuthTracerFactory;
import com.weibo.api.motan.filter.sleuth.SleuthTracingContext;
import config.common.MotanCommonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;


/**
 * Created by xiaoqian on 2016/9/27.
 */

@ImportAutoConfiguration(
        MotanCommonConfig.class
)
@Configuration
public class MotanServiceConfig {

    /**
     * 基础服务端配置
     *
     * @param port
     * @return
     */
    @Bean(name = "motanServerBasicConfig")
    @ConditionalOnMissingBean
    public BasicServiceConfigBean baseServiceConfig(@Value("${motan.export.port}") String port,
                                                    @Value("${motan.service.group:sinomall}") String motanServerGroup,
                                                    @Value("${motan.service.access.log:false}") Boolean motanServerAccessLog, @Value("${spring.sleuth.enabled:false}") Boolean tracing) {
        BasicServiceConfigBean config = new BasicServiceConfigBean();
        config.setDefault(true);
        config.setExport("motan:" + port);
        if (StringUtils.isEmpty(motanServerGroup)) {
            config.setGroup("sinomall");
        } else {
            config.setGroup(motanServerGroup);
        }
        if (StringUtils.isEmpty(motanServerAccessLog)) {
            config.setAccessLog(true);
        } else {
            config.setAccessLog(motanServerAccessLog);
        }
        config.setShareChannel(true);
        config.setRequestTimeout(60 * 1000);
        config.setRegistry("registry");
        if(tracing){
            config.setFilter("sleuth-tracing");
        }
        return config;
    }

    @Bean
    @ConditionalOnExpression(value = "${spring.sleuth.enabled:false}")
    SleuthTracingContext sleuthTracingContext(@Autowired(required = false)  org.springframework.cloud.sleuth.Tracer tracer){
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
