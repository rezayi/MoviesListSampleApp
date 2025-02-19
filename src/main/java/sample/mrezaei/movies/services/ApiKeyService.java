package sample.mrezaei.movies.services;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sample.mrezaei.movies.data.entities.ApiKeyEntity;
import sample.mrezaei.movies.data.repositories.ApiKeyRepository;

import java.util.List;

/**
 * This service includes functions to validate api keys.
 * This class caches the API keys in order to increase the response speed.
 */
@Service
@AllArgsConstructor
public class ApiKeyService {
    private ApiKeyRepository apiKeyRepository;

    private List<String> apiKeys;

    @PostConstruct
    public void initCache() {
        apiKeys = apiKeyRepository.findAll().
                stream()
                .map(ApiKeyEntity::getApiKey)
                .toList();
    }

    public boolean isValidApiKey(String apiKey) {
        return apiKeys.contains(apiKey);
    }
}
