package ru.clevertec.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserApplicationMain {

    public static void main(String[] args) {
        SpringApplication.run(UserApplicationMain.class, args);
    }
}
