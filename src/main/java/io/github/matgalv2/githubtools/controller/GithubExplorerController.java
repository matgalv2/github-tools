package io.github.matgalv2.githubtools.controller;


import io.github.matgalv2.githubtools.dto.BranchDTO;
import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import io.github.matgalv2.githubtools.service.GithubExplorerService;
import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;


@Controller
@AllArgsConstructor
public class GithubExplorerController {

    private final GithubExplorerService githubExplorerService;
    private final ModelMapper mapper;

    @GetMapping("/repos/{username}")
    public ResponseEntity<?> getUserRepositories(@PathVariable @NotBlank String username){
        return githubExplorerService
                .getUserRepositories(username)
                .map(repositories -> repositories.stream().map(repositoryToAPI()::map))
                .fold(
                        error -> ResponseEntity.status(error.getStatus()).body(error),
                        ResponseEntity::ok
                );

    }

    private TypeMap<Repository, RepositoryDTO> repositoryToAPI() {
        Converter<Repository, RepositoryDTO> converter = mappingContext -> {
            RepositoryDTO destination = mappingContext.getDestination();
            destination.setBranches(mappingContext.getSource().getBranches().stream().map(branchToAPI()::map).toList());
            return destination;
        };

        return mapper
                .typeMap(Repository.class, RepositoryDTO.class)
                .addMapping(Repository::getName, RepositoryDTO::setRepositoryName)
                .setPostConverter(converter);
    }

    private TypeMap<Branch, BranchDTO> branchToAPI() {
        return mapper
                .typeMap(Branch.class, BranchDTO.class)
                .addMapping(Branch::getLastCommitSha, BranchDTO::setLastCommit);
    }


}
