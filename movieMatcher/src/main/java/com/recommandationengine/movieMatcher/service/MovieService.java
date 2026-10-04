package com.recommandationengine.movieMatcher.service;

import com.recommandationengine.movieMatcher.model.Movie;
import com.recommandationengine.movieMatcher.model.MovieData;
import com.recommandationengine.movieMatcher.model.MovieMatch;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class MovieService {

    private final org.springframework.ai.embedding.EmbeddingModel embeddingModel;
    private final JsonMapper jsonMapper;

    public MovieService(@Qualifier("googleGenAiTextEmbedding") EmbeddingModel embeddingModel, JsonMapper jsonMapper) {
        this.embeddingModel = embeddingModel;
        this.jsonMapper = jsonMapper;
    }

    private final List<Movie> moviesEmbeddings = new ArrayList<>();



    @PostConstruct
    public void initializeMovies() throws IOException {
        log.info("***** Initializing Movies  *****");
        ClassPathResource resource =
                new ClassPathResource("movies.json");
        InputStream inputStream = resource.getInputStream();

        List<MovieData> movieDataList = jsonMapper.readValue(
                inputStream, new TypeReference<>() {}
        );

        for (MovieData movieData : movieDataList) {
            float[] embeddings = embeddingModel.embed(movieData.getDescription());
            Movie movie = new Movie(
                    movieData.getTitle(),
                    movieData.getDescription(),
                    embeddings
            );

            moviesEmbeddings.add(movie);
        }
        inputStream.close();
        System.out.println(moviesEmbeddings.size() + "  movies loaded with embeddings");
    }

    public List<MovieMatch> search(String query)
    {
        float[] userQueryEmbeddings = embeddingModel.embed(query);

        List<MovieMatch> matches = new ArrayList<>();

        for(Movie movie : moviesEmbeddings)
        {
            double similarity = cosineSimilarity(userQueryEmbeddings , movie.getEmbedding());
            MovieMatch movieMatch = new MovieMatch(movie.getTitle() , movie.getDescription(),  similarity);
            matches.add(movieMatch);
        }
        sortBySimilarity(matches);

        return topKMatches(matches , 3);
    }



    private double cosineSimilarity(float[] a, float[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += (a[i] * b[i]);
            normA += (a[i] * a[i]);
            normB += (b[i] * b[i]);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private void sortBySimilarity(List<MovieMatch> matches) {
        Collections.sort(matches,
                (first, second) -> Double.compare(
                        second.getMatch(),
                        first.getMatch())
        );
    }

    private List<MovieMatch> topKMatches(
            List<MovieMatch> matches, int limit) {

        List<MovieMatch> topMatches = new ArrayList<>();

        int numberOfMatches = Math.min(limit, matches.size());

        for (int i = 0; i < numberOfMatches; i++) {
            topMatches.add(matches.get(i));
        }

        return topMatches;
    }
}
