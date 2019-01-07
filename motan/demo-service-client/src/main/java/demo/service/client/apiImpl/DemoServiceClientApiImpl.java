package demo.service.client.apiImpl;

import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import demo.api.DemoServiceApi;
import demo.api.DemoServiceClientApi;
import lombok.extern.slf4j.Slf4j;

@MotanService
@Slf4j
public class DemoServiceClientApiImpl implements DemoServiceClientApi {
    @MotanReferer
    DemoServiceApi demoServiceApi;
    @Override
    public void getDemoServiceClient() {
        log.info("DemoServiceApiImpl invoking ...");

        log.info("start  demoServiceApi invoking ...");
        demoServiceApi.getDemoService();

        log.info("end  demoServiceApi invoking ...");
    }
}
