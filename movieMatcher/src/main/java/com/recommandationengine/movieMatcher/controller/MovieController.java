package com.recommandationengine.movieMatcher.controller;

import com.recommandationengine.movieMatcher.model.MovieMatch;
import com.recommandationengine.movieMatcher.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private  final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public List<MovieMatch> search(@RequestParam String query)
    {
        return movieService.search(query);
    }
}
