package com.svoboda_kraus.stin2025_news.service;

import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.NewsApiResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class NewsApiClient {

    private final WebClient webClient = WebClient.create("https://newsapi.org");
    private final String apiKey = "0e25ed76a2c74908b4d628216c3be42d";

    public List<Article> fetchNews(String query, int daysBack) {
        LocalDate fromDate = LocalDate.now().minusDays(daysBack);
        NewsApiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/everything")
                        .queryParam("q", query)
                        .queryParam("from", fromDate.toString()) // můžeš později dynamicky
                        .queryParam("sortBy", "publishedAt")
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(NewsApiResponse.class)
                .onErrorReturn(new NewsApiResponse())
                .block(); // POZOR – blokuje, ale pro jednoduchost OK

        if (response == null || response.getArticles() == null) {
            System.out.println("Žádné články pro '" + query + "'.");
            return Collections.emptyList();
        }

        System.out.println("Počet článků pro '" + query + "': " + response.getArticles().size());
        return response.getArticles();
    }
}
