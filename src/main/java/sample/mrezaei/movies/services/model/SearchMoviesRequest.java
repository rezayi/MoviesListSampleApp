package sample.mrezaei.movies.services.model;

import org.springframework.data.domain.Sort;

public record SearchMoviesRequest(
        String query,
        String sortBy,
        Sort.Direction sortDirection,
        String releaseDateFrom,
        String releaseDateTo,
        Double minRating
) {
}
