package io.github.matgalv2.githubtools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GithubToolsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GithubToolsApplication.class, args);
    }

}
