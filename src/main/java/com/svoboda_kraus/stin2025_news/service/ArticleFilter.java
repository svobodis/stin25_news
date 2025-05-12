package com.svoboda_kraus.stin2025_news.service;

import com.svoboda_kraus.stin2025_news.model.RatedArticleGroup;

import java.util.*;
import java.util.stream.Collectors;

public class ArticleFilter {

    private static final int MIN_ARTICLE_COUNT = 3;

    public List<RatedArticleGroup> filter(List<RatedArticleGroup> input) {
        return input.stream()
            .filter(group -> group.getArticles().size() >= MIN_ARTICLE_COUNT)
            .filter(group -> group.getArticles().stream().anyMatch(a -> a.getRating() >= 0))
            .collect(Collectors.toList());
    }
}
