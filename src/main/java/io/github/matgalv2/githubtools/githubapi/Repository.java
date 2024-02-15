package io.github.matgalv2.githubtools.githubapi;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import io.github.matgalv2.githubtools.common.Error;
import lombok.NoArgsConstructor;


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
    private List<Error> errors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Owner{
        @JsonProperty("login")
        private String login;
    }

    @JsonCreator
    public Repository(String name, Owner owner, String branches_url, boolean fork, boolean isPrivate, List<Branch> branches, List<Error> errors){
        this.name = name;
        this.owner = owner;
        this.branches_url = branches_url;
        this.fork = fork;
        this.isPrivate = isPrivate;
        this.branches = branches == null ? List.of() : branches;
        this.errors = errors == null ? List.of() : errors;
    }
}
