package com.saatvik.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchRequest {
    @NotBlank(message = "Query cannot be empty")
    private String query;
    
    private String language; // Optional
    private String sort;     // Optional: stars forks updated
}
