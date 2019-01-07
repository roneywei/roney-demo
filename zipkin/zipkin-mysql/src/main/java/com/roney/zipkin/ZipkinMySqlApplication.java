package com.roney.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;


@SpringBootApplication
@EnableZipkinStreamServer
@EnableAutoConfiguration
public class ZipkinMySqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinMySqlApplication.class, args);
    }

}
