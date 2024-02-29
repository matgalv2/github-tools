package io.github.matgalv2.githubtools.githubapi;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

import io.github.matgalv2.githubtools.error.Error;


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

    @JsonCreator
    public Repository(String name, Owner owner, String branchesUrl, boolean fork, boolean isPrivate, List<Branch> branches){
        this.name = name;
        this.owner = owner;
        this.branchesUrl = branchesUrl;
        this.fork = fork;
        this.isPrivate = isPrivate;
        this.branches = branches == null ? List.of() : branches;
    }

    public Repository(Repository that){
        this.name = that.name;
        this.owner = that.owner;
        this.branchesUrl = that.branchesUrl;
        this.fork = that.fork;
        this.isPrivate = that.isPrivate;
        this.branches = that.branches;
    }

    public Repository withBranchesUrl(String branchesUrl){
        return new Repository(name, owner, branchesUrl, fork, isPrivate, branches);
    }
    public Repository withBranches(List<Branch> branches){
        return new Repository(name, owner, branchesUrl, fork, isPrivate, branches);
    }
}
