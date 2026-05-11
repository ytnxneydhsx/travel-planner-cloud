package org.example.itineraryservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ItineraryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItineraryServiceApplication.class, args);
    }
}
