package demo.service.client.config;


import config.client.MotanClientConfig;
import config.service.MotanServiceConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;


@ImportAutoConfiguration({
        MotanClientConfig.class,
        MotanServiceConfig.class
})
@Configuration
public class AutoConfig {
}
