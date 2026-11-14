package com.saatvik.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saatvik.project.entity.GithubEntity;

@Repository
public interface GitHubEntityRepository extends JpaRepository<GithubEntity, Long> {
    Optional<GithubEntity> findByGithubId(Long githubId);
    List<GithubEntity> findByLanguageAndStarsGreaterThanEqual(String language, Integer minStars);
    List<GithubEntity> findByStarsGreaterThanEqual(Integer minStars);
    List<GithubEntity> findByLanguage(String language);
}
