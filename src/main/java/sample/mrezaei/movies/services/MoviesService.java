package sample.mrezaei.movies.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import sample.mrezaei.movies.data.entities.MovieEntity;
import sample.mrezaei.movies.data.repositories.MoviesRepository;
import sample.mrezaei.movies.data.specifications.MovieSpecification;
import sample.mrezaei.movies.exceptions.MovieNotFoundException;
import sample.mrezaei.movies.services.model.MovieDetailsResponse;
import sample.mrezaei.movies.services.model.MovieListResponse;
import sample.mrezaei.movies.services.model.SearchMoviesRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MoviesService {
    private MoviesRepository moviesRepository;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public MovieDetailsResponse getMovieById(Integer id) {
        var movieEntity = moviesRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);
        return MovieDetailsResponse.fromMovieEntity(movieEntity);
    }

    public List<MovieListResponse> getPopularMovies(Integer page) {
        var pageRequest = PageRequest.of(page == null ? 0 : page, 50)
                .withSort(Sort.by("ratingScore").descending());
        var movies = moviesRepository.findAll(pageRequest);
        return movies.stream()
                .map(MovieListResponse::fromMovieEntity)
                .collect(Collectors.toList());
    }

    public List<MovieListResponse> searchMovies(SearchMoviesRequest searchMoviesRequest) {
        //TODO validate inputs
        Specification<MovieEntity> spec;
        try {
            spec = Specification.where(MovieSpecification.searchByTitleQuery(searchMoviesRequest.query()));
            if (searchMoviesRequest.releaseDateFrom() != null && !searchMoviesRequest.releaseDateFrom().isEmpty())
                spec = spec.and(MovieSpecification.filterByReleaseDateFrom(DATE_FORMAT.parse(searchMoviesRequest.releaseDateFrom())));
            if (searchMoviesRequest.releaseDateTo() != null && !searchMoviesRequest.releaseDateTo().isEmpty())
                spec = spec.and(MovieSpecification.filterByReleaseDateTo(DATE_FORMAT.parse(searchMoviesRequest.releaseDateTo())));
            if (searchMoviesRequest.minRating() != null)
                spec = spec.and(MovieSpecification.filterByMinimumRating(searchMoviesRequest.minRating()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        var sortColumn = switch (searchMoviesRequest.sortBy()){
            case "averageRating" -> "ratingScore";
            case "releaseDate" -> "releaseDate";
            default -> "id";
        };
        var sort = Sort.by(sortColumn);
        sort = switch (searchMoviesRequest.sortDirection()) {
            case ASC -> sort.ascending();
            case DESC -> sort.descending();
        };
        var pageRequest = PageRequest.of(0, 10, sort);
        return moviesRepository.findAll(spec, pageRequest).stream()
                .map(MovieListResponse::fromMovieEntity)
                .collect(Collectors.toList());
    }
}
