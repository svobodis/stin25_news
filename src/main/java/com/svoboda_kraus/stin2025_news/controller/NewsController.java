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

    // Simulované portfolio (můžeš propojit s databází)
    private final Set<String> portfolio = new HashSet<>(Set.of("Apple", "Google"));

    // FRONTEND: získání hodnocených článků
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

    // BURZA posílá: name, date – my vrátíme rating (bez sell)
    @PostMapping("/rating")
    public List<StockRecommendation> analyzeStocks(@RequestBody List<StockRecommendation> requests) {
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
                int score = SimpleSentimentAnalyzer.analyze(fullText);

                logger.info("Analyzuji akcii '{}':", req.getName());
                logger.info("Článek: '{}'", fullText);
                logger.info("Skóre článku: {}", score);

                totalScore += score;
            }

            int avgRating = articles.isEmpty() ? 0 : totalScore / articles.size();

            // Necháváme sell jako null – přidá BURZA
            results.add(new StockRecommendation(req.getName(), req.getDate(), avgRating, null));
        }

        return results;
    }

    // BURZA vrací zpět rating + sell => my prodáme nebo nakoupíme
    @PostMapping("/salestock")
    public String handleRecommendations(@RequestBody List<StockRecommendation> recommendations) {
        List<String> koupene = new ArrayList<>();
        List<String> prodane = new ArrayList<>();

        for (StockRecommendation rec : recommendations) {
            if (rec.getName() == null || rec.getName().trim().isEmpty() ||
                rec.getDate() <= 0 || rec.getRating() < -10 || rec.getRating() > 10 ||
                rec.getSell() == null || (rec.getSell() != 0 && rec.getSell() != 1)) {

                logger.warn("Neplatná položka: {}", rec);
                continue;
            }

            String stock = rec.getName();

            if (rec.getSell() == 1 && portfolio.contains(stock)) {
                portfolio.remove(stock);
                prodane.add(stock);
                logger.info("Prodaná akcie: {}", stock);
            } else if (rec.getSell() == 0 && !portfolio.contains(stock)) {
                portfolio.add(stock);
                koupene.add(stock);
                logger.info("Nakoupena akcie: {}", stock);
            } else {
                logger.info("Beze změny: {}", stock);
            }
        }

        StringBuilder response = new StringBuilder();
        if (!koupene.isEmpty()) {
            response.append("Nakoupil jsem ").append(String.join(", ", koupene));
        }
        if (!prodane.isEmpty()) {
            if (!koupene.isEmpty()) {
                response.append(" a ");
            }
            response.append("prodal jsem ").append(String.join(", ", prodane));
        }

        if (response.isEmpty()) {
            return "Nebyly provedeny žádné změny v portfoliu.";
        }

        return response.toString();
    }

    // Pomocný endpoint pro kontrolu portfolia
    @GetMapping("/portfolio")
    public Set<String> getPortfolio() {
        return portfolio;
    }
}
