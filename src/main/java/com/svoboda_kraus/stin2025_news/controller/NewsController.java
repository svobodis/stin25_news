package com.svoboda_kraus.stin2025_news.controller;

import com.svoboda_kraus.stin2025_news.model.*;
import com.svoboda_kraus.stin2025_news.service.ArticleFilter;
import com.svoboda_kraus.stin2025_news.service.NewsApiClient;
import com.svoboda_kraus.stin2025_news.service.SimpleSentimentAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/liststock")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsApiClient newsApiClient;

    private final Set<String> portfolio = new HashSet<>(Set.of("Apple", "Google")); // simulace

    // 🔁 FRONTEND ENDPOINT
    @PostMapping
    public List<RatedArticleGroup> listStock(@RequestBody List<String> stockNames,
                                             @RequestParam(defaultValue = "3") int minArticles,
                                             @RequestParam(defaultValue = "false") boolean allowNegative,
                                             @RequestParam(defaultValue = "7") int daysBack) {

        List<RatedArticleGroup> rawGroups = new ArrayList<>();

        for (String name : stockNames) {
            List<Article> articles = newsApiClient.fetchNews(name, daysBack);

            for (Article article : articles) {
                String fullText = article.getTitle() + " " + article.getDescription();
                int score = SimpleSentimentAnalyzer.analyze(fullText);
                article.setRating(score);
            }

            rawGroups.add(new RatedArticleGroup(name, articles));
        }

        ArticleFilter filter = new ArticleFilter(minArticles, allowNegative);
        return filter.filter(rawGroups);
    }

    // 📤 BURZA POSÍLÁ: name, date – ZPRÁVY DOPLNÍ: rating, sell
    @PostMapping("/rating")
    public List<StockRecommendation> analyzeStocks(@RequestBody List<StockRecommendation> requests,
                                                   @RequestParam(defaultValue = "0") int sellThreshold) {
        List<StockRecommendation> results = new ArrayList<>();

        for (StockRecommendation req : requests) {
            if (req.getName() == null || req.getName().isBlank() || req.getDate() <= 0) {
                logger.warn("Neplatná položka ve vstupu: {}", req);
                continue;
            }

            LocalDate fromDate = Instant.ofEpochSecond(req.getDate())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
            long daysBack = ChronoUnit.DAYS.between(fromDate, LocalDate.now());
            if (daysBack < 1) daysBack = 1;

            List<Article> articles = newsApiClient.fetchNews(req.getName(), (int) daysBack);

            int totalScore = 0;
            for (Article article : articles) {
                String fullText = article.getTitle() + " " + article.getDescription();
                totalScore += SimpleSentimentAnalyzer.analyze(fullText);
            }

            int avgRating = articles.isEmpty() ? 0 : totalScore / articles.size();
            int sell = avgRating < sellThreshold ? 1 : 0;


            results.add(new StockRecommendation(req.getName(), req.getDate(), avgRating, sell));

        }

        return results;
    }

    // ✅ POUZE VALIDUJE A VRACÍ VSTUP (např. pro testování / kontrolu z Burzy)
    @PostMapping("/salestock")
    public List<StockRecommendation> handleRecommendations(@RequestBody List<StockRecommendation> recommendations) {
        List<StockRecommendation> valid = new ArrayList<>();

        for (StockRecommendation rec : recommendations) {
            boolean invalid = false;

            if (rec.getName() == null || rec.getName().trim().isEmpty()) {
                logger.warn("Neplatná položka – chybí jméno: {}", rec);
                invalid = true;
            }
            if (rec.getDate() <= 0) {
                logger.warn("Neplatná položka – neplatné datum: {}", rec);
                invalid = true;
            }
            if (rec.getRating() < -10 || rec.getRating() > 10) {
                logger.warn("Neplatná položka – rating mimo rozsah: {}", rec);
                invalid = true;
            }
            if (rec.getSell() != 0 && rec.getSell() != 1) {
                logger.warn("Neplatná položka – sell není 0 nebo 1: {}", rec);
                invalid = true;
            }

            if (!invalid) {
                valid.add(rec);
            }
        }

        return valid;
    }

    @GetMapping("/portfolio")
    public Set<String> getPortfolio() {
        return portfolio;
    }
}
