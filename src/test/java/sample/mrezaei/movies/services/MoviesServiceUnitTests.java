package sample.mrezaei.movies.services;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import sample.mrezaei.movies.data.entities.Genre;
import sample.mrezaei.movies.data.entities.Language;
import sample.mrezaei.movies.data.entities.MovieEntity;
import sample.mrezaei.movies.data.repositories.MoviesRepository;
import sample.mrezaei.movies.exceptions.MovieNotFoundException;
import sample.mrezaei.movies.services.model.SearchMoviesRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class MoviesServiceUnitTests {
    @InjectMocks
    private MoviesService moviesService;

    @Mock
    private MoviesRepository moviesRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMovieById_successfullyLoaded() {
        var movieEntity = MovieEntity.builder()
                .id(1)
                .title("movie title")
                .genre(Genre.Action)
                .overview("This is a good movie")
                .posterUrl("http://movie.test/movie/1/poster.png")
                .ratingCount(10000)
                .ratingScore(8.1)
                .language(Language.English)
                .releaseDate("2025-01-01")
                .runtimeInMinutes(120)
                .build();
        Mockito.doReturn(Optional.of(movieEntity)).when(moviesRepository).findById(1);

        var movie = moviesService.getMovieById(1);
        Assertions.assertEquals("movie title", movie.title());
        Assertions.assertEquals(Genre.Action, movie.genre());
        Assertions.assertEquals("This is a good movie", movie.overview());
        Assertions.assertEquals("http://movie.test/movie/1/poster.png", movie.posterUrl());
        Assertions.assertEquals(8.1, movie.averageRating());
        Assertions.assertEquals(Language.English, movie.language());
        Assertions.assertEquals("2025-01-01", movie.releaseDate());
        Assertions.assertEquals(120, movie.runtime());
    }

    @Test
    public void testGetMovieById_userDoesNotExist_throwsMovieNotFoundException() {
        Mockito.doReturn(Optional.empty()).when(moviesRepository).findById(1);
        Assertions.assertThrows(MovieNotFoundException.class, () -> moviesService.getMovieById(1));
    }

    @Test
    public void testPopularMovies_checkOutput() {
        var movies = new ArrayList<MovieEntity>();
        for (int i = 0; i < 50; i++) {
            var movie = MovieEntity.builder()
                    .id(i + 1)
                    .title("movie title #" + i)
                    .genre(Genre.Action)
                    .overview("This is a good movie #" + i)
                    .posterUrl("http://movie.test/movie/" + i + "/poster.png")
                    .ratingCount(10000 + i)
                    .ratingScore(8.1 + (i / 5 * 0.1))
                    .language(Language.English)
                    .releaseDate("2025-01-0" + (i % 9 + 1))
                    .runtimeInMinutes(120 + (i % 10))
                    .build();
            movies.add(movie);
        }
        Mockito.doReturn(new PageImpl<>(movies)).when(moviesRepository).findAll(Mockito.any(PageRequest.class));

        var popularMovies = moviesService.getPopularMovies(null);

        //validate result
        Assertions.assertEquals(50, popularMovies.size());
        for (int i = 0; i < 50; i++) {
            var movie = popularMovies.get(i);
            Assertions.assertEquals(i + 1, movie.id());
            Assertions.assertEquals("movie title #" + i, movie.title());
            Assertions.assertEquals("http://movie.test/movie/" + i + "/poster.png", movie.posterUrl());
            Assertions.assertEquals(8.1 + (i / 5 * 0.1), movie.averageRating());
            Assertions.assertEquals("2025-01-0" + (i % 9 + 1), movie.releaseDate());
        }
    }

    @Test
    public void testPopularMovies_verifyRepositoryCall_withoutPagination_expectCallPageZero() {
        Mockito.doReturn(new PageImpl<>(Collections.emptyList()))
                .when(moviesRepository).findAll(Mockito.any(PageRequest.class));

        moviesService.getPopularMovies(null);

        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        Mockito.verify(moviesRepository).findAll(pageRequestCaptor.capture());
        //validate function called
        Assertions.assertTrue(pageRequestCaptor.getValue().getSort().isSorted());
        Assertions.assertEquals(0, pageRequestCaptor.getValue().getPageNumber());
        Assertions.assertEquals(50, pageRequestCaptor.getValue().getPageSize());
    }

    @Test
    public void testPopularMovies_verifyRepositoryCall_withPagination_expectCallSpecifiedPage() {
        Mockito.doReturn(new PageImpl<>(Collections.emptyList()))
                .when(moviesRepository).findAll(Mockito.any(PageRequest.class));

        moviesService.getPopularMovies(2);

        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        Mockito.verify(moviesRepository).findAll(pageRequestCaptor.capture());
        //validate function called
        Assertions.assertTrue(pageRequestCaptor.getValue().getSort().isSorted());
        Assertions.assertEquals(1, pageRequestCaptor.getValue().getPageNumber());
        Assertions.assertEquals(50, pageRequestCaptor.getValue().getPageSize());
    }

    @Test
    public void testSearchMovies_onlyQuery() {
        ArgumentCaptor<Specification<MovieEntity>> specificationCaptor = ArgumentCaptor.forClass(Specification.class);
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);

        Mockito.doReturn(new PageImpl<>(Collections.emptyList()))
                .when(moviesRepository).findAll(specificationCaptor.capture(), pageRequestCaptor.capture());


        moviesService.searchMovies(SearchMoviesRequest.builder()
                .query("test")
                .sortBy("title")
                .sortDirection(Sort.Direction.ASC)
                .build());

        //check page request
        Assertions.assertTrue(pageRequestCaptor.getValue().getSort().isSorted());
        Assertions.assertEquals(0, pageRequestCaptor.getValue().getPageNumber());
        Assertions.assertEquals(10, pageRequestCaptor.getValue().getPageSize());
    }
}
