package io.github.matgalv2.githubtools.githubapi;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

import io.github.matgalv2.githubtools.common.Error;


@Data
public class Repository {
    private final String name;
    private final Owner owner;
    @JsonProperty("branches_url")
    private final String branchesUrl;
    private final boolean fork;
    @JsonProperty("private")
    private final boolean isPrivate;

    private final List<Branch> branches;
    private final List<Error> errors;

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

    public Repository withBranchesUrl(String branchesUrl){
        return new Repository(name, owner, branchesUrl, fork, isPrivate, branches, errors);
    }
    public Repository withBranches(List<Branch> branches){
        return new Repository(name, owner, branchesUrl, fork, isPrivate, branches, errors);
    }
    public Repository withErrors(List<Error> errors){
        return new Repository(name, owner, branchesUrl, fork, isPrivate, branches, errors);
    }
}
