package io.github.matgalv2.githubtools.controller;


import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.error.ApplicationException;
import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.service.GithubExplorerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class GithubExplorerController {

    private final GithubExplorerService githubExplorerService;

    @GetMapping("/repos/{username}")
    public List<RepositoryDTO> getUserRepositories(@PathVariable String username){
        return githubExplorerService.getUserRepositories(username);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Error> handleApplicationException(ApplicationException applicationException) {
        HttpStatusCode code = HttpStatusCode.valueOf(applicationException.getError().getStatus());
        return new ResponseEntity<>(applicationException.getError(), code);
    }

}
