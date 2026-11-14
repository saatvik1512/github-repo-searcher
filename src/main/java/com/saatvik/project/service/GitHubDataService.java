package com.saatvik.project.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.saatvik.project.dto.GitHubApiResponse;
import com.saatvik.project.entity.GithubEntity;
import com.saatvik.project.repository.GitHubEntityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubDataService {

    private final GitHubEntityRepository repository;

    @Transactional
    public List<GithubEntity> saveOrUpdateRepos(List<GitHubApiResponse.GitHubRepoItem> items) {
        List<GithubEntity> savedEntities = new ArrayList<>();

        for (GitHubApiResponse.GitHubRepoItem item : items) {
            GithubEntity entity = repository.findByGithubId(item.getId()).orElseGet(() -> {
                    // 2. If not, create new
                    GithubEntity newEntity = new GithubEntity();
                    newEntity.setGithubId(item.getId());
                    return newEntity;
                });

            // 3. Update/Set fields 
            entity.setName(item.getName());
            entity.setDescription(item.getDescription());
            entity.setOwner(item.getOwner() != null ? item.getOwner().getLogin() : null);
            entity.setLanguage(item.getLanguage());
            entity.setStars(item.getStars());
            entity.setForks(item.getForks());
            entity.setLastUpdated(item.getLastUpdated());

            // 4. Save 
            savedEntities.add(repository.save(entity));
        }

        log.info("Saved/Updated {} repositories", savedEntities.size());
        return savedEntities;
    }
    public List<GithubEntity> getFilteredRepos(String language, Integer minStars, String sort) {
        List<GithubEntity> results;

        if (language != null && minStars != null) {
            results = repository.findByLanguageAndStarsGreaterThanEqual(language, minStars);
        } else if (language != null) {
            results = repository.findByLanguage(language);
        } else if (minStars != null) {
            results = repository.findByStarsGreaterThanEqual(minStars);
        } else {
            results = repository.findAll();
        }

        // Sorting Logic (using comparator for that)
        Comparator<GithubEntity> comparator;

        if ("forks".equals(sort)) {
            comparator = Comparator.comparing(GithubEntity::getForks, Comparator.nullsLast(Comparator.reverseOrder()));
        } else if ("updated".equals(sort)) {
            comparator = Comparator.comparing(GithubEntity::getLastUpdated, Comparator.nullsLast(Comparator.reverseOrder()));
        } else {
            // Default: stars
            comparator = Comparator.comparing(GithubEntity::getStars, Comparator.nullsLast(Comparator.reverseOrder()));
        }

        return results.stream().sorted(comparator).collect(Collectors.toList());
    }


}
