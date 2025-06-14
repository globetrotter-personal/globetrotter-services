package com.globetrotter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.globetrotter")
public class GlobetrotterServicesApplication {
    public static void main(String[] args) {
        SpringApplication.run(GlobetrotterServicesApplication.class, args);
    }
}
