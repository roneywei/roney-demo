package demo.service.config;

import config.service.MotanServiceConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;



@ImportAutoConfiguration({
        MotanServiceConfig.class
})
@Configuration
public class AutoConfig {
}
