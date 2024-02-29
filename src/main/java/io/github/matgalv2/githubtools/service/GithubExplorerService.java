package io.github.matgalv2.githubtools.service;


import io.github.matgalv2.githubtools.error.Error;
import io.github.matgalv2.githubtools.githubapi.Repository;
import io.vavr.control.Either;

import java.util.List;

public interface GithubExplorerService {
    List<Repository> getUserRepositories(String username);
}
