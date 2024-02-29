package io.github.matgalv2.githubtools.githubapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Branch {
    private final String name;
    @JsonProperty("commit")
    private final Commit lastCommit;

    public String getLastCommitSha() {
        return lastCommit.getSha();
    }
}
