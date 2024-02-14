package io.github.matgalv2.githubtools.githubapi;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

import io.github.matgalv2.githubtools.common.Error;


@Data
public class Repository {
    @JsonProperty("name")
    private String name;
    @JsonProperty("owner")
    private Owner owner;
    @JsonProperty("branches_url")
    private String branches_url;
    @JsonProperty("fork")
    private boolean fork;
    @JsonProperty("private")
    private boolean isPrivate;

    private List<Branch> branches;
    private List<Error> errors = List.of();

    @Data
    public static class Owner{
        @JsonProperty("login")
        private String login;
    }
}
