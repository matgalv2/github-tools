package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GithubExplorerWebClientService implements GithubExplorerService {

    private final WebClient client;
    private final ModelMapper mapper;


    @Value("${github.api.endpoint.repositories}")
    private String repositoriesEndpoint;
    @Value("${github.api.endpoint.branches}")
    private String branchEndpoint;


    @Override
    public Flux<RepositoryDTO> getUserRepositories(String username) {
        return getRepositories(username).map(repository -> mapper.map(repository, RepositoryDTO.class));
    }

    private Flux<Repository> getRepositories(String username) {
        Flux<Repository> repositories = client
                .get()
                .uri(repositoriesEndpoint, username)
                .retrieve()
                .bodyToFlux(Repository.class)
                .flatMap(repository -> getBranches(username, repository.getName()).flatMap(branches -> Mono.just(repository.withBranches(branches))));
        return repositories;
    }

    private Mono<List<Branch>> getBranches(String username, String repositoryName) {
        ParameterizedTypeReference<List<Branch>> type = new ParameterizedTypeReference<>() {};
        Mono<List<Branch>> branches = client
                .get()
                .uri(branchEndpoint, username, repositoryName)
                .retrieve()
                .bodyToMono(type);
        return branches;
    }
}
