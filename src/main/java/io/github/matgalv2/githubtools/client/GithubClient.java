package io.github.matgalv2.githubtools.client;

import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "githubAPI", url = "${github.api.baseURL}")
public interface GithubClient {
    @RequestMapping(method = RequestMethod.GET, path = "/users/{username}/repos")
    List<Repository> getRepositories(@PathVariable("username") String username);

    @RequestMapping(method = RequestMethod.GET, value = "/repos/{username}/{repository}/branches")
    List<Branch> getBranches(@PathVariable("username") String username, @PathVariable("repository") String repositoryName);

}
