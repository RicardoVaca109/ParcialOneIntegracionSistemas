package com.ecologistics.eco_logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcoLogisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcoLogisticsApplication.class, args);
        System.out.println("[INFO] API iniciada en puerto 8081.");
    }
}
