package sample.mrezaei.movies.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sample.mrezaei.movies.data.entities.Genre;
import sample.mrezaei.movies.data.entities.Language;
import sample.mrezaei.movies.services.model.MovieDetailsResponse;
import sample.mrezaei.movies.services.model.MovieListResponse;

import java.text.SimpleDateFormat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoviesServiceIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private MoviesService moviesService;

    @Test
    public void testGetMovieById_expectToBeSuccessful() {
        var movie = restTemplate.getForObject("/movies/1?apiKey=9c7ede34-372e-4744-80bd-0ab59f835924", MovieDetailsResponse.class);

        Assertions.assertNotNull(movie);
        Assertions.assertEquals("Mad Max: Fury Road", movie.title());
        Assertions.assertEquals(Genre.Action, movie.genre());
        Assertions.assertEquals("A dystopian future where a warlord controls the water supply and a group of rebels fight to overthrow him.", movie.overview());
        Assertions.assertEquals("https://m.media-amazon.com/images/M/MV5BZDRkODJhOTgtOTc1OC00NTgzLTk4NjItNDgxZDY4YjlmNDY2XkEyXkFqcGc@._V1_.jpg", movie.posterUrl());
        Assertions.assertEquals(8.1, movie.averageRating());
        Assertions.assertEquals(Language.English, movie.language());
        Assertions.assertEquals("2015-05-15", movie.releaseDate());
        Assertions.assertEquals(120, movie.runtime());
    }

    @Test
    public void testGetMovieById_notExistingMovie_expectProperErrorCode() {
        var movie = restTemplate.getForEntity("/movies/1000?apiKey=9c7ede34-372e-4744-80bd-0ab59f835924", MovieDetailsResponse.class);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, movie.getStatusCode().value());
    }

    @Test
    public void testGetMovieById_invalidId_expectProperErrorCode() {
        var movie = restTemplate.getForEntity("/movies/-1?apiKey=9c7ede34-372e-4744-80bd-0ab59f835924", MovieDetailsResponse.class);
        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, movie.getStatusCode().value());
    }

    @Test
    public void testGetMovieById_unauthorized_expectProperErrorCode() {
        var movie = restTemplate.getForEntity("/movies/1", MovieDetailsResponse.class);
        Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, movie.getStatusCode().value());
    }

    @Test
    public void testGetPopularMovies_withoutPage_expectedToLoadFirstPage() {
        MovieListResponse[] movies = restTemplate.getForObject("/movies/popular?apiKey=9c7ede34-372e-4744-80bd-0ab59f835924", MovieListResponse[].class);
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(50, movies.length);
        Assertions.assertEquals(9.3, movies[0].averageRating());
        double previousScore = movies[0].averageRating();
        for (MovieListResponse movie : movies) {
            Assertions.assertTrue(movie.averageRating() <= previousScore);
            previousScore = movie.averageRating();
        }
    }

    @Test
    public void testGetPopularMovies_withPage_expectedToLoadChosenPage() {
        MovieListResponse[] movies = restTemplate.getForObject("/movies/popular?page=2&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924", MovieListResponse[].class);
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(42, movies.length);
        Assertions.assertEquals(8.1, movies[0].averageRating());
        double previousScore = movies[0].averageRating();
        for (MovieListResponse movie : movies) {
            Assertions.assertTrue(movie.averageRating() <= previousScore);
            previousScore = movie.averageRating();
        }
    }

    @Test
    public void testGetPopularMovies_notExistsPage_expectedEmptyResult() {
        MovieListResponse[] movies = restTemplate.getForObject("/movies/popular?page=3&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924", MovieListResponse[].class);
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(0, movies.length);
    }


    @Test
    public void testGetPopularMovies_notExistsPage_invalidPage_expectProperErrorCode() {
        var movie = restTemplate.getForEntity("/movies/popular?page=-1&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924", MovieDetailsResponse.class);
        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, movie.getStatusCode().value());
    }

    @Test
    public void testGetPopularMovies_unauthorized_expectProperErrorCode() {
        var movie = restTemplate.getForEntity("/movies/popular?page=1", MovieDetailsResponse.class);
        Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, movie.getStatusCode().value());
    }

    @Test
    public void testSearchMovies_withOnlySearchQuery_expectedToFilterResultsAndDefaultOrderByIdAscending() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(10, movies.length);
        double previousId = movies[0].id();
        for (int i = 1; i < movies.length; i++) {
            Assertions.assertTrue(movies[i].id() > previousId);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousId = movies[i].id();
        }
    }

    @Test
    public void testSearchMovies_withoutSearchQuery_expectedProperErrorCode() {
        var movies = restTemplate.getForEntity(
                "/movies/search?apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                String.class
        );
        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, movies.getStatusCode().value());
    }

    @Test
    @SneakyThrows
    public void testSearchMovies_withSearchQueryAndFilterByReleaseDateFromByOnlyYear_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&filter_date_from=2008&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(9, movies.length);
        var filterDate = DATE_FORMAT.parse("2008-01-01");
        double previousId = movies[0].id();
        for (int i = 1; i < movies.length; i++) {
            var movieReleaseDate = DATE_FORMAT.parse(movies[i].releaseDate());
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDate) > 0);
            Assertions.assertTrue(movies[i].id() > previousId);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousId = movies[i].id();
        }
    }

    @Test
    @SneakyThrows
    public void testSearchMovies_withSearchQueryAndFilterByReleaseDateFromByYearAndMonth_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&filter_date_from=2009-07&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(5, movies.length);
        var filterDate = DATE_FORMAT.parse("2009-07-01");
        double previousId = movies[0].id();
        for (int i = 1; i < movies.length; i++) {
            var movieReleaseDate = DATE_FORMAT.parse(movies[i].releaseDate());
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDate) > 0);
            Assertions.assertTrue(movies[i].id() > previousId);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousId = movies[i].id();
        }
    }

    @Test
    @SneakyThrows
    public void testSearchMovies_withSearchQueryAndFilterByReleaseDateFromByFullDate_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&filter_date_from=2009-11-25&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(4, movies.length);
        var filterDate = DATE_FORMAT.parse("2009-11-25");
        double previousId = movies[0].id();
        for (int i = 1; i < movies.length; i++) {
            var movieReleaseDate = DATE_FORMAT.parse(movies[i].releaseDate());
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDate) > 0);
            Assertions.assertTrue(movies[i].id() > previousId);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousId = movies[i].id();
        }
    }
    @Test
    @SneakyThrows
    public void testSearchMovies_withSearchQueryAndFilterByReleaseDateToByOnlyYear_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&filter_date_from=2008&&filter_date_to=2014&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(8, movies.length);
        var filterDateFrom = DATE_FORMAT.parse("2008-01-01");
        var filterDateTo = DATE_FORMAT.parse("2014-12-31");
        double previousId = movies[0].id();
        for (int i = 1; i < movies.length; i++) {
            var movieReleaseDate = DATE_FORMAT.parse(movies[i].releaseDate());
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDateFrom) > 0);
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDateTo) < 0);
            Assertions.assertTrue(movies[i].id() > previousId);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousId = movies[i].id();
        }
    }

    @Test
    @SneakyThrows
    public void testSearchMovies_withSearchQueryAndFilterByReleaseDateToByYearAndMonth_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&filter_date_from=2008&filter_date_to=2014-07&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(7, movies.length);
        var filterDateFrom = DATE_FORMAT.parse("2008-01-01");
        var filterDateTo = DATE_FORMAT.parse("2014-07-31");
        double previousId = movies[0].id();
        for (int i = 1; i < movies.length; i++) {
            var movieReleaseDate = DATE_FORMAT.parse(movies[i].releaseDate());
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDateFrom) > 0);
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDateTo) < 0);
            Assertions.assertTrue(movies[i].id() > previousId);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousId = movies[i].id();
        }
    }

    @Test
    @SneakyThrows
    public void testSearchMovies_withSearchQueryAndFilterByReleaseDateToByFullDate_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&filter_date_from=2008&filter_date_to=2014-11-25&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(8, movies.length);
        var filterDateFrom = DATE_FORMAT.parse("2008-01-01");
        var filterDateTo = DATE_FORMAT.parse("2014-11-25");
        double previousId = movies[0].id();
        for (int i = 1; i < movies.length; i++) {
            var movieReleaseDate = DATE_FORMAT.parse(movies[i].releaseDate());
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDateFrom) > 0);
            Assertions.assertTrue(movieReleaseDate.compareTo(filterDateTo) < 0);
            Assertions.assertTrue(movies[i].id() > previousId);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousId = movies[i].id();
        }
    }

    @Test
    public void testSearchMovies_withOnlySearchQueryAndDescending_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&sort_dir=desc&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(10, movies.length);
        double previousId = movies[0].id();
        for (int i = 1; i < movies.length; i++) {
            Assertions.assertTrue(movies[i].id() < previousId);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousId = movies[i].id();
        }
    }

    @Test
    @SneakyThrows
    public void testSearchMovies_withOnlySearchQueryAndSortByReleaseDateAndDescending_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&sort_by=releaseDate&sort_dir=desc&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(10, movies.length);
        var previousDate = DATE_FORMAT.parse(movies[0].releaseDate());
        for (int i = 1; i < movies.length; i++) {
            var movieReleaseDate = DATE_FORMAT.parse(movies[i].releaseDate());
            Assertions.assertTrue(movieReleaseDate.compareTo(previousDate) <= 0);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousDate = movieReleaseDate;
        }
    }

    @Test
    @SneakyThrows
    public void testSearchMovies_withOnlySearchQueryAndSortByAverageRatingAndDescending_expectedProperResults() {
        MovieListResponse[] movies = restTemplate.getForObject(
                "/movies/search?query=the&sort_by=averageRating&sort_dir=desc&apiKey=9c7ede34-372e-4744-80bd-0ab59f835924",
                MovieListResponse[].class
        );
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(10, movies.length);
        var previousRating = movies[0].averageRating();
        for (int i = 1; i < movies.length; i++) {
            var movieAverageRating = movies[i].averageRating();
            Assertions.assertTrue(movieAverageRating.compareTo(previousRating) <= 0);
            Assertions.assertTrue(movies[i].title().toLowerCase().contains("the"));
            previousRating = movieAverageRating;
        }
    }
}
