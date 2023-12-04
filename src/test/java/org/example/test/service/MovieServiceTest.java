package org.example.test.service;

import org.example.model.Movie;
import org.example.repository.MovieRepository;
import org.example.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    private Movie avatarMovie;
    private Movie titanicMovie;

    @BeforeEach
    void init(){
        avatarMovie = new Movie();
        avatarMovie.setId(1L);
        avatarMovie.setName("Avatar");
        avatarMovie.setGenera("Action");
        avatarMovie.setReleaseDate(LocalDate.of(2000, Month.APRIL, 22));

        titanicMovie = new Movie();
        titanicMovie.setId(2L);
        titanicMovie.setName("Titanic");
        titanicMovie.setGenera("Romance");
        titanicMovie.setReleaseDate(LocalDate.of(1999, Month.MAY, 20));

    }

    @Test
    @DisplayName("Should save te object movie to db")
    void save(){
        when(movieRepository.save(any(Movie.class))).thenReturn(avatarMovie);
        Movie newMovie = movieService.save(avatarMovie);

        assertNotNull(newMovie);
        assertThat(newMovie.getName()).isEqualTo("Avatar");
    }

    @Test
    @DisplayName("Should return list of movies with size 2")
    void getMovies(){
        List<Movie> movies = new ArrayList<>();
        movies.add(avatarMovie);
        movies.add(titanicMovie);

        when(movieRepository.findAll()).thenReturn(movies);
        List<Movie> listMovies = movieService.getAllMovies();

        assertEquals(2, listMovies.size());
        assertNotNull(movies);
    }

    @Test
    @DisplayName("Should return the movie object")
    void getMovieById(){
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(avatarMovie));
        Movie existingMovie = movieService.getMovieById(1L);

        assertNotNull(existingMovie);
        assertThat(existingMovie.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throws exception")
    void getMovieByIdThrowsException(){
        when(movieRepository.findById(1L)).thenReturn(Optional.of(avatarMovie));
        when(movieRepository.findById(2L)).thenReturn(Optional.of(titanicMovie));

        assertThrows(RuntimeException.class, () -> {
            movieService.getMovieById(100L);
        });
    }

    @Test
    @DisplayName("Should update the movie into the db.")
    void updateMovie(){
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(avatarMovie));
        when((movieRepository.save(any(Movie.class)))).thenReturn(avatarMovie);
        avatarMovie.setGenera("Fantacy");

        Movie updatedMovie = movieService.updateMovie(avatarMovie, 1L);
        assertNotNull(updatedMovie);
        assertEquals("Fantacy", updatedMovie.getGenera());
    }

    @Test
    @DisplayName("Should delete the movie from db.")
    void deleteMovie(){
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(avatarMovie));
        doNothing().when(movieRepository).delete(any(Movie.class));

        movieService.deleteMovie(1L);
        verify(movieRepository, times(1)).delete(avatarMovie);
    }
}
