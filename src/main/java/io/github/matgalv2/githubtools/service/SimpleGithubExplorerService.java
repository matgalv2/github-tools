package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.common.Error;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import io.vavr.control.Either;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static io.github.matgalv2.githubtools.common.ErrorMessages.COULD_NOT_CONNECT_TO;
import static io.github.matgalv2.githubtools.common.ErrorMessages.COULD_NOT_GET_BRANCHES;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@Service
public class SimpleGithubExplorerService implements GithubExplorerService {

    private static final String reposURL = "https://api.github.com/users/%s/repos";
    private static final String branchesURL = "https://api.github.com/repos/%s/%s/branches";

    @Override
    public Either<Error, List<Repository>> getUserRepositories(String username) {
        ParameterizedTypeReference<List<Repository>> typeRef = new ParameterizedTypeReference<>() {};
        Either<Error, List<Repository>> response = getRequest(String.format(reposURL, username), typeRef);

        // There is no need to filter public repositories, because unauthorized requests get only public repositories.
        response
                .map(repositories -> repositories.stream().filter(repository -> !repository.isFork()).toList())
                .forEach(repositories -> repositories.forEach(repository -> {
                    String url = String.format(branchesURL, repository.getOwner().getLogin(), repository.getName());
                    repository.setBranchesUrl(url);
                    Either<Error, List<Branch>> branches = getBranches(repository.getBranchesUrl(), repository.getName());
                    if (branches.isLeft())
                        repository.setErrors(List.of(branches.getLeft()));
                    else
                        repository.setBranches(branches.get());
                }));
        return response;
    }

    private Either<Error, List<Branch>> getBranches(String branchesURL, String repositoryName) {
        ParameterizedTypeReference<List<Branch>> typeRef = new ParameterizedTypeReference<>() {};
        Either<Error, List<Branch>> response = getRequest(branchesURL, typeRef)
                .mapLeft(error ->
                        new Error(
                                error.getStatus(),
                                String.format(COULD_NOT_GET_BRANCHES, repositoryName, error.getMessage()))
                );
        return response;
    }

    private <T> Either<Error, T> getRequest(String url, ParameterizedTypeReference<T> typeRef) {
        RestClient client = RestClient
                .builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(url)
                .build();

        Either<Error, T> response;

        try {
            response = client
                    .get()
                    .accept(APPLICATION_JSON)
                    .exchange((req, resp) -> {
                        if (resp.getStatusCode().is4xxClientError())
                            return Either.left(new Error(resp.getStatusCode().value(), resp.getStatusText()));
                        else
                            return Either.right(resp.bodyTo(typeRef));
                    });
        } catch (Exception exception) {
            response = Either.left(new Error(500, String.format(COULD_NOT_CONNECT_TO, url)));
        }
        return response;
    }

}
