package sample.mrezaei.movies.services.model;

import sample.mrezaei.movies.data.entities.Genre;
import sample.mrezaei.movies.data.entities.Language;
import sample.mrezaei.movies.data.entities.MovieEntity;

public record MovieDetailsResponse(
        String title,
        String releaseDate,
        String posterUrl,
        String overview,
        Genre genre,
        Double averageRating,
        Integer runtime,
        Language language
){
    public static MovieDetailsResponse fromMovieEntity(MovieEntity movieEntity) {
        return new MovieDetailsResponse(
                movieEntity.getTitle(),
                movieEntity.getReleaseDate(),
                movieEntity.getPosterUrl(),
                movieEntity.getOverview(),
                movieEntity.getGenre(),
                movieEntity.getRatingScore(),
                movieEntity.getRuntimeInMinutes(),
                movieEntity.getLanguage()
        );
    }
}
