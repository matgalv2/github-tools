package io.github.matgalv2.githubtools.githubapi;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public record Commit(String sha) {

    @JsonCreator
    public Commit(@JsonProperty("sha") String sha) {
        this.sha = sha;
    }
}
