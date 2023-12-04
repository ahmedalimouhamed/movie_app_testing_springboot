package org.example.test.repository;

import org.example.model.Movie;
import org.example.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MovieRepositoryTest {
    @Autowired
    private MovieRepository movieRepository;

    private Movie avatarMovie;
    private Movie titanicMovie;

    @BeforeEach
    void init(){
        avatarMovie = new Movie();
        avatarMovie.setName("Avatar");
        avatarMovie.setGenera("Action");
        avatarMovie.setReleaseDate(LocalDate.of(2000, Month.APRIL, 22));

        titanicMovie = new Movie();
        titanicMovie.setName("Titanic");
        titanicMovie.setGenera("Romance");
        titanicMovie.setReleaseDate(LocalDate.of(1999, Month.MAY, 20));
    }

    @Test
    @DisplayName("It should save the movie to the database.")
    void save(){
        Movie newMovie = movieRepository.save(avatarMovie);

        assertNotNull(newMovie);
        assertThat(newMovie.getId()).isNotNull();
    }

    @Test
    @DisplayName("It Should return the movies list with size of 2.")
    void getAllMovies(){
        movieRepository.save(avatarMovie);
        movieRepository.save(titanicMovie);

        List<Movie> movies = movieRepository.findAll();
        assertNotNull(movies);
        assertThat(movies).isNotNull();
        assertEquals(2, movies.size());
    }

    @Test
    @DisplayName("It Sould eturn the movie by its id.")
    void getMovieById(){
        movieRepository.save(avatarMovie);
        movieRepository.save(titanicMovie);

        Movie foundedMovie = movieRepository.findById(avatarMovie.getId()).get();
        assertNotNull(foundedMovie);
        assertEquals("Action", foundedMovie.getGenera());
        assertThat(avatarMovie.getReleaseDate()).isBefore(LocalDate.of(2000, Month.APRIL, 23));

        Movie foundedMovie2 = movieRepository.findById(titanicMovie.getId()).get();
        assertNotNull(foundedMovie2);
        assertEquals("Romance", foundedMovie2.getGenera());
        assertThat(titanicMovie.getReleaseDate()).isBefore(LocalDate.of(1999, Month.MAY, 21));
    }

    @Test
    @DisplayName("It should update movie with genera fantacy")
    void updateMovie(){
        movieRepository.save(avatarMovie);
        movieRepository.save(titanicMovie);

        Movie movieToUpdate = movieRepository.findById(avatarMovie.getId()).get();
        movieToUpdate.setGenera("Fantacy");
        Movie updatedMovie = movieRepository.save(movieToUpdate);

        assertEquals("Fantacy", updatedMovie.getGenera());
        assertEquals("Avatar", updatedMovie.getName());
    }

    @Test
    @DisplayName("It should delete avatar movie")
    void deleteMovie(){
        movieRepository.save(avatarMovie);
        movieRepository.save(titanicMovie);

        Long id = avatarMovie.getId();

        movieRepository.delete(avatarMovie);
        Optional<Movie> deletedMovie = movieRepository.findById(id);
        List<Movie> movies = movieRepository.findAll();
        assertEquals(1, movies.size());
        assertThat(deletedMovie).isEmpty();
    }

    @Test
    @DisplayName("It should get list of movies genera Action")
    void getMoviesByGenera(){
        movieRepository.save(avatarMovie);
        movieRepository.save(titanicMovie);

        List<Movie> actionMovies = movieRepository.findByGenera("Action");
        assertNotNull(actionMovies);
        assertThat(actionMovies.size()).isEqualTo(1);
    }
}
