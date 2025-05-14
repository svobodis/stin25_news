package com.svoboda_kraus.stin2025_news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svoboda_kraus.stin2025_news.controller.NewsController;
import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.StockRecommendation;
import com.svoboda_kraus.stin2025_news.service.NewsApiClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsApiClient newsApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListStockReturnsRatedArticles() throws Exception {
        Article article = new Article();
        article.setTitle("Good news");
        article.setDescription("The company is growing fast.");

        Mockito.when(newsApiClient.fetchNews("AAPL", 7))
                .thenReturn(List.of(article));

        mockMvc.perform(post("/liststock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minArticles", "1")
                        .param("allowNegative", "true")
                        .content(objectMapper.writeValueAsString(List.of("AAPL"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockName").value("AAPL"))
                .andExpect(jsonPath("$[0].articles.length()").value(1))
                .andExpect(jsonPath("$[0].articles[0].rating").isNumber());
    }

    @Test
    void testRateStocksReturnsCorrectRatings() throws Exception {
        Article article = new Article();
        article.setTitle("Great product");
        article.setDescription("Positive market response.");

        Mockito.when(newsApiClient.fetchNews("TSLA", 5))
                .thenReturn(List.of(article));

        StockRecommendation request = new StockRecommendation("TSLA",
                Instant.now().minus(5, ChronoUnit.DAYS).getEpochSecond(), 0);

        mockMvc.perform(post("/liststock/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("TSLA"))
                .andExpect(jsonPath("$[0].rating").isNumber());
    }

    @Test
    void testExecuteTradesAddsToPortfolio() throws Exception {
        StockRecommendation buy = new StockRecommendation("NVDA",
                Instant.now().getEpochSecond(), 5);
        buy.setSell(0); // Buy

        mockMvc.perform(post("/liststock/salestock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(buy))))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("✅ Nakoupena akcie: NVDA")));
    }

    @Test
    void testGetPortfolioAfterTrade() throws Exception {
        // First buy something
        StockRecommendation buy = new StockRecommendation("GOOG",
                Instant.now().getEpochSecond(), 3);
        buy.setSell(0);

        mockMvc.perform(post("/liststock/salestock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(buy))))
                .andExpect(status().isOk());

        // Then fetch portfolio
        mockMvc.perform(get("/liststock/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("GOOG"));
    }



}
