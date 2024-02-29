package io.github.matgalv2.githubtools.config;

import io.github.matgalv2.githubtools.dto.BranchDTO;
import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonMapper {

    private final ModelMapper mapper;

    public CommonMapper(){
        mapper = new ModelMapper();
        mapper.typeMap(Branch.class, BranchDTO.class).addMapping(Branch::getLastCommitSha, BranchDTO::setLastCommit);
        mapper.typeMap(Repository.class, RepositoryDTO.class).addMapping(Repository::getName, RepositoryDTO::setRepositoryName);
    }

    @Bean
    public ModelMapper mapper() {
        return mapper;
    }
}
