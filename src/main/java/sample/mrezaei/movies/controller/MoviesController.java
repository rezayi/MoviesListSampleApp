package sample.mrezaei.movies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sample.mrezaei.movies.services.MoviesService;
import sample.mrezaei.movies.services.model.MovieDetailsResponse;
import sample.mrezaei.movies.services.model.MovieListResponse;
import sample.mrezaei.movies.services.model.SearchMoviesRequest;

import java.util.List;

/**
 * This Controller class provides movies related Rest APIs
 */
@Validated
@RestController
@RequestMapping("/movies")
@AllArgsConstructor
@Tag(name = "Movies")
public class MoviesController {

    private MoviesService moviesService;

    @Operation(
            summary = "Get popular movies",
            description = "Returns list of popular movies. page numbers are in 1-based system."
    )
    @GetMapping("popular")
    public List<MovieListResponse> getPopularMovies(
            @Parameter(
                    description = "page number",
                    required = false
            )
            @Min(1)
            @RequestParam(required = false) Integer page
    ) {
        return moviesService.getPopularMovies(page);
    }

    @Operation(
            summary = "Search in movies",
            description = "Returns the list of movies searched by query and sorted and filtered if needed."
    )
    @GetMapping("search")
    public List<MovieListResponse> searchMovies(
            @Parameter(
                    description = "search query. this parameter searches by title",
                    required = true
            )
            @NotBlank(message = "Query is empty")
            @RequestParam(name = "query") String query,

            @Parameter(
                    description = "sorting column",
                    required = false,
                    schema = @Schema(allowableValues = {"id", "releaseDate", "averageRating"})
            )
            @RequestParam(name = "sort_by", required = false, defaultValue = "id") String sortBy,

            @Parameter(
                    description = "sorting direction",
                    required = false,
                    schema = @Schema(allowableValues = {"asc", "desc"})
            )
            @RequestParam(name = "sort_dir", required = false, defaultValue = "asc") String sortDirection,

            @Parameter(
                    description = "filter by date start. if you don't set day or month it will set it to the first day." +
                            "It will accept any of these formats: 2025-12-31 , 2025-12 , 2024",
                    required = false,
                    example = "2025-01-01"
            )
            @Pattern(regexp = "^([0-9]{4})?(-[0-9]{2})?(-[0-9]{2})?$", message = "date format is invalid")
            @RequestParam(name = "filter_date_from", required = false) String dateFrom,

            @Parameter(
                    description = "filter by date end. if you don't set day or month it will set it to the last day." +
                            "It will accept any of these formats: 2025-12-31 , 2025-12 , 2024",
                    required = false,
                    example = "2025-12-31"
            )
            @Pattern(regexp = "^([0-9]{4})?(-[0-9]{2})?(-[0-9]{2})?$", message = "date format is invalid")
            @RequestParam(name = "filter_date_to", required = false) String dateTo,

            @Parameter(
                    description = "filter by minimum rating score of movies",
                    required = false
            )
            @Min(value = 1, message = "Minimum rating must be at least 1")
            @Max(value = 10, message = "Maximum rating cannot exceed 10")
            @RequestParam(name = "filter_min_rate", required = false) Double minRate
    ) {
        var direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        SearchMoviesRequest searchMoviesRequest = new SearchMoviesRequest(query, sortBy, direction, dateFrom, dateTo, minRate);
        return moviesService.searchMovies(searchMoviesRequest);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Movie not found!"),
    })
    @Operation(
            summary = "Find movie by id",
            description = "Returns detailed single movies information"
    )
        @GetMapping("{id}")
        public MovieDetailsResponse getMovieById(
                @Parameter(description = "Movie ID")
                @Positive
                @PathVariable("id") Integer id
        ) {
            return moviesService.getMovieById(id);
        }
}
