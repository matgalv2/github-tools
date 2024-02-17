package io.github.matgalv2.githubtools.controller;


import io.github.matgalv2.githubtools.converter.RepositoryConverter;
import io.github.matgalv2.githubtools.service.GithubExplorerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;


@RestController
@AllArgsConstructor
public class GithubExplorerController {

    private final GithubExplorerService githubExplorerService;
    private final RepositoryConverter converter;

    @GetMapping("/repos/{username}")
    public ResponseEntity<?> getUserRepositories(@PathVariable @NotBlank String username){
        return githubExplorerService.getUserRepositories(username)
                .map(repositories -> repositories.stream().map(converter.repositoryToAPI()::map))
                .fold(
                        error -> ResponseEntity.status(error.getStatus()).body(error),
                        ResponseEntity::ok
                );
    }


}
