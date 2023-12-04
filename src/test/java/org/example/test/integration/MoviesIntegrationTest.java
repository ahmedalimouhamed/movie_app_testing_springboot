package org.example.test.integration;

import org.example.model.Movie;
import org.example.repository.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoviesIntegrationTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";
    private static RestTemplate restTemplate;

    @Autowired
    private MovieRepository movieRepository;

    private Movie avatarMovie;
    private Movie titanicMovie;

    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void beforeSetup(){
        baseUrl = baseUrl+":"+port+"/movies";

        avatarMovie = new Movie();
        avatarMovie.setName("Avatar");
        avatarMovie.setGenera("Action");
        avatarMovie.setReleaseDate(LocalDate.of(2000, Month.APRIL, 22));

        titanicMovie = new Movie();
        titanicMovie.setName("Titanic");
        titanicMovie.setGenera("Romance");
        titanicMovie.setReleaseDate(LocalDate.of(1999, Month.MAY, 20));

        avatarMovie = movieRepository.save(avatarMovie);
        titanicMovie = movieRepository.save(titanicMovie);
    }

    @AfterEach
    public void afterSetup(){
        movieRepository.deleteAll();
    }

    @Test
    void shouldCreateMovieTest(){
        Movie avatarMovie = new Movie();
        avatarMovie.setName("Avatar");
        avatarMovie.setGenera("Action");
        avatarMovie.setReleaseDate(LocalDate.of(2000, Month.APRIL, 22));

        Movie newMovie = restTemplate.postForObject(baseUrl, avatarMovie, Movie.class);

        assertNotNull(newMovie);
        assertThat(newMovie.getId()).isNotNull();
    }

    @Test
    void shouldFetchMoviesTest(){
        List<Movie> movies = restTemplate.getForObject(baseUrl, List.class);

        assertThat(movies.size()).isEqualTo(2);
    }

    @Test
    void shouldFetchOneMovieTest(){
        Movie existingMovie = restTemplate.getForObject(baseUrl+"/"+avatarMovie.getId(), Movie.class);
        assertNotNull(existingMovie);
        assertEquals("Avatar", existingMovie.getName());

        Movie existingMovie2 = restTemplate.getForObject(baseUrl+"/"+titanicMovie.getId(), Movie.class);
        assertNotNull(existingMovie2);
        assertEquals("Titanic", existingMovie2.getName());
    }

    @Test
    void shouldDeleteMovieTest(){
        restTemplate.delete(baseUrl+"/"+avatarMovie.getId());
        int count = movieRepository.findAll().size();

        assertEquals(1, count);
    }

    @Test
    void shouldUpdateMovieTest(){
        avatarMovie.setGenera("Fantacy");
        restTemplate.put(baseUrl+"/{id}", avatarMovie, avatarMovie.getId());

        Movie existingMovie = restTemplate.getForObject(baseUrl+"/"+avatarMovie.getId(), Movie.class);
        assertNotNull(existingMovie);
        assertEquals("Fantacy", existingMovie.getGenera());
    }

}
