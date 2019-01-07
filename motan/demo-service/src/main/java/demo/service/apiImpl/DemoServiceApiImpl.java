package demo.service.apiImpl;

import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import demo.api.DemoServiceApi;
import lombok.extern.slf4j.Slf4j;

@MotanService
@Slf4j
public class DemoServiceApiImpl implements DemoServiceApi {
    @Override
    public void getDemoService() {
        log.info("DemoServiceApiImpl invoking ...");
    }
}
