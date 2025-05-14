package com.svoboda_kraus.stin2025_news.service;

import com.svoboda_kraus.stin2025_news.model.StockRecommendation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class BurzaClient {

    private final WebClient client;


        public BurzaClient() {
            this.client = WebClient.builder()
                .baseUrl("http://localhost:8080/mock/burza")
                .build();
        }
        

    /**
     * Pošle ratingy zpátky na burzu.
     * @param recs seznam StockRecommendation bez sell (sell==null → vynecháno)
     * @return 
     */
    public List<StockRecommendation> sendRatings(List<StockRecommendation> recs) {
        client.post()
            .uri("/rating")  // nebo endpoint, který od partnera čekají
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(recs)
            .retrieve()
            .bodyToMono(Void.class)   
            .block();                 // nebo .subscribe() v non-blocking aplikaci
                    return recs;
    }
}
