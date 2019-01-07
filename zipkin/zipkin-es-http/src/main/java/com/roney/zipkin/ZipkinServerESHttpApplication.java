package com.roney.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin.server.EnableZipkinServer;

/**
 * Created by Roney on 2019/1/4.
 */
@EnableZipkinServer
@SpringBootApplication
public class ZipkinServerESHttpApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZipkinServerESHttpApplication.class,args);
    }
}
