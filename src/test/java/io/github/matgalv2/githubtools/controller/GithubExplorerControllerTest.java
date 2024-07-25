package io.github.matgalv2.githubtools.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unauthorized rate limit rate equals to 60.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GithubExplorerControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    /**
     * GitHubs' usernames can only contain alphanumeric signs and dashes "-",
     * so username consisting of only "." will always return 404.
     */

    @Test
    public void givenNotExistentUsername_whenGetUserRepos_thenStatus404() throws Exception {
        mvc.perform(get("/repos/.")).andExpect(status().isNotFound());
    }

    @Test
    public void givenExistentUsername_whenGetUserRepos_thenStatus200() throws Exception {
        mvc.perform(get("/repos/matgalv2")).andExpect(status().isOk());
    }

}
