package sample.mrezaei.movies.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sample.mrezaei.movies.data.entities.ApiKeyEntity;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Integer> {
}
