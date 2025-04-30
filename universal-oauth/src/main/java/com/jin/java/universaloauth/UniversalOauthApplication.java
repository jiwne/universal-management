package com.jin.java.universaloauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UniversalOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniversalOauthApplication.class, args);
    }

}
