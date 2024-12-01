package com.example.azure.web;

import com.google.gson.GsonBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AzureWebDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzureWebDemoApplication.class, args);
    }

    @Bean
    public GsonBuilder gsonBuilder() {
        return new GsonBuilder();
    }

}
