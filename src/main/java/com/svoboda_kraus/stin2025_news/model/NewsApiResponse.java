package com.svoboda_kraus.stin2025_news.model;

import java.util.ArrayList;
import java.util.List;

public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<Article> articles = new ArrayList<>();

    public List<Article> getArticles() {
        return articles != null ? articles : new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}