package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.error.ApplicationException;
import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class SimpleGithubExplorerService implements GithubExplorerService {

    private final RestClient client;
    private final ModelMapper mapper;


    @Value("${github.api.endpoint.repositories}")
    private String repositoriesEndpoint;
    @Value("${github.api.endpoint.branches}")
    private String branchEndpoint;
    @Value("${github.api.baseURL}")
    private String baseURL;


    public String getRepositoriesURL(){
        return baseURL + repositoriesEndpoint;
    }

    public String getBranchesURL(){
        return baseURL + branchEndpoint;
    }

    @Override
    public List<RepositoryDTO> getUserRepositories(String username) {
        ParameterizedTypeReference<List<Repository>> typeRef = new ParameterizedTypeReference<>() {};

        List<Repository> response = getRequest(String.format(getRepositoriesURL(), username), typeRef);
        return response.stream()
                .filter(repository -> !repository.isFork() && !repository.isPrivate())
                .peek(repository -> {
                    String url = String.format(getBranchesURL(), repository.getOwner().getLogin(), repository.getName());
                    List<Branch> branches = getBranches(url);
                    repository.setBranches(branches);
                })
                .map(repository -> mapper.map(repository, RepositoryDTO.class))
                .toList();
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
                            throw new ApplicationException(new Error(resp.getStatusCode().value(), resp.getStatusText()));
                    });
        } catch (ResourceAccessException resourceAccessException) {
            Error error = new Error(503, String.format("Could not connect to %s", url));
            throw new ApplicationException(error);
        }
        return response;
    }
}
