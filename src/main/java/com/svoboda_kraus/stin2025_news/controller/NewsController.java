package com.svoboda_kraus.stin2025_news.controller;

import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.RatedArticleGroup;
import com.svoboda_kraus.stin2025_news.model.StockRecommendation;
import com.svoboda_kraus.stin2025_news.service.ArticleFilter;
import com.svoboda_kraus.stin2025_news.service.NewsApiClient;
import com.svoboda_kraus.stin2025_news.service.SimpleSentimentAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/liststock")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsApiClient newsApiClient;

    private final Set<String> portfolio = new HashSet<>(Set.of("Apple", "Google")); // simulace

    @PostMapping
    public List<RatedArticleGroup> listStock(@RequestBody List<String> stockNames,
                                             @RequestParam(defaultValue = "3") int minArticles,
                                             @RequestParam(defaultValue = "false") boolean allowNegative) {

        List<RatedArticleGroup> rawGroups = new ArrayList<>();

        for (String name : stockNames) {
            List<Article> articles = newsApiClient.fetchNews(name, 15);

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

   
    @PostMapping("/salestock")
public List<String> handleRecommendations(@RequestBody List<StockRecommendation> recommendations) {
    List<String> changes = new ArrayList<>();

    for (StockRecommendation rec : recommendations) {
        String name = rec.getName();
        int sell = rec.getSell();
        int rating = rec.getRating();
        long date = rec.getDate();

        boolean invalid = false;

        if (name == null || name.trim().isEmpty()) {
            logger.warn("❌ Neplatná položka – chybí nebo je prázdné jméno: {}", rec);
            invalid = true;
        }

        if (date <= 0) {
            logger.warn("❌ Neplatná položka – neplatné datum (timestamp <= 0): {}", rec);
            invalid = true;
        }

        if (rating < -10 || rating > 10) {
            logger.warn("❌ Neplatná položka – rating mimo rozsah <-10, 10>: {}", rec);
            invalid = true;
        }

        if (sell != 0 && sell != 1) {
            logger.warn("❌ Neplatná položka – hodnota sell musí být 0 nebo 1, ale je {}: {}", sell, rec);
            invalid = true;
        }

        if (invalid) continue;

        boolean inPortfolio = portfolio.contains(name);

        if (sell == 1 && inPortfolio) {
            portfolio.remove(name);
            changes.add("Prodal jsem: " + name);
        } else if (sell == 0 && !inPortfolio) {
            portfolio.add(name);
            changes.add("Nakoupil jsem: " + name);
        } else {
            changes.add("Beze změny: " + name);
        }
    }

    return changes;
}



}
