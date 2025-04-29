package com.svoboda_kraus.stin2025_news.controller;

import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.service.NewsApiClient;
import com.svoboda_kraus.stin2025_news.service.SimpleSentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/liststock")
public class NewsController {

    @Autowired
    private NewsApiClient newsApiClient;

    @PostMapping
    public List<Article> listStock(@RequestBody List<String> stockNames) {
        List<Article> allArticles = new ArrayList<>();
        for (String name : stockNames) {
            List<Article> articles = newsApiClient.fetchNews(name, 15);
            for (Article article : articles) {
                String fullText = article.getTitle() + " " + article.getDescription();
                String sentiment = SimpleSentimentAnalyzer.analyze(fullText);
                article.setRating(sentiment);
            }
            allArticles.addAll(articles);
        }
        return allArticles;
    }
}
