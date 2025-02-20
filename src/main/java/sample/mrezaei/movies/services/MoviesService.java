package sample.mrezaei.movies.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import sample.mrezaei.movies.data.entities.MovieEntity;
import sample.mrezaei.movies.data.repositories.MoviesRepository;
import sample.mrezaei.movies.data.specifications.MovieSpecification;
import sample.mrezaei.movies.exceptions.InputParamException;
import sample.mrezaei.movies.exceptions.MovieNotFoundException;
import sample.mrezaei.movies.services.model.MovieDetailsResponse;
import sample.mrezaei.movies.services.model.MovieListResponse;
import sample.mrezaei.movies.services.model.SearchMoviesRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This Service includes all methods to provide data for Rest Controller
 */
@Service
@AllArgsConstructor
public class MoviesService {
    private MoviesRepository moviesRepository;

    private static final SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat MONTH_DATE_FORMAT = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy");

    /**
     * this function returns a detailed information object of a movies by its id.
     *
     * @param id movie id
     * @return detail information object
     */
    public MovieDetailsResponse getMovieById(Integer id) {
        var movieEntity = moviesRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);
        return MovieDetailsResponse.fromMovieEntity(movieEntity);
    }

    /**
     * It returns a list of most popular movies by pagination
     *
     * @param page page number
     * @return list of movies
     */
    public List<MovieListResponse> getPopularMovies(Integer page) {
        var pageRequest = PageRequest.of(page == null ? 0 : page - 1, 50)
                .withSort(Sort.by("ratingScore").descending());
        var movies = moviesRepository.findAll(pageRequest);
        return movies.stream()
                .map(MovieListResponse::fromMovieEntity)
                .toList();
    }

    /**
     * This function searches the movies according to input.
     * The only required parameter is `query`. The others are optional and only if the user provides a value will affect.
     *
     * @param searchMoviesRequest api input params model
     * @return final list of movies
     */
    public List<MovieListResponse> searchMovies(SearchMoviesRequest searchMoviesRequest) {
        Specification<MovieEntity> spec = getSearchSpecification(searchMoviesRequest);

        var sort = getSort(searchMoviesRequest);
        var pageRequest = PageRequest.of(0, 10, sort);
        return moviesRepository.findAll(spec, pageRequest).stream()
                .map(MovieListResponse::fromMovieEntity)
                .toList();
    }

    /**
     * This function returns the `Sort` object according to api inputs.
     *
     * @param searchMoviesRequest api input params model
     * @return Sort object
     */
    private Sort getSort(SearchMoviesRequest searchMoviesRequest) {
        var sortColumn = switch (searchMoviesRequest.sortBy()) {
            case "averageRating" -> "ratingScore";
            case "releaseDate" -> "releaseDate";
            default -> "id";
        };
        var sort = Sort.by(sortColumn);
        if (searchMoviesRequest.sortDirection() != null) {
            sort = switch (searchMoviesRequest.sortDirection()) {
                case ASC -> sort.ascending();
                case DESC -> sort.descending();
            };
        }
        return sort;
    }

    /**
     * This function returns the `Specification` object according to api inputs.
     * It applies only none empty search and filter parameters.
     *
     * @param searchMoviesRequest api input params model
     * @return specification inputs
     */
    private Specification<MovieEntity> getSearchSpecification(SearchMoviesRequest searchMoviesRequest) {
        Specification<MovieEntity> spec = Specification.where(MovieSpecification.searchByTitleQuery(searchMoviesRequest.query()));
        if (searchMoviesRequest.releaseDateFrom() != null && !searchMoviesRequest.releaseDateFrom().isEmpty())
            spec = spec.and(MovieSpecification.filterByReleaseDateFrom(getValidReleaseDate(searchMoviesRequest.releaseDateFrom(), true)));
        if (searchMoviesRequest.releaseDateTo() != null && !searchMoviesRequest.releaseDateTo().isEmpty())
            spec = spec.and(MovieSpecification.filterByReleaseDateTo(getValidReleaseDate(searchMoviesRequest.releaseDateTo(), false)));
        if (searchMoviesRequest.minRating() != null)
            spec = spec.and(MovieSpecification.filterByMinimumRating(searchMoviesRequest.minRating()));
        return spec;
    }

    /**
     * This function converts a date string to Date object.
     * It accepts all `yyyy-MM-ddd` or `yyyy-MM` or `yyyy` formats.
     * It sets the month and day to first for starting date and last for ending date.
     *
     * @param dateString input date string
     * @param start      is it from date or end date
     * @return validated date
     */
    public Date getValidReleaseDate(String dateString, boolean start) {
        if (dateString == null || dateString.isEmpty())
            return null;
        try {
            Calendar calendar = Calendar.getInstance();
            if (dateString.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
                calendar.setTime(FULL_DATE_FORMAT.parse(dateString));
            } else if (dateString.matches("^[0-9]{4}-[0-9]{2}$")) {
                calendar.setTime(MONTH_DATE_FORMAT.parse(dateString));
                calendar.set(Calendar.DAY_OF_MONTH, start ? 1 : calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else if (dateString.matches("^[0-9]{4}$")) {
                calendar.setTime(YEAR_DATE_FORMAT.parse(dateString));
                calendar.set(Calendar.MONTH, start ? 1 : 12);
                calendar.set(Calendar.DAY_OF_MONTH, start ? 1 : calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else {
                throw new InputParamException("invalid date format");
            }
            return calendar.getTime();
        } catch (ParseException e) {
            throw new InputParamException("invalid date format");
        }
    }
}
