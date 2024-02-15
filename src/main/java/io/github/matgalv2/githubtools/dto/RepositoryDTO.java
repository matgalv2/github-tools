package io.github.matgalv2.githubtools.dto;


import io.github.matgalv2.githubtools.common.Error;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class RepositoryDTO {
    private String ownerLogin;
    private String repositoryName;
    private List<BranchDTO> branches;
    private List<Error> errors;

}
