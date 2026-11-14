package com.saatvik.project.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saatvik.project.dto.RepoResponse;
import com.saatvik.project.dto.SearchRequest;
import com.saatvik.project.entity.GithubEntity;
import com.saatvik.project.service.GitHubClient;
import com.saatvik.project.service.GitHubDataService;
import com.saatvik.project.utility.RepoMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubSearchController {

    private final GitHubClient gitHubClient;
    private final GitHubDataService gitHubDataService;


    @Operation(summary = "Search GitHub Repositories", description = "Fetches repos from GitHub API and saves/updates in DB")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repositories fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search query"),
            @ApiResponse(responseCode = "500", description = "GitHub API rate limit or server error")
    })
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAndSave(@Valid @RequestBody SearchRequest request) {
        // 1. Fetch from GitHub
        var apiResponse = gitHubClient.searchRepositories(
            request.getQuery(), 
            request.getLanguage(), 
            request.getSort()
        );

        // 2. Upsert
        List<GithubEntity> savedEntities = gitHubDataService.saveOrUpdateRepos(apiResponse.getItems());

        // 3. Map to Response DTO
        List<RepoResponse> responses = savedEntities.stream()
                .map(RepoMapper::toResponse)
                .collect(Collectors.toList());

        // 4. Build response 
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Repositories fetched and saved successfully");
        result.put("repositories", responses);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get stored repositories", description = "Retrieves saved repositories from the DB with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repositories retrieved successfully")
    })
    @GetMapping("/repositories")
    public ResponseEntity<Map<String, List<RepoResponse>>> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(defaultValue = "stars") String sort) {

        List<GithubEntity> entities = gitHubDataService.getFilteredRepos(language, minStars, sort);

        List<RepoResponse> responses = entities.stream()
                .map(RepoMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("repositories", responses));
    }
}
