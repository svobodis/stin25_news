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
        List<RatedArticleGroup> filtered = new ArrayList<>();
    
        for (RatedArticleGroup group : input) {
            int articleCount = group.getArticles().size();
    
            if (articleCount < minArticleCount) {
                continue; // málo článků
            }
    
            if (!allowNegative) {
                // ⛔ Odstraň články s negativním hodnocením
                group.getArticles().removeIf(article -> article.getRating() < 0);
            }
    
            // ⚠️ Pokud po odstranění nejsou žádné články, přeskoč
            if (group.getArticles().isEmpty()) {
                continue;
            }
    
            filtered.add(group);
        }
    
        return filtered;
    }
    
}
    
