package com.svoboda_kraus.stin2025_news.service;

import com.svoboda_kraus.stin2025_news.model.RatedArticleGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ArticleFilter {

    private static final Logger logger = LoggerFactory.getLogger(ArticleFilter.class);

    private final int minArticleCount;
    private final boolean allowNegative;

    public ArticleFilter(int minArticleCount, boolean allowNegative) {
        this.minArticleCount = minArticleCount;
        this.allowNegative = allowNegative;
    }

    public List<RatedArticleGroup> filter(List<RatedArticleGroup> input) {
        logger.info("=== FILTR BYL VOLÁN ===");

        List<RatedArticleGroup> filtered = new ArrayList<>();

        for (RatedArticleGroup group : input) {
            double avgRating = group.getAverageRating();
            int articleCount = group.getArticles().size();

            logger.info("Firma: {}, článků: {}, průměr: {}, allowNegative: {}",
                    getGroupName(group), articleCount, avgRating, allowNegative);

            if (articleCount < minArticleCount) {
                logger.info("yřazeno – málo článků");
                continue;
            }

            if (!allowNegative && avgRating < 0) {
                logger.info("Vyřazeno – negativní rating");
                continue;
            }

            logger.info("Přidáno");
            filtered.add(group);
        }

        return filtered;
    }

    private String getGroupName(RatedArticleGroup group) {
        try {
            return group.getStockName(); 
        } catch (Exception e) {
            return "(neznámá firma)";
        }
    }
}
