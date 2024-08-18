package io.github.matgalv2.githubtools.controller;


import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.service.GithubExplorerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;


@RestController
@RequiredArgsConstructor
public class GithubExplorerController {

    private final GithubExplorerService githubExplorerService;
    private final Logger logger = LoggerFactory.getLogger(GithubExplorerController.class);

    @GetMapping("/repos/{username}")
    public Flux<RepositoryDTO> getUserRepositories(@PathVariable String username){
        return githubExplorerService.getUserRepositories(username);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Error> handleApplicationException(WebClientResponseException webClientResponseException) {
        HttpStatusCode code = webClientResponseException.getStatusCode();
        String statusText = webClientResponseException.getStatusText();
        Error error = new Error(code.value(), statusText);
        return new ResponseEntity<>(error, code);
    }

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<Error> handleApplicationException(WebClientException webClientException) {
        logger.error(webClientException.getMessage());
        int code = 500;
        Error error = new Error(code, "Internal server error");
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(code));
    }

}
