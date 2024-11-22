package dev.jarradclark.tflproxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfigTemplate {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
