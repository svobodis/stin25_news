package com.svoboda_kraus.stin2025_news.service;

import com.svoboda_kraus.stin2025_news.model.RatedArticleGroup;

import java.util.ArrayList;
import java.util.List;

public class ArticleFilter {

    private final int minArticleCount;
    private final boolean allowNegative;

    public ArticleFilter(int minArticleCount, boolean allowNegative) {
        this.minArticleCount = minArticleCount;
        this.allowNegative = allowNegative;
    }

    public List<RatedArticleGroup> filter(List<RatedArticleGroup> input) {
        List<RatedArticleGroup> filtered = new ArrayList<>();

        for (RatedArticleGroup group : input) {
            if (group.getArticles().size() < minArticleCount) {
                continue;
            }

            if (!allowNegative && group.getAverageRating() < 0) {
                continue;
            }

            filtered.add(group);
        }

        return filtered;
    }
}
