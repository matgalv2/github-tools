package io.github.matgalv2.githubtools.converter;


import io.github.matgalv2.githubtools.dto.BranchDTO;
import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import io.github.matgalv2.githubtools.githubapi.Branch;
import io.github.matgalv2.githubtools.githubapi.Repository;
import io.github.matgalv2.githubtools.common.Error;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@RunWith(SpringRunner.class)
public class RepositoryConverterTest {

    @Autowired
    private RepositoryConverter converter;


    public Repository createRepository(){
        Repository.Owner owner = new Repository.Owner("login");
        Branch.Commit commit = new Branch.Commit("lastCommitSha");
        Branch branch = new Branch("branch", commit);
        Error error = new Error(400, "error");
        return new Repository("test", owner, "branches_url", false, false, List.of(branch), List.of(error));
    }

    @Test
    public void correctConversion(){
        BranchDTO expectedBranch = new BranchDTO("branch", "lastCommitSha");
        Error error = new Error(400, "error");
        RepositoryDTO expected = new RepositoryDTO("login", "test", List.of(expectedBranch), List.of(error));
        RepositoryDTO converted = converter.repositoryToAPI().map(createRepository());

        assertEquals(expected.getRepositoryName(), converted.getRepositoryName());
        assertEquals(expected.getOwnerLogin(), converted.getOwnerLogin());
        assertEquals(expected.getBranches(), converted.getBranches());
        assertEquals(expected.getErrors(), converted.getErrors());
    }
}