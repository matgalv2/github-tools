package io.github.matgalv2.githubtools.service;

import io.github.matgalv2.githubtools.client.GithubClient;
import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.githubapi.Branch;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableFeignClients(clients = GithubClient.class)
public class GithubExplorerFeignClientService implements GithubExplorerService {

    private final GithubClient githubClient;
    private final ModelMapper modelMapper;


    @Override
    public List<RepositoryDTO> getUserRepositories(String username) {
        return githubClient.getRepositories(username)
                .stream()
                .peek(repository -> {
                    List<Branch> branches = githubClient.getBranches(username, repository.getName());
                    repository.setBranches(branches);
                })
                .map(repository -> modelMapper.map(repository, RepositoryDTO.class))
                .toList();
    }
}