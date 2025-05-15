package com.svoboda_kraus.stin2025_news.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svoboda_kraus.stin2025_news.model.StockRecommendation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mock")
public class BurzaController {

    private final WebClient newsClient;
    private final ObjectMapper objectMapper = new ObjectMapper(); // pro logování JSONů

    public BurzaController(@Value("${news.base-url}") String baseUrl) {
        this.newsClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @PostMapping("/rating")
    public void acceptRatingFromZpravy(@RequestBody List<StockRecommendation> rated,
                                       @RequestParam(defaultValue = "0") int sellThreshold) {

        try {
            System.out.println("📨 BURZA přijala od ZPRÁV JSON:");
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rated));
        } catch (JsonProcessingException e) {
            System.out.println("❌ Chyba při logování přijatého JSONu");
        }

        List<StockRecommendation> withSell = rated.stream()
                .map(r -> new StockRecommendation(
                        r.getName(), r.getDate(), r.getRating(),
                        r.getRating() > sellThreshold ? 1 : 0))
                .collect(Collectors.toList());

        try {
            System.out.println("📤 BURZA odesílá zpět do ZPRÁV JSON:");
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(withSell));
        } catch (JsonProcessingException e) {
            System.out.println("❌ Chyba při logování odesílaného JSONu");
        }

        newsClient.post()
                .uri("/liststock/salestock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(withSell)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
