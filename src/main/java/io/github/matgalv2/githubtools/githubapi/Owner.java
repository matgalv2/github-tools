package io.github.matgalv2.githubtools.githubapi;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Owner(String login) {

    @JsonCreator
    public Owner(@JsonProperty("login") String login) {
        this.login = login;
    }
}