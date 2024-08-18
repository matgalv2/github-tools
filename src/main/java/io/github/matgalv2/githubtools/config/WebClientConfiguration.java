package io.github.matgalv2.githubtools.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient webClient(@Value("${github.api.baseURL}") String baseUrl){
        return WebClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .build();
    }

}
