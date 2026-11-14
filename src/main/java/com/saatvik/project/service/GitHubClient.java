package com.saatvik.project.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.saatvik.project.dto.GitHubApiResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GitHubClient {

    private final RestTemplate restTemplate;
    private static final String GITHUB_SEARCH_URL = 
        "https://api.github.com/search/repositories?q={query}+language:{language}&sort={sort}&order=desc";

    public GitHubClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GitHubApiResponse searchRepositories(String query, String language, String sort) {
        try {
            String lang = (language != null) ? language : "";
            String sortBy = (sort != null) ? sort : "stars";
            
            ResponseEntity<GitHubApiResponse> response = restTemplate.getForEntity(
                GITHUB_SEARCH_URL,
                GitHubApiResponse.class,
                query, lang, sortBy
            );
            
            return response.getBody();
            
        } catch (HttpClientErrorException e) {
            log.error("GitHub API Error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to fetch from GitHub: " + e.getMessage());
        }
    }
}
