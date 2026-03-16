package com.anotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnotationBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnotationBackendApplication.class, args);
    }
}
