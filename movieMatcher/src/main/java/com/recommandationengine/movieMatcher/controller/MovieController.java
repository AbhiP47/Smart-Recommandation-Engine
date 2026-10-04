package com.recommandationengine.movieMatcher.controller;

import com.recommandationengine.movieMatcher.service.MovieService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private  final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public void search(@RequestParam String query)
    {
        movieService.search(query);
    }
}
