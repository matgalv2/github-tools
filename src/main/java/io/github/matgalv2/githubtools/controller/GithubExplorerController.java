package io.github.matgalv2.githubtools.controller;


import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.error.ApplicationError;
import io.github.matgalv2.githubtools.error.CouldNotConnectToGithubApi;
import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.error.ServiceException;
import io.github.matgalv2.githubtools.service.GithubExplorerService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
public class GithubExplorerController {

    private final GithubExplorerService githubExplorerService;
    private final ModelMapper mapper;

    /**
     * TODO:
     *      1. Error handling
     *      2. Nie zwracać Response entity (znak zapytania nie nadaje się na produkcję)
     *      3. Wykorzystać rekordy!!! Zapewnić niemutowalność ✓
     *      4. RestClient powinien być przekazywany jako bean ✓
     *      5. Poprawić model mappery ✓
     */

    @GetMapping("/repos/{username}")
    public List<RepositoryDTO> getUserRepositories(@PathVariable String username){
        return githubExplorerService.getUserRepositories(username)
                .stream().map(repository -> mapper.map(repository, RepositoryDTO.class)).toList();
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Error> handleServiceException(ServiceException serviceException) {
        HttpStatusCode code = HttpStatusCode.valueOf(serviceException.getError().getStatus());
        return new ResponseEntity<>(serviceException.getError(), code);
    }

    @ExceptionHandler(CouldNotConnectToGithubApi.class)
    public ResponseEntity<Error> handleConnectionException(CouldNotConnectToGithubApi couldNotConnectToGithubApi) {
        HttpStatusCode code = HttpStatusCode.valueOf(couldNotConnectToGithubApi.getError().getStatus());
        return new ResponseEntity<>(couldNotConnectToGithubApi.getError(), code);
    }

}
