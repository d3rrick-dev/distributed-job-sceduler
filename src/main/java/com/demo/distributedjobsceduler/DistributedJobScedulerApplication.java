package com.demo.distributedjobsceduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DistributedJobScedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedJobScedulerApplication.class, args);
    }

}
