package demo.client.controller;

import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import demo.api.DemoServiceApi;
import demo.api.DemoServiceClientApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoClientController {


    @MotanReferer
    DemoServiceApi demoServiceApi;

    @MotanReferer
    DemoServiceClientApi demoServiceClientApi;



    @RequestMapping("/getDemoService")
    public Map getDemoService() {

        demoServiceApi.getDemoService();
        return null;
    }

    @RequestMapping("/getDemoServiceClient")
    public Map getDemoServiceClient() {

        demoServiceClientApi.getDemoServiceClient();
        return null;
    }

}
