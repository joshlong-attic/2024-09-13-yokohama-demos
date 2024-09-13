package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}

// demo by Cora Iberkleid (@coraiberkleid)

@Controller
@ResponseBody
class DemoController {

    private final RestClient http;

    DemoController(RestClient http) {
        this.http = http;
    }

    @GetMapping("/wait")
    String delay() {
        return this.http
                .get()
                .uri("https://httpbin.org/delay/5")
                .retrieve()
                .body(String.class);
    }
}