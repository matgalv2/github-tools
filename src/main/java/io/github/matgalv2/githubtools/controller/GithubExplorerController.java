package io.github.matgalv2.githubtools.controller;


import feign.FeignException;
import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.error.ApplicationException;
import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.service.GithubExplorerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Error> handleApplicationException(FeignException feignException) {
        HttpStatusCode code = HttpStatusCode.valueOf(feignException.status());

        Pattern pattern = Pattern.compile("\"message\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(feignException.getMessage());

        String message = matcher.find() ? matcher.group(1) : feignException.getMessage();
        Error error = new Error(feignException.status(), message);
        return new ResponseEntity<>(error, code);
    }

}
