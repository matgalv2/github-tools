package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.common.Error;
import io.github.matgalv2.githubtools.githubapi.Repository;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.vavr.control.Either;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;


@Service
public class SimpleGithubExplorerService implements GithubExplorerService {

    private static final String reposURL = "https://api.github.com/users/%s/repos";
    private static final String branchesURL = "https://api.github.com/repos/%s/%s/branches";

    @Override
    public Either<Error, List<Repository>> findUser(String username) {
        return getUsersRepositories(username);
    }

    private Either<Error, List<Repository>> getUsersRepositories(String username) {
        ParameterizedTypeReference<List<Repository>> typeRef = new ParameterizedTypeReference<>() {};
        Either<Error, List<Repository>> response = getRequest(String.format(reposURL, username), typeRef);

        response.forEach(repositories -> repositories.forEach(repository -> {
            String url = String.format(branchesURL, repository.getOwner().getLogin(), repository.getName());
            repository.setBranches_url(url);
            Either<Error, List<Branch>> branches = getBranches(repository.getBranches_url());
            repository.setBranches(branches.getOrElse(List.of()));
        }));
        return response;
    }

    private Either<Error, List<Branch>> getBranches(String branchesURL){
        ParameterizedTypeReference<List<Branch>> typeRef = new ParameterizedTypeReference<>() {};
        return getRequest(branchesURL, typeRef);
    }

    private <T> Either<Error, T> getRequest(String url, ParameterizedTypeReference<T> typeRef){
        RestClient client = RestClient
                .builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(url)
                .build();

        return client
                .get()
                .accept(APPLICATION_JSON)
                .exchange((req, resp) -> {
                    if (resp.getStatusCode().is4xxClientError())
                        return Either.left(new Error(resp.getStatusCode().value(), resp.getStatusText()));
                    else
                        return Either.right(resp.bodyTo(typeRef));
                });
    }

}
