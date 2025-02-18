package sample.mrezaei.movies.services.model;

import sample.mrezaei.movies.data.entities.MovieEntity;

public record MovieListResponse(
        int id,
        String title,
        String releaseDate,
        String posterUrl,
        Double averageRating
) {
    public static MovieListResponse fromMovieEntity(MovieEntity movieEntity) {
        return new MovieListResponse(
                movieEntity.getId(),
                movieEntity.getTitle(),
                movieEntity.getReleaseDate(),
                movieEntity.getPosterUrl(),
                movieEntity.getRatingScore()
        );
    }
}
