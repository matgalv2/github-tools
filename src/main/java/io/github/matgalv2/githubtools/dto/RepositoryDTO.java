package io.github.matgalv2.githubtools.dto;


import io.github.matgalv2.githubtools.error.Error;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryDTO {
    private String ownerLogin;
    private String repositoryName;
    private List<BranchDTO> branches;

}
