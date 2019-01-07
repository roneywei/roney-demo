package com.roney.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin.server.EnableZipkinServer;


@SpringBootApplication
@EnableZipkinServer
@EnableAutoConfiguration
public class ZipkinMySqlHttpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinMySqlHttpApplication.class, args);
    }

}
