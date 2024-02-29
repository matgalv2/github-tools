package io.github.matgalv2.githubtools.converter;


import io.github.matgalv2.githubtools.dto.BranchDTO;
import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Commit;
import io.github.matgalv2.githubtools.githubapi.Owner;
import io.github.matgalv2.githubtools.githubapi.Repository;
import io.github.matgalv2.githubtools.error.Error;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@RunWith(SpringRunner.class)
public class RepositoryConverterTest {

    @Autowired
    private ModelMapper mapper;

    public Repository createRepository(){
        Owner owner = new Owner("login");
        Commit commit = new Commit("lastCommitSha");
        Branch branch = new Branch("branch", commit);
        return new Repository("test", owner, "branchesUrl", false, false, List.of(branch));
    }

    @Test
    public void correctConversion(){
        BranchDTO expectedBranch = new BranchDTO("branch", "lastCommitSha");
        Error error = new Error(400, "error");
        RepositoryDTO expected = new RepositoryDTO("login", "test", List.of(expectedBranch), List.of(error));
        RepositoryDTO converted = mapper.map(createRepository(), RepositoryDTO.class);

        assertEquals(expected.getRepositoryName(), converted.getRepositoryName());
        assertEquals(expected.getOwnerLogin(), converted.getOwnerLogin());
        assertEquals(expected.getBranches(), converted.getBranches());
        assertEquals(expected.getErrors(), converted.getErrors());
    }
}
