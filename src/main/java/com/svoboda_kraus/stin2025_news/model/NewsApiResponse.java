package com.svoboda_kraus.stin2025_news.model;

import java.util.List;

public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;

    // gettery a settery

    public List<Article> getArticles() {
        return articles;
    }
}