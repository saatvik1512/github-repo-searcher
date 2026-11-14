package com.saatvik.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubApiResponse {
    private List<GitHubRepoItem> items;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubRepoItem {
        private Long id;
        private String name;
        private String description;
        private Owner owner;
        private String language;
        
        @JsonProperty("stargazers_count")
        private Integer stars;
        
        @JsonProperty("forks_count")
        private Integer forks;
        
        @JsonProperty("updated_at")
        private LocalDateTime lastUpdated;
        
        @Data
        public static class Owner {
            private String login;
        }
    }
}