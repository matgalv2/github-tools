package io.github.matgalv2.githubtools.service;


import io.github.matgalv2.githubtools.dto.RepositoryDTO;

import java.util.List;

public interface GithubExplorerService {
    List<RepositoryDTO> getUserRepositories(String username);
}
