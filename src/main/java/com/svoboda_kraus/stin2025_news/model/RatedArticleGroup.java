package com.svoboda_kraus.stin2025_news.model;

import java.util.List;

public class RatedArticleGroup {
    private String stockName;
    private List<Article> articles;
    private double averageRating;

    // Konstruktor, gettery, settery

    public RatedArticleGroup(String stockName, List<Article> articles) {
        this.stockName = stockName;
        this.articles = articles;
        this.averageRating = calculateAverageRating();
    }

    private double calculateAverageRating() {
        return articles.stream()
                .mapToInt(Article::getRating)
                .average()
                .orElse(0);
    }

    public String getStockName() {
        return stockName;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public double getAverageRating() {
        return averageRating;
    }
}
