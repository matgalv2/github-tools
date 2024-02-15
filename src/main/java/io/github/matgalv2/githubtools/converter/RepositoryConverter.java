package io.github.matgalv2.githubtools.converter;

import io.github.matgalv2.githubtools.dto.BranchDTO;
import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class RepositoryConverter {
    private final ModelMapper mapper;

    public TypeMap<Repository, RepositoryDTO> repositoryToAPI() {
        Converter<Repository, RepositoryDTO> converter = mappingContext -> {
            RepositoryDTO destination = mappingContext.getDestination();
            destination.setBranches(mappingContext.getSource().getBranches().stream().map(branchToAPI()::map).toList());
            return destination;
        };

        return mapper
                .typeMap(Repository.class, RepositoryDTO.class)
                .addMapping(Repository::getName, RepositoryDTO::setRepositoryName)
                .setPostConverter(converter);
    }

    private TypeMap<Branch, BranchDTO> branchToAPI() {
        return mapper
                .typeMap(Branch.class, BranchDTO.class)
                .addMapping(Branch::getLastCommitSha, BranchDTO::setLastCommit);
    }
}
