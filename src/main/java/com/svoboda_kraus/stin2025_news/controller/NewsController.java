package com.svoboda_kraus.stin2025_news.controller;

import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.RatedArticleGroup;
import com.svoboda_kraus.stin2025_news.service.ArticleFilter;
import com.svoboda_kraus.stin2025_news.service.NewsApiClient;
import com.svoboda_kraus.stin2025_news.service.SimpleSentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/liststock")
public class NewsController {

    @Autowired
    private NewsApiClient newsApiClient;

    @PostMapping
    public List<RatedArticleGroup> listStock(@RequestBody List<String> stockNames,
                                             @RequestParam(defaultValue = "3") int minArticles,
                                             @RequestParam(defaultValue = "false") boolean allowNegative) {

        List<RatedArticleGroup> rawGroups = new ArrayList<>();

        for (String name : stockNames) {
            List<Article> articles = newsApiClient.fetchNews(name, 15);

            for (Article article : articles) {
                String fullText = article.getTitle() + " " + article.getDescription();
                int score = SimpleSentimentAnalyzer.analyze(fullText);
                article.setRating(score);
            }

            rawGroups.add(new RatedArticleGroup(name, articles));
        }

        // 🔧 APLIKUJ FILTR
        ArticleFilter filter = new ArticleFilter(minArticles, allowNegative);
        return filter.filter(rawGroups);
    }
}
