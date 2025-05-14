package com.svoboda_kraus.stin2025_news.service;
import com.svoboda_kraus.stin2025_news.model.StockRecommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/burza")
public class BurzaController {

    private final WebClient newsClient;

    @Value("${news.base-url}")
    private String baseUrl;
    @Autowired
    public BurzaController(@Value("${news.base-url}") String baseUrl) {
        this.newsClient = WebClient.builder()
            .baseUrl(baseUrl)   // URL modulu zprávy
            .build();
    }

    /**
     * Krok I: na manuální start nebo CRON volání.
     * Tady by se normálně získávala data z burzy (historie), filtrovala by se podle cen.
     * Pro jednoduchost předpokládáme, že dostaneme jména+timestamp.
     */
    @PostMapping("/getRecommendations")
    public List<StockRecommendation> getRecommendations(
            @RequestBody List<StockRecommendation> stocks,
            @RequestParam(defaultValue = "0") int sellThreshold) {

        // 1) Získej hodnocení od modulu Zprávy
        List<StockRecommendation> rated = newsClient.post()
            .uri("/liststock/rating")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(stocks)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<StockRecommendation>>() {})
            .block();

        // 2) Aplikuj threshold → doplň sell
        List<StockRecommendation> withSell = rated.stream()
            .map(r -> {
                int sell = r.getRating() > sellThreshold ? 1 : 0;
                return new StockRecommendation(r.getName(), r.getDate(), r.getRating(), sell);
            })
            .collect(Collectors.toList());

        // 3) Pošli zpátky do Zpráv (krok II zpráv)
        List<StockRecommendation> finalResp = newsClient.post()
            .uri("/liststock/salestock")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(withSell)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<StockRecommendation>>() {})
            .block();

        return finalResp;
    }
}
