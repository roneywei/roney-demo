package config.common;

import com.weibo.api.motan.common.URLParamType;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaoqian on 2016/9/27.
 */
@Order(-1)
@Configuration
public class MotanCommonConfig {


    /**
     * jar包中的@Value值只能方法内获取
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AnnotationBean motanAnnotationBean(@Value("${motan.beans.package:}") String motanBeansPackage) {
        AnnotationBean motanAnnotationBean = new AnnotationBean();
        if (StringUtils.hasLength(motanBeansPackage)) {
            motanAnnotationBean.setPackage(motanBeansPackage);
        }
        if (motanAnnotationBean.getPackage() == null && !"close".equals(motanBeansPackage)) {
            throw new RuntimeException("请配置maton api 包");
        }
        return motanAnnotationBean;
    }

    @Bean(name = "motan")
    @ConditionalOnMissingBean
    public ProtocolConfigBean protocolConfig(@Value("${motan.service.maxWorkerThread:200}") Integer maxWorkerThread,
                                             @Value("${motan.service.minWorkerThread:20}") Integer minWorkerThread) {
        ProtocolConfigBean config = new ProtocolConfigBean();
        config.setDefault(true);
        config.setSerialization("fastjson");
        config.setName("motan");
        config.setMaxContentLength(10485760);
        config.setMaxWorkerThread(maxWorkerThread == null ? 200 : maxWorkerThread);
        config.setMinWorkerThread(minWorkerThread == null ? 20 : minWorkerThread);
        return config;
    }

    @Bean(name = "registry")
    @ConditionalOnMissingBean
    public RegistryConfigBean registryConfigSit(@Value("${motan.zookeeper.host:}") String zookeeperAddress) {

        RegistryConfigBean config = new RegistryConfigBean();
        config.setDefault(true);
        config.setRegProtocol("zookeeper");
        config.setAddress(zookeeperAddress);
        config.setConnectTimeout(30000);
        return config;

    }




}
