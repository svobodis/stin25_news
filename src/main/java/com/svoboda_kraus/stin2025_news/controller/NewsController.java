package com.svoboda_kraus.stin2025_news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.RatedArticleGroup;
import com.svoboda_kraus.stin2025_news.model.StockRecommendation;
import com.svoboda_kraus.stin2025_news.service.ArticleFilter;
import com.svoboda_kraus.stin2025_news.service.NewsApiClient;
import com.svoboda_kraus.stin2025_news.service.SimpleSentimentAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    // Sdílené portfolio, přetrvává mezi voláními
    private final Set<String> portfolio = new HashSet<>();
    List<StockRecommendation> out = new ArrayList<>();

    // 1) FRONTEND: získání článků + ratingů
    @PostMapping
    public List<RatedArticleGroup> listStock(
            @RequestBody List<String> stockNames,
            @RequestParam(defaultValue = "3") int minArticles,
            @RequestParam(defaultValue = "false") boolean allowNegative,
            @RequestParam(defaultValue = "7") int daysBack) {

        List<RatedArticleGroup> groups = new ArrayList<>();
        for (String name : stockNames) {
            List<Article> arts = newsApiClient.fetchNews(name, daysBack);
            for (Article a : arts) {
                String full = Optional.ofNullable(a.getTitle()).orElse("") + " "
                                    + Optional.ofNullable(a.getDescription()).orElse("");
                a.setRating(SimpleSentimentAnalyzer.analyze(full));
            }
            groups.add(new RatedArticleGroup(name, arts));
        }
        return new ArticleFilter(minArticles, allowNegative).filter(groups);
    }

    // 2) BURZA volá → vracíme jen rating (sell=null)
    @PostMapping("/rating")
    public List<StockRecommendation> rateStocks(
            @RequestBody List<StockRecommendation> reqs,
            @RequestParam(defaultValue = "30") int maxDays) {
    
        logger.info("📥 ZPRÁVY obdržely JSON s pouze 'name' a 'date':");
        reqs.forEach(r -> logger.info(" - name: {}, date: {}", r.getName(), r.getDate()));
    
        List<StockRecommendation> out = new ArrayList<>();
    
        for (StockRecommendation r : reqs) {
            boolean valid = true;
            if (r.getName() == null || r.getName().isBlank()) {
                logger.warn("❌ Neplatný vstup: chybí nebo prázdné jméno akcie: {}", r);
                valid = false;
            }
            if (r.getDate() <= 1000000000L || r.getDate() >= 9999999999L) {
                logger.warn("❌ Neplatný vstup: očekáván 10místný epoch timestamp pro {}", r.getName());
                valid = false;
            }
            if (!valid) continue;
    
            LocalDate from = Instant.ofEpochSecond(r.getDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            long days = ChronoUnit.DAYS.between(from, LocalDate.now());
            days = Math.max(1, Math.min(days, maxDays));
    
            List<Article> arts = newsApiClient.fetchNews(r.getName(), (int) days);
            int total = arts.stream()
                    .mapToInt(a -> SimpleSentimentAnalyzer.analyze(
                            Optional.ofNullable(a.getTitle()).orElse("") + " " +
                            Optional.ofNullable(a.getDescription()).orElse("")))
                    .sum();
            int avg = arts.isEmpty() ? 0 : total / arts.size();
            out.add(new StockRecommendation(r.getName(), r.getDate(), avg));
        }
    
        out.forEach(r -> logger.info(" - name: {}, date: {}, rating: {}", r.getName(), r.getDate(), r.getRating()));
    
        // ➕ odeslání zpět na BURZA
        String burzaUrl = "http://localhost:8080/mock/rating";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<StockRecommendation>> entity = new HttpEntity<>(out, headers);
            restTemplate.postForEntity(burzaUrl, entity, String.class);
            //logger.info("✅ Výsledky byly úspěšně odeslány na {}", burzaUrl);
        } catch (Exception ex) {
            logger.error("❌ Chyba při odesílání výsledků na burzu: {}", ex.getMessage());
        }
    
        return out;
    }
    


    // 3) BURZA volá s rating+sell → provedeme trade a vrátíme detaily
    @PostMapping("/salestock")
    public List<String> executeTrades(@RequestBody List<StockRecommendation> recs) {
        List<String> messages = new ArrayList<>();

        for (StockRecommendation r : recs) {
            String stock = r.getName();
            Integer sell  = r.getSell();

            if (stock == null || stock.isBlank()) {
                messages.add("Neplatná položka: chybí název");
                continue;
            }
            if (r.getDate() <= 0) {
                messages.add("Neplatná položka: neplatné datum pro " + stock);
                continue;
            }
            if (r.getRating() < -10 || r.getRating() > 10) {
                messages.add("Neplatná položka: rating mimo rozsah pro " + stock);
                continue;
            }
            if (sell == null) {
                messages.add("Neplatná položka: chybí sell pro " + stock);
                continue;
            }
            if (sell != 0 && sell != 1) {
                messages.add("Neplatná položka: sell musí být 0 nebo 1 pro " + stock);
                continue;
            }

            if (sell == 1) {
                // prodej
                if (portfolio.remove(stock)) {
                    messages.add("Prodaná akcie: " + stock);
                    logger.info("Prodaná akcie: {}", stock);
                } else {
                    messages.add("Nelze prodat akcii " + stock + " – není v portfoliu");
                    logger.info("Nelze prodat {}, není v portfoliu", stock);
                }
            } else {
                // nákup
                if (portfolio.add(stock)) {
                    messages.add("Nakoupena akcie: " + stock);
                    logger.info("Nakoupena akcie: {}", stock);
                } else {
                    messages.add("Akcie " + stock + " již je v portfoliu, nekoupeno");
                    logger.info("Akcie {} již v portfoliu, nekoupeno", stock);
                }
            }
        }

        if (messages.isEmpty()) {
            messages.add("ℹ️ Nebyly provedeny žádné operace.");
        }
        return messages;
    }

    @GetMapping("/portfolio")
    public Set<String> getPortfolio() {
        return Collections.unmodifiableSet(portfolio);
    }
}
