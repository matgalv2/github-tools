package io.github.matgalv2.githubtools.controller;


import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.service.GithubExplorerService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


@RestController
@AllArgsConstructor
public class GithubExplorerController {

    private final GithubExplorerService githubExplorerService;
    private final ModelMapper mapper;

    /**
     * TODO:
     *      1. Error handling
     *      2. Nie zwracać Response entity (znak zapytania nie nadaje się na produkcję)
     *      3. Wykorzystać rekordy!!! Zapewnić niemutowalność
     *      4. RestClient powinien być przekazywany jako bean
     *      5. Poprawić model mappery
     */

    @GetMapping("/repos/{username}")
    public ResponseEntity<?> getUserRepositories(@PathVariable @NotEmpty String username){
        return githubExplorerService.getUserRepositories(username)
                .map(repositories -> repositories.stream().map(repository -> mapper.map(repository, RepositoryDTO.class)))
                .fold(
                        error -> ResponseEntity.status(error.getStatus()).body(error),
                        ResponseEntity::ok
                );
    }


}
