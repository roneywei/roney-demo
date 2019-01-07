package com.roney.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

/**
 * Created by Roney on 2019/1/4.
 */
@EnableZipkinStreamServer
@SpringBootApplication
public class ZipkinServerESApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZipkinServerESApplication.class,args);
    }
}
