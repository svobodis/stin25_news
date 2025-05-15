package com.svoboda_kraus.stin2025_news;

import com.svoboda_kraus.stin2025_news.controller.NewsController;
import com.svoboda_kraus.stin2025_news.model.StockRecommendation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class NewsControllerTests {

    @InjectMocks
    private NewsController controller;

    @Test
    void testExecuteTrades_validBuyAndSellFlow() {
        List<StockRecommendation> input = new ArrayList<>();
        input.add(new StockRecommendation("AAPL", Instant.now().getEpochSecond(), 5, 0)); // buy
        input.add(new StockRecommendation("AAPL", Instant.now().getEpochSecond(), 5, 0)); // duplicate buy
        input.add(new StockRecommendation("AAPL", Instant.now().getEpochSecond(), 5, 1)); // sell
        input.add(new StockRecommendation("AAPL", Instant.now().getEpochSecond(), 5, 1)); // sell again (fail)

        List<String> result = controller.executeTrades(input);

        assertEquals(4, result.size());
        assertTrue(result.get(0).contains("Nakoupena akcie"));
        assertTrue(result.get(1).contains("Akcie"));
        assertTrue(result.get(2).contains("Prodaná akcie"));
        assertTrue(result.get(3).contains("Nelze prodat"));
    }

    @Test
    void testExecuteTrades_invalidInputs() {
        List<StockRecommendation> input = new ArrayList<>();
        input.add(new StockRecommendation(null, Instant.now().getEpochSecond(), 0, 1)); // chybí jméno
        input.add(new StockRecommendation("GOOG", -100, 0, 1)); // špatné datum
        input.add(new StockRecommendation("GOOG", Instant.now().getEpochSecond(), 20, 1)); // rating mimo rozsah
        input.add(new StockRecommendation("GOOG", Instant.now().getEpochSecond(), 5, null)); // chybí sell
        input.add(new StockRecommendation("GOOG", Instant.now().getEpochSecond(), 5, 2)); // neplatný sell

        List<String> result = controller.executeTrades(input);

        assertEquals(5, result.size());
        assertTrue(result.get(0).contains("chybí název"));
        assertTrue(result.get(1).contains("neplatné datum"));
        assertTrue(result.get(2).contains("rating mimo rozsah"));
        assertTrue(result.get(3).contains("chybí sell"));
        assertTrue(result.get(4).contains("sell musí být 0 nebo 1"));
    }


    @Test
    void testExecuteTrades_emptyInput() {
        List<String> result = controller.executeTrades(Collections.emptyList());

        assertEquals(1, result.size());
        assertEquals("ℹ️ Nebyly provedeny žádné operace.", result.get(0));
    }


}
