package com.svoboda_kraus.stin2025_news;
import com.svoboda_kraus.stin2025_news.service.ArticleFilter;
import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.RatedArticleGroup;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArticleFilterTest {

    // Pomocná metoda pro vytvoření seznamu článků s daným ratingem
    private List<Article> createArticles(double rating, int count) {
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Article article = new Article();
            article.setRating((int) rating); // assuming rating is int in model
            articles.add(article);
        }
        return articles;
    }

    @Test
    void testGroupIncludedWhenSatisfiesAllConditions() {
        ArticleFilter filter = new ArticleFilter(3, false);
        RatedArticleGroup group = new RatedArticleGroup("Apple", createArticles(2, 5));

        List<RatedArticleGroup> result = filter.filter(List.of(group));

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).getStockName());
    }

    @Test
    void testGroupExcludedWhenTooFewArticles() {
        ArticleFilter filter = new ArticleFilter(3, false);
        RatedArticleGroup group = new RatedArticleGroup("Google", createArticles(3, 2));

        List<RatedArticleGroup> result = filter.filter(List.of(group));

        assertTrue(result.isEmpty());
    }

    @Test
    void testGroupExcludedWhenNegativeRatingAndNotAllowed() {
        ArticleFilter filter = new ArticleFilter(3, false);
        RatedArticleGroup group = new RatedArticleGroup("Tesla", createArticles(-1, 5));

        List<RatedArticleGroup> result = filter.filter(List.of(group));

        assertTrue(result.isEmpty());
    }

    @Test
    void testGroupIncludedWhenNegativeRatingAllowed() {
        ArticleFilter filter = new ArticleFilter(3, true);
        RatedArticleGroup group = new RatedArticleGroup("Meta", createArticles(-2, 5));

        List<RatedArticleGroup> result = filter.filter(List.of(group));

        assertEquals(1, result.size());
        assertEquals("Meta", result.get(0).getStockName());
    }

    @Test
    void testMultipleGroupsMixedResults() {
        ArticleFilter filter = new ArticleFilter(3, false);

        RatedArticleGroup valid = new RatedArticleGroup("Valid", createArticles(1, 4));
        RatedArticleGroup tooFew = new RatedArticleGroup("Few", createArticles(2, 2));
        RatedArticleGroup negative = new RatedArticleGroup("Bad", createArticles(-2, 5));

        List<RatedArticleGroup> result = filter.filter(Arrays.asList(valid, tooFew, negative));

        assertEquals(1, result.size());
        assertEquals("Valid", result.get(0).getStockName());
    }
}
