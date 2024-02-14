package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.common.Error;
import io.github.matgalv2.githubtools.githubapi.Repository;
import io.vavr.control.Either;

import java.util.List;

public interface GithubExplorerService {
    Either<Error, List<Repository>> findUser(String username);
}
