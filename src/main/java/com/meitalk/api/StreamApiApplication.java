package com.meitalk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableAspectJAutoProxy
@SpringBootApplication
public class StreamApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamApiApplication.class, args);
    }

}
