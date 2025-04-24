package com.svoboda_kraus.stin2025_news.controller;

import com.svoboda_kraus.stin2025_news.service.NewsApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/liststock")
public class NewsController {

    @Autowired
    private NewsApiClient newsApiClient;

    @PostMapping
    public List<String> listStock(@RequestBody List<String> stockNames) {
        for (String name : stockNames) {
            String response = newsApiClient.fetchNews(name);
            System.out.println("Zprávy pro " + name + ":");
            System.out.println(response); // zatím jen tiskneme surový JSON do konzole
        }
        return List.of(); // zatím vracíme prázdný list
    }
}
