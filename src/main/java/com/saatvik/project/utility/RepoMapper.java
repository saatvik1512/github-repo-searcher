package com.saatvik.project.utility;

import com.saatvik.project.dto.RepoResponse;
import com.saatvik.project.entity.GithubEntity;

public class RepoMapper {
    public static RepoResponse toResponse(GithubEntity entity) {
        return RepoResponse.builder()
                .id(entity.getGithubId())
                .name(entity.getName())
                .description(entity.getDescription())
                .owner(entity.getOwner())
                .language(entity.getLanguage())
                .stars(entity.getStars())
                .forks(entity.getForks())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }
}