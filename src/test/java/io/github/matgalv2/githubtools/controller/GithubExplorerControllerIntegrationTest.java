package io.github.matgalv2.githubtools.controller;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.github.matgalv2.githubtools.dto.BranchDTO;
import io.github.matgalv2.githubtools.dto.RepositoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.util.List;

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


    private WebTestClient webTestClient;
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

	@BeforeEach
	public void setUp(ApplicationContext applicationContext) {
		webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
	}

    @Test
    public void test_existing_user_correct_response(){
        String username = "username";

        wireMockExtension.stubFor(WireMock.get(urlMatching(endpointRepositories.replaceFirst("\\{username}", username)))
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

		wireMockExtension.stubFor(WireMock.get(urlMatching(endpointBranches.replaceFirst("\\{username}", ".*").replaceFirst("\\{repository}" , ".*")))
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

		RepositoryDTO expected = new RepositoryDTO(
				username,
				"repo1",
				List.of(
						new BranchDTO("master", "ef32ff17ea4b7eb7936db1158470fa3c257864c3"),
						new BranchDTO("main", "caa40305d00b18d4bc2669fcd63fb5c2e075136b")
				)
		);
        webTestClient
				.get()
				.uri("/repos/{username}", username)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(RepositoryDTO.class)
				.isEqualTo(List.of(expected));

    }

	@Test
	public void test_nonexistent_user_not_found_response() throws Exception {
		String nonExistentUsername = "nonExistentUsername";
		wireMockExtension.stubFor(WireMock.get(urlMatching(endpointRepositories.replaceFirst("\\{username}", "username")))
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

		wireMockExtension.stubFor(WireMock.get(urlMatching(endpointBranches.replaceFirst("\\{username}", ".*").replaceFirst("\\{repository}" , ".*")))
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

		webTestClient
				.get()
				.uri("/repos/{username}", nonExistentUsername)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void test_githubApiError_internal_server_error_response() throws Exception {
		String username = "username";
		wireMockExtension.stubFor(WireMock.get(urlMatching(endpointRepositories.replaceFirst("\\{username}", username)))
				.willReturn(serverError()));

		webTestClient
				.get()
				.uri("/repos/{username}", username)
				.exchange()
				.expectStatus().is5xxServerError();
	}

}
