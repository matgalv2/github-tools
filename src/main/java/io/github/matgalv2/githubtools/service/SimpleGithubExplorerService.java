package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.error.CouldNotConnectToGithubApi;
import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.error.ServiceException;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;


@Service
@RequiredArgsConstructor
public class SimpleGithubExplorerService implements GithubExplorerService {

    private final RestClient client;

    private static final String reposURL = "https://api.github.com/users/%s/repos";
    private static final String branchesURL = "https://api.github.com/repos/%s/%s/branches";


    @Override
    public List<Repository> getUserRepositories(String username) {
        ParameterizedTypeReference<List<Repository>> typeRef = new ParameterizedTypeReference<>() {};

        List<Repository> response = getRequest(String.format(reposURL, username), typeRef);
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
            String message = String.format("Could not get branches for repository %s due to error: %s", repositoryName, exception.getError().getMessage());
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
                    .exchange((req, resp) -> {
                        if (resp.getStatusCode().is4xxClientError())
                            throw new ServiceException(new Error(resp.getStatusCode().value(), resp.getStatusText()));
                        else
                            return resp.bodyTo(typeRef);
                    });
        } catch (ServiceException error) {
            throw new ServiceException(error.getError());
        } catch (Exception exception) {
            String message = String.format("Could not connect to %s", url);
            throw new CouldNotConnectToGithubApi(message);
        }
        return response;
    }

}
