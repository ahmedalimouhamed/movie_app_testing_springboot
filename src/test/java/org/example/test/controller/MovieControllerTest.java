package org.example.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Movie;
import org.example.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class MovieControllerTest {
    @MockBean
    private MovieService movieService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie avatarMovie;
    private Movie titanicMovie;

    @BeforeEach
    public void init(){
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
    @DisplayName("should create a new movie")
    void shouldCreateNewMovie() throws Exception {
        when(movieService.save(any(Movie.class))).thenReturn(avatarMovie);
        this.mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avatarMovie)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is(avatarMovie.getName())))
        .andExpect(jsonPath("$.genera", is(avatarMovie.getGenera())))
        .andExpect(jsonPath("$.releaseDate", is(avatarMovie.getReleaseDate().toString())));

    }

    @Test
    @DisplayName("should return list of movies")
    void shouldFetchAllMovies() throws Exception {
        List<Movie> listMovies = new ArrayList<>();
        listMovies.add(avatarMovie);
        listMovies.add(titanicMovie);

        when(movieService.getAllMovies()).thenReturn(listMovies);
        this.mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(listMovies.size())));
    }

    @Test
    @DisplayName("should fetch movie by id")
    void shouldFetchMovieById() throws Exception {
        when(movieService.getMovieById(1L)).thenReturn(avatarMovie);
        when(movieService.getMovieById(2L)).thenReturn(titanicMovie);

        this.mockMvc.perform(get("/movies/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(avatarMovie.getName())))
                .andExpect(jsonPath("$.genera", is(avatarMovie.getGenera())));

        this.mockMvc.perform(get("/movies/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(titanicMovie.getName())))
                .andExpect(jsonPath("$.genera", is(titanicMovie.getGenera())));
    }

    @Test
    @DisplayName("should delete a movie by id")
    void shouldDeleteMovie() throws Exception {
        doNothing().when(movieService).deleteMovie(1L);
        this.mockMvc.perform(delete("/movies/{id}", 1L))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(delete("/movies/{id}", 2L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should update existing movie")
    void shouldUpdateMovie() throws Exception {
        when(movieService.updateMovie(any(Movie.class), anyLong())).thenReturn(avatarMovie);
        this.mockMvc.perform(put("/movies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avatarMovie))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(avatarMovie.getName())))
        .andExpect(jsonPath("$.genera", is(avatarMovie.getGenera())));
    }

}


