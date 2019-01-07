package demo.client.config;


import config.client.MotanClientConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;



@ImportAutoConfiguration({
        MotanClientConfig.class
})
@Configuration
public class AutoConfig {
}
