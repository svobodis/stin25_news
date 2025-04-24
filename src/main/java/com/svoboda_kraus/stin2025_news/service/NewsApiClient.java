package com.svoboda_kraus.stin2025_news.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NewsApiClient {

    private final WebClient webClient = WebClient.create("https://newsapi.org");

    private final String apiKey = "0e25ed76a2c74908b4d628216c3be42d";

    public String fetchNews(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/everything")
                        .queryParam("q", query)
                        .queryParam("from", "2024-04-01") // můžeš později dynamicky
                        .queryParam("sortBy", "publishedAt")
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("{}")
                .block(); // POZOR – blokuje, ale pro jednoduchost OK
    }
}
