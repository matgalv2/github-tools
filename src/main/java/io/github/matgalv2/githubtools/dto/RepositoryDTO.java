package io.github.matgalv2.githubtools.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RepositoryDTO {
    private String ownerLogin;
    private String name;
    private List<BranchDTO> branches;

}
