package com.recommandationengine.movieMatcher.service;

import com.recommandationengine.movieMatcher.model.Movie;
import com.recommandationengine.movieMatcher.model.MovieData;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
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
        System.out.println(moviesEmbeddings.size() + "movies loaded with embeddings");
    }

    public void search(String query)
    {
        float[] embeddings = embeddingModel.embed(query);

        System.out.println(embeddings.length);
        System.out.println("Embeddings : " + Arrays.toString(embeddings));
    }
}
