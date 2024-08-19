package io.github.matgalv2.githubtools.controller;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;


@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubExplorerControllerIntegrationTest {

	@Autowired
    private MockMvc mockMvc;
    @Value("${github.api.endpoint.repositories}")
    private String endpointRepositories;

    @Value("${github.api.endpoint.branches}")
    private String endpointBranches;

	@RegisterExtension
	static WireMockExtension wireMockExtension = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

	@DynamicPropertySource
	static void dynamicPropertySource(final DynamicPropertyRegistry registry) {
		registry.add("github.api.baseURL", wireMockExtension::baseUrl);
	}

    @Test
    public void test_existing_user_correct_response() throws Exception {
        String username = "username";

        wireMockExtension.stubFor(WireMock.get(urlMatching(endpointRepositories.replaceFirst("\\{.*?}", username)))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("""
						[
						    {
							    "name": "repo1",
							    "owner": {
							    	"login": "username"
							    },
							    "branches_url": "https://api.github.com/repos/user/repo1/branches{/branch}",
							    "fork": false,
							    "private": false
						    },
						    {
						        "name": "repo2",
							    "owner": {
							    	"login": "username"
							    },
							    "branches_url": "https://api.github.com/repos/user/repo2/branches{/branch}",
							    "fork": true,
							    "private": false
						    },
						    {
							    "name": "repo3",
							    "owner": {
							    	"login": "username"
							    },
							    "branches_url": "https://api.github.com/repos/user/repo3/branches{/branch}",
							    "fork": false,
							    "private": true
						    },
						    {
						        "name": "repo4",
							    "owner": {
							    	"login": "username"
							    },
							    "branches_url": "https://api.github.com/repos/user/repo4/branches{/branch}",
							    "fork": true,
							    "private": true
						    }
						]
						""")));

		wireMockExtension.stubFor(WireMock.get(urlMatching(endpointBranches.replaceFirst("\\{.*?}", username).replaceFirst("\\{.*?}", ".*?")))
				.willReturn(
						aResponse()
								.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
								.withBody("""
						[
						    {
								"name": "master",
								"commit": {
									"sha": "ef32ff17ea4b7eb7936db1158470fa3c257864c3"
								},
								"protected": false
						    },
						    {
								"name": "main",
								"commit": {
									"sha": "caa40305d00b18d4bc2669fcd63fb5c2e075136b"
								},
								"protected": false
						    }
						]
						""")));

        this.mockMvc
				.perform(get("/repos/{username}", username))
				.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].ownerLogin", is(username)))
                .andExpect(jsonPath("$.[0].repositoryName", is("repo1")))
                .andExpect(jsonPath("$.[0].branches.[0].name", is("master")))
                .andExpect(jsonPath("$.[0].branches.[0].lastCommit", is("ef32ff17ea4b7eb7936db1158470fa3c257864c3")))
                .andExpect(jsonPath("$.[0].branches.[1].name", is("main")))
                .andExpect(jsonPath("$.[0].branches.[1].lastCommit", is("caa40305d00b18d4bc2669fcd63fb5c2e075136b")));

    }

	@Test
	public void test_nonexistent_user_not_found_response() throws Exception {
		String username = "username";
		wireMockExtension.stubFor(WireMock.get(urlMatching(endpointRepositories.replaceFirst("\\{.*?}", username)))
				.willReturn(
						aResponse()
								.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
								.withBody("""
						[
						    {
							    "name": "repo1",
							    "owner": {
							    	"login": "username"
							    },
							    "branches_url": "https://api.github.com/repos/user/repo1/branches{/branch}",
							    "fork": false,
							    "private": false
						    }
						]
						""")));

		wireMockExtension.stubFor(WireMock.get(urlMatching(endpointBranches.replaceFirst("\\{.*?}", username).replaceFirst("\\{.*?}", ".*?")))
				.willReturn(
						aResponse()
								.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
								.withBody("""
						[
						    {
								"name": "master",
								"commit": {
									"sha": "ef32ff17ea4b7eb7936db1158470fa3c257864c3"
								},
								"protected": false
						    }
						]
						""")));

		this.mockMvc
				.perform(get("/repos/{username}", "nonexistent_user"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void test_githubApiError_internal_server_error_response() throws Exception {
		String username = "username";
		wireMockExtension.stubFor(WireMock.get(urlMatching(endpointRepositories.replaceFirst("\\{.*?}", username)))
				.willReturn(serverError()));

		this.mockMvc
				.perform(get("/repos/{username}", username))
				.andExpect(status().isInternalServerError());
	}

}
