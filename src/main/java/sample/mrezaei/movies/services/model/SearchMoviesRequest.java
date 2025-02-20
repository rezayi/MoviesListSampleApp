package sample.mrezaei.movies.services.model;

import lombok.Builder;
import org.springframework.data.domain.Sort;

@Builder
public record SearchMoviesRequest(
        String query,
        String sortBy,
        Sort.Direction sortDirection,
        String releaseDateFrom,
        String releaseDateTo,
        Double minRating
) {
}
