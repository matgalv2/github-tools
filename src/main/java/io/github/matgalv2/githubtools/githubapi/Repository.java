package io.github.matgalv2.githubtools.githubapi;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;



@Getter
public class Repository {
    private final String name;
    private final Owner owner;
    private final boolean fork;
    @JsonProperty("private")
    private final boolean isPrivate;

    @Setter
    private List<Branch> branches;

    @JsonCreator
    public Repository(String name, Owner owner, boolean fork, boolean isPrivate, List<Branch> branches){
        this.name = name;
        this.owner = owner;
        this.fork = fork;
        this.isPrivate = isPrivate;
        this.branches = branches == null ? List.of() : branches;
    }

    public String getOwnerLogin() {
        return owner.login();
    }
}
