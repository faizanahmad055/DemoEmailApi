package com.myemail.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class DemoEmailApi {
    public static void main(final String[] args) {
        SpringApplication.run(DemoEmailApi.class, args);
    }
}
