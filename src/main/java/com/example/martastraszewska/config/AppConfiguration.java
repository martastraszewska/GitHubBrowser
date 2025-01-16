package com.example.martastraszewska.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {
    @Bean("githubRestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplateBuilder()
                .interceptors((httpRequest, bytes, clientHttpRequestExacution) -> {
                    httpRequest.getHeaders().set("Accept", "application/vnd.github+json");
                    httpRequest.getHeaders().set("X-GitHub-Api-Version", "2022-11-28");
                    return clientHttpRequestExacution.execute(httpRequest, bytes);
                })
                .rootUri("https://api.github.com").build();
    }
    @Bean
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    private RestTemplateBuilder getRestTemplateBuilder(String userAgent) {
        return new RestTemplateBuilder().
                interceptors((httpRequest, bytes, clientHttpRequestExecution) -> {
                    httpRequest.getHeaders().set("User-Agent", userAgent);
                    httpRequest.getHeaders().set("Accept", "application/vnd.github.v3+json");
                    return clientHttpRequestExecution.execute(httpRequest, bytes);
                });
    }
}
