package io.github.matgalv2.githubtools.service;


import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GithubExplorerService {
    Flux<RepositoryDTO> getUserRepositories(String username);
}
