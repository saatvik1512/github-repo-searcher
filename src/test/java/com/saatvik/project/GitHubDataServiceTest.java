package com.saatvik.project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.saatvik.project.dto.GitHubApiResponse;
import com.saatvik.project.entity.GithubEntity;
import com.saatvik.project.repository.GitHubEntityRepository;
import com.saatvik.project.service.GitHubDataService;

@ExtendWith(MockitoExtension.class)
class GitHubDataServiceTest {

    @Mock
    private GitHubEntityRepository repository;

    @InjectMocks
    private GitHubDataService service;

    @Test
    void shouldCreateNewEntityWhenNotExists() {
        // Arrange
        GitHubApiResponse.GitHubRepoItem item = createSampleItem(999L, "new-repo");
        when(repository.findByGithubId(999L)).thenReturn(Optional.empty());
        when(repository.save(any(GithubEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        List<GithubEntity> result = service.saveOrUpdateRepos(List.of(item));

        // Assert
        assertEquals(1, result.size());
        assertEquals("new-repo", result.get(0).getName());
        verify(repository).save(any(GithubEntity.class)); // Save call hua
    }

    @Test
    void shouldUpdateExistingEntityWhenGithubIdMatches() {
        // Arrange
        GithubEntity existing = new GithubEntity();
        existing.setId(1L); // DB ID set karo (important!)
        existing.setGithubId(888L);
        existing.setStars(100);
        existing.setName("old-name");

        GitHubApiResponse.GitHubRepoItem updatedItem = createSampleItem(888L, "updated-repo");
        updatedItem.setStars(200);

        when(repository.findByGithubId(888L)).thenReturn(Optional.of(existing));
        when(repository.save(any(GithubEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        List<GithubEntity> result = service.saveOrUpdateRepos(List.of(updatedItem));

        // Assert
        assertEquals(1, result.size());
        assertEquals(200, result.get(0).getStars()); // Stars updated
        assertEquals("updated-repo", result.get(0).getName()); // Name updated
        assertEquals(888L, result.get(0).getGithubId()); // Same GitHub ID

        verify(repository, times(1)).save(argThat(e -> e.getGithubId().equals(888L) && e.getStars() == 200));
    }

    @Test
    void shouldHandleNullOwnerGracefully() {
        GitHubApiResponse.GitHubRepoItem item = createSampleItem(777L, "no-owner");
        item.setOwner(null);
        
        when(repository.findByGithubId(777L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.saveOrUpdateRepos(List.of(item)));
    }

    // Helper method
    private GitHubApiResponse.GitHubRepoItem createSampleItem(Long id, String name) {
        GitHubApiResponse.GitHubRepoItem item = new GitHubApiResponse.GitHubRepoItem();
        item.setId(id);
        item.setName(name);
        item.setStars(50);
        item.setForks(10);
        item.setLanguage("Java");
        item.setLastUpdated(LocalDateTime.now());
        
        GitHubApiResponse.GitHubRepoItem.Owner owner = new GitHubApiResponse.GitHubRepoItem.Owner();
        owner.setLogin("test-user");
        item.setOwner(owner);
        
        return item;
    }
}
