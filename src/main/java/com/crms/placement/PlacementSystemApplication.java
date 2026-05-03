package com.crms.placement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // ✅ enables the @Scheduled jobs
public class PlacementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlacementSystemApplication.class, args);
    }
}