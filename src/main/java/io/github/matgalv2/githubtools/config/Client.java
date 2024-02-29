package io.github.matgalv2.githubtools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class Client {

    @Bean
    public RestClient restClient(){
        return RestClient
                .builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .build();
    }

}
