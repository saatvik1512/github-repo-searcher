package com.saatvik.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.saatvik.project.dto.ErrorResponse;
import com.saatvik.project.dto.GitHubApiResponse;
import com.saatvik.project.dto.SearchRequest;
import com.saatvik.project.service.GitHubClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class GitHubControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean 
    private GitHubClient gitHubClient; // Real API call 

    @BeforeEach
    void setupMocks() {
        // Default mock response for all tests
        GitHubApiResponse fakeResponse = new GitHubApiResponse();
        GitHubApiResponse.GitHubRepoItem item = new GitHubApiResponse.GitHubRepoItem();
        item.setId(123L);
        item.setName("test-repo");
        item.setStars(100);
        item.setForks(10);
        item.setLanguage("Java");
        item.setLastUpdated(LocalDateTime.now());
        
        GitHubApiResponse.GitHubRepoItem.Owner owner = new GitHubApiResponse.GitHubRepoItem.Owner();
        owner.setLogin("test-user");
        item.setOwner(owner);
        
        fakeResponse.setItems(List.of(item));
        
        when(gitHubClient.searchRepositories(anyString(), any(), any()))
            .thenReturn(fakeResponse);
    }

    @Test
    void shouldSearchAndSaveSuccessfully() {
        SearchRequest request = new SearchRequest();
        request.setQuery("spring");
        request.setLanguage("Java");
        request.setSort("stars");

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/api/github/search", request, Map.class);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody().get("repositories"));
        assertEquals("Repositories fetched and saved successfully", 
                     response.getBody().get("message"));
    }

    @Test
    void shouldReturn400ForEmptyQuery() {
        SearchRequest request = new SearchRequest();
        request.setQuery(""); // Invalid

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/github/search", request, ErrorResponse.class);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().getMessage().contains("query"));
    }

    @Test
    void shouldRetrieveWithFilters() {
        SearchRequest searchReq = new SearchRequest();
        searchReq.setQuery("test");
        restTemplate.postForEntity("/api/github/search", searchReq, Map.class);

        
        ResponseEntity<Map> response = restTemplate.getForEntity(
            "/api/github/repositories?language=Java&minStars=50&sort=stars", 
            Map.class);

        assertEquals(200, response.getStatusCode().value());
        List<?> repos = (List<?>) response.getBody().get("repositories");
        assertFalse(repos.isEmpty());
    }
}
