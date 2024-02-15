package io.github.matgalv2.githubtools.githubapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class Branch {
    @JsonProperty("name")
    private String name;
    @JsonProperty("commit")
    private Commit lastCommit;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Commit{
        @JsonProperty("sha")
        private String sha;
    }

    public String getLastCommitSha(){
        return lastCommit.getSha();
    }
}
