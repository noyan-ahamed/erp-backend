package com.erp;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Base64;

@SpringBootApplication
//this is for sending mail in another thread
@EnableAsync
public class EnterpriseResourcePlanningApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseResourcePlanningApplication.class, args);
    }

}
