package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.error.CouldNotConnectToGithubApi;
import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.error.ServiceException;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static java.lang.StringTemplate.STR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class SimpleGithubExplorerService implements GithubExplorerService {

    private final RestClient client;

    @Value("${github-repositoriesURL}")
    private String repositoriesURL;

    @Value("${github-branchesURL}")
    private String branchesURL;

    @Override
    public List<Repository> getUserRepositories(String username) {
        ParameterizedTypeReference<List<Repository>> typeRef = new ParameterizedTypeReference<>() {};

        List<Repository> response = getRequest(String.format(repositoriesURL, username), typeRef);
        return response
                .stream().filter(repository -> !repository.isFork())
                .map(repository -> {
                    String url = String.format(branchesURL, repository.getOwner().getLogin(), repository.getName());
                    return repository.withBranchesUrl(url);
                })
                .map(repository -> {
                    List<Branch> branches = getBranches(repository.getBranchesUrl(), repository.getName());
                    return repository.withBranches(branches);
                }).toList();

    }

    private List<Branch> getBranches(String branchesURL, String repositoryName) {
        ParameterizedTypeReference<List<Branch>> typeRef = new ParameterizedTypeReference<>() {};
        try{
            return getRequest(branchesURL, typeRef);
        }
        catch (ServiceException exception){
            String message = STR."Could not get branches for repository \{repositoryName} due to error: \{exception.getError().getMessage()}";
            Error error = new Error(exception.getError().getStatus(), message);
            throw new ServiceException(error);
        }
    }

    private <T> T getRequest(String url, ParameterizedTypeReference<T> typeRef) {
        T response;

        try {
            response = client
                    .get()
                    .uri(URI.create(url))
                    .accept(APPLICATION_JSON)
                    .exchange((_, resp) -> {
                        if (resp.getStatusCode().is4xxClientError())
                            throw new ServiceException(new Error(resp.getStatusCode().value(), resp.getStatusText()));
                        else
                            return resp.bodyTo(typeRef);
                    });
        } catch (ServiceException error) {
            throw new ServiceException(error.getError());
        } catch (Exception exception) {
            String message = STR."Could not connect to \{url}";
            throw new CouldNotConnectToGithubApi(message);
        }
        return response;
    }

}
