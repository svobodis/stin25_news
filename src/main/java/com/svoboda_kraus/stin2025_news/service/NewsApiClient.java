package com.svoboda_kraus.stin2025_news.service;

import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.NewsApiResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
public class NewsApiClient {

    private final WebClient webClient = WebClient.create("https://newsapi.org");
    private final String apiKey = "0e25ed76a2c74908b4d628216c3be42d";

    /**
     * @param query
     * @param epochSeconds
     * @return
     */
    public List<Article> fetchNews(String query, long epochSeconds) {
        LocalDate requestedDate = Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate now = LocalDate.now();
        long daysBack = ChronoUnit.DAYS.between(requestedDate, now);
    
        if (daysBack < 1) daysBack = 1;
        if (daysBack > 30) daysBack = 30;
    
        LocalDate fromDate = now.minusDays(daysBack);
    
        System.out.println("➡️ Dotaz na akcii '" + query + "' za posledních " + daysBack + " dní.");
    
        NewsApiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/everything")
                        .queryParam("q", query)
                        .queryParam("language", "en")
                        .queryParam("pageSize", "50")
                        .queryParam("from", fromDate.toString())
                        .queryParam("sortBy", "publishedAt")
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(NewsApiResponse.class)
                .onErrorReturn(new NewsApiResponse())
                .block();
    
        if (response == null || response.getArticles() == null) {
            System.out.println("❌ Žádné články pro '" + query + "'.");
            return Collections.emptyList();
        }
    
        System.out.println("✅ Počet článků pro '" + query + "': " + response.getArticles().size());
        return response.getArticles();
    }
}    