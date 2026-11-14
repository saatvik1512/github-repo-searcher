package com.saatvik.project;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.saatvik.project.entity.GithubEntity;
import com.saatvik.project.service.GitHubClient;
import com.saatvik.project.service.GitHubDataService;

import lombok.RequiredArgsConstructor;

// it was made only to test early features like is the api working or not ? 

@Component
@RequiredArgsConstructor
public class GitHubApiTester implements CommandLineRunner {

    private final GitHubClient gitHubClient;

    private final GitHubDataService gitHubDataService;

    @Override
    public void run(String... args) {
        System.out.println("Testing Fetch + Upsert...");

        var response = gitHubClient.searchRepositories("spring boot", "Java", "stars");

        if (response != null && response.getItems() != null) {
            // Call Upsert Service
            List<GithubEntity> saved = gitHubDataService.saveOrUpdateRepos(response.getItems());

            System.out.println("DB Sync Complete! Total records processed: " + saved.size());

            // Verify: Print first 3
            saved.stream().limit(3).forEach(e -> System.out.printf(" DB ID: %d | GH ID: %d | %s | %d%n",
                    e.getId(), e.getGithubId(), e.getName(), e.getStars()));
        }
    }
}