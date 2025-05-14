package com.svoboda_kraus.stin2025_news;

import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.NewsApiResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsApiResponseTest {

    @Test
    void testGetArticles_returnsEmptyListIfNull() {
        NewsApiResponse response = new NewsApiResponse();

        assertNotNull(response.getArticles(), "Articles should never be null");
        assertTrue(response.getArticles().isEmpty(), "Articles should be an empty list if not set");
    }

    @Test
    void testSetAndGetStatus() {
        NewsApiResponse response = new NewsApiResponse();

        response.setStatus("ok");
        assertEquals("ok", response.getStatus(), "Status should be 'ok'");

    }

    @Test
    void testSetAndGetTotalResults() {
        NewsApiResponse response = new NewsApiResponse();

        response.setTotalResults(100);
        assertEquals(100, response.getTotalResults(), "TotalResults should be 100");
    }

    @Test
    void testSetAndGetArticles() {
        NewsApiResponse response = new NewsApiResponse();
        List<Article> articles = new ArrayList<>();
        articles.add(new Article());

        response.setArticles(articles);

        assertEquals(1, response.getArticles().size(), "There should be 1 article in the list");
    }
}
