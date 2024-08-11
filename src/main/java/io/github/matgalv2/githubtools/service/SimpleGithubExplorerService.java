package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


@Service
@RequiredArgsConstructor
public class SimpleGithubExplorerService implements GithubExplorerService {

    private final WebClient client;
    private final ModelMapper mapper;


    @Value("${github.api.endpoint.repositories}")
    private String repositoriesEndpoint;
    @Value("${github.api.endpoint.branches}")
    private String branchEndpoint;


    @Override
    public Flux<RepositoryDTO> getUserRepositories(String username) {
        String repositoriesURI = String.format(repositoriesEndpoint, username);
        Flux<RepositoryDTO> response = client
                .get()
                .uri(repositoriesURI)
                .retrieve()
                .bodyToFlux(Repository.class)
                .map(repository -> {
                    Flux<Branch> branches = getBranches(username, repository.getName());
                    return repository.withBranches(branches);
                })
                .map(repository -> mapper.map(repository, RepositoryDTO.class));
        return response;
    }

    private Flux<Branch> getBranches(String username, String repositoryName) {
        String branchesURI = String.format(branchEndpoint, username, repositoryName);
        Flux<Branch> branches = client
                .get()
                .uri(branchesURI)
                .retrieve()
                .bodyToFlux(Branch.class);
        return branches;
    }

}
