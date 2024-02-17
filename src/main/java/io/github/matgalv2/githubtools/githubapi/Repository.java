package io.github.matgalv2.githubtools.githubapi;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

import io.github.matgalv2.githubtools.common.Error;


@Data
public class Repository {
    private String name;
    private Owner owner;
    @JsonProperty("branches_url")
    private String branchesUrl;
    private boolean fork;
    @JsonProperty("private")
    private boolean isPrivate;

    private List<Branch> branches;
    private List<Error> errors;

    @JsonCreator
    public Repository(String name, Owner owner, String branchesUrl, boolean fork, boolean isPrivate, List<Branch> branches, List<Error> errors){
        this.name = name;
        this.owner = owner;
        this.branchesUrl = branchesUrl;
        this.fork = fork;
        this.isPrivate = isPrivate;
        this.branches = branches == null ? List.of() : branches;
        this.errors = errors == null ? List.of() : errors;
    }
}
