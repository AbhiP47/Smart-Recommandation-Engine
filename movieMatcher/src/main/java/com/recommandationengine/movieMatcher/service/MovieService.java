package com.recommandationengine.movieMatcher.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MovieService {

    private final org.springframework.ai.embedding.EmbeddingModel embeddingModel;

    public MovieService(@Qualifier("googleGenAiTextEmbedding") EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public void search(String query)
    {
        float[] embeddings = embeddingModel.embed(query);

        System.out.println(embeddings.length);
        System.out.println("Embeddings : " + Arrays.toString(embeddings));
    }
}
