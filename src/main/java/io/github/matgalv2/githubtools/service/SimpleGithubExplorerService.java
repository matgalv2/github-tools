package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.error.CouldNotConnectToGithubApi;
import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.error.ServiceException;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class SimpleGithubExplorerService implements GithubExplorerService {

    private final RestClient client;
    private final String repositoriesURL;
    private final String branchesURL;


    public SimpleGithubExplorerService(@Value("${github.api.baseURL}") String baseURL,
                                       @Value("${github.api.endpoint.branches}") String branchesEndpoint,
                                       @Value("${github.api.endpoint.repositories}") String repositoriesEndpoint,
                                       RestClient client) {
        this.client = client;
        this.repositoriesURL = baseURL + repositoriesEndpoint;
        this.branchesURL = baseURL + branchesEndpoint;
    }

    @Override
    public List<Repository> getUserRepositories(String username) {
        ParameterizedTypeReference<List<Repository>> typeRef = new ParameterizedTypeReference<>() {};

        List<Repository> response = getRequest(String.format(repositoriesURL, username), typeRef);
        return response.stream()
                .filter(repository -> !repository.isFork() && !repository.isPrivate())
                .map(repository -> {
                    String url = String.format(branchesURL, repository.getOwner().getLogin(), repository.getName());
                    return repository.withBranchesUrl(url);
                })
                .map(repository -> {
                    List<Branch> branches = getBranches(repository.getBranchesUrl());
                    return repository.withBranches(branches);
                }).toList();
    }

    private List<Branch> getBranches(String branchesURL) {
        ParameterizedTypeReference<List<Branch>> typeRef = new ParameterizedTypeReference<>() {};
        return getRequest(branchesURL, typeRef);
    }

    private <T> T getRequest(String url, ParameterizedTypeReference<T> typeRef) {
        T response;

        try {
            response = client
                    .get()
                    .uri(URI.create(url))
                    .accept(APPLICATION_JSON)
                    .exchange((_, resp) -> {
                        if (resp.getStatusCode().is2xxSuccessful())
                            return resp.bodyTo(typeRef);
                        else
                            throw new ServiceException(new Error(resp.getStatusCode().value(), resp.getStatusText()));
                    });
        } catch (ResourceAccessException resourceAccessException) {
            String message = String.format("Could not connect to %s", url);
            throw new CouldNotConnectToGithubApi(message);
        }
        return response;
    }
}
