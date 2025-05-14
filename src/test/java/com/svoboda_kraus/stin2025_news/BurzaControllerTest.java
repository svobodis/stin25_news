package com.svoboda_kraus.stin2025_news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svoboda_kraus.stin2025_news.model.StockRecommendation;
import com.svoboda_kraus.stin2025_news.service.BurzaController;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BurzaControllerTest {

    private static MockWebServer mockNewsServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BurzaController controller;

    @BeforeAll
    static void startServer() throws Exception {
        mockNewsServer = new MockWebServer();
        mockNewsServer.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        mockNewsServer.shutdown();
    }

    @DynamicPropertySource
    static void registerBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("news.base-url", () -> mockNewsServer.url("/").toString());
    }

    @Test
    void testGetRecommendations() throws Exception {
        // === PŘÍPRAVA TESTOVÝCH DAT ===
        List<StockRecommendation> input = List.of(
                new StockRecommendation("Apple", 1, 4, 0),
                new StockRecommendation("Tesla", 1, -1, 0)
        );

        // Odpověď z prvního POST /liststock/rating
        mockNewsServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(input))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // Odpověď z druhého POST /liststock/salestock
        List<StockRecommendation> expected = List.of(
                new StockRecommendation("Apple", 1, 4, 1),
                new StockRecommendation("Tesla", 1, -1, 0)
        );
        mockNewsServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(expected))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        List<StockRecommendation> result = controller.getRecommendations(input, 0);

        assertEquals(2, result.size());
        assertEquals("Apple", result.get(0).getName());
        assertEquals(1, result.get(0).getSell());
        assertEquals(0, result.get(1).getSell());
    }
}
