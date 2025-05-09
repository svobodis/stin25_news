package com.svoboda_kraus.stin2025_news.service;

import java.util.Arrays;
import java.util.List;

public class SimpleSentimentAnalyzer {

    private static final List<String> POSITIVE_WORDS = Arrays.asList("profit", "gain", "growth", "increase", "success", "strong");
    private static final List<String> NEGATIVE_WORDS = Arrays.asList("loss", "decline", "crash", "drop", "weaken", "bad", "lab");

    public static int analyze(String text) {
        if (text == null) return 0;

        String lower = text.toLowerCase();

        int positive = (int) POSITIVE_WORDS.stream().filter(lower::contains).count();
        int negative = (int) NEGATIVE_WORDS.stream().filter(lower::contains).count();

        int score = positive - negative;

        if (score > 10) return 10;
        return Math.max(score, -10);
    }
}
