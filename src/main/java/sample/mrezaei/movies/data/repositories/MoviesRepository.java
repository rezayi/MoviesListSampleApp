package sample.mrezaei.movies.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sample.mrezaei.movies.data.entities.MovieEntity;

public interface MoviesRepository extends JpaRepository<MovieEntity, Integer>, JpaSpecificationExecutor<MovieEntity> {

}
