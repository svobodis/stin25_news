package com.svoboda_kraus.stin2025_news.service;

import java.util.HashMap;
import java.util.Map;

public class SimpleSentimentAnalyzer {

    private static final Map<String, Integer> SENTIMENT_WEIGHTS = new HashMap<>();

    static {
        // Pozitivní
        for (String word : new String[]{
            "profit", "gain", "growth", "success", "strong", "surge", "rise",
            "record", "optimism", "bullish", "soar", "breakthrough", "innovation",
            "recovery", "resilient", "outperform", "exceed", "boost", "high",
            "skyrocket", "accelerate", "expansion", "win", "solution", "achievement",
            "milestone", "momentum", "improvement", "upgrade", "favorable"
        }) {
            SENTIMENT_WEIGHTS.put(word, 5);
        }

        SENTIMENT_WEIGHTS.put("breakthrough", 4);
        SENTIMENT_WEIGHTS.put("success", 3);
        SENTIMENT_WEIGHTS.put("surge", 6);
        SENTIMENT_WEIGHTS.put("soar", 3);
        SENTIMENT_WEIGHTS.put("record-breaking", 9);

        // Negativní
        for (String word : new String[]{
            "loss", "decline", "drop", "fall", "cut", "miss", "bad", "risk",
            "instability", "underperform", "debt", "scandal", "layoff", "downturn",
            "fraud", "collapse", "bankruptcy", "warning", "volatile", "negative",
            "crash", "plunge", "bearish", "slump", "turmoil", "recession", "fear",
            "lawsuit", "downgrade", "default"
        }) {
            SENTIMENT_WEIGHTS.put(word, -3);
        }

        SENTIMENT_WEIGHTS.put("crash", -8);
        SENTIMENT_WEIGHTS.put("plunge", -3);
        SENTIMENT_WEIGHTS.put("scandal", -3);
        SENTIMENT_WEIGHTS.put("bankruptcy", -4);
        SENTIMENT_WEIGHTS.put("collapse", -4);
        SENTIMENT_WEIGHTS.put("fraud", -3);
        SENTIMENT_WEIGHTS.put("recession", -3);
        SENTIMENT_WEIGHTS.put("turmoil", -3);
        SENTIMENT_WEIGHTS.put("warning", -2);
    }

    public static int analyze(String text) {
        if (text == null || text.isBlank()) return 0;

        String lower = text.toLowerCase().replaceAll("[^a-z\\s]", " ");
        int totalScore = 0;

        for (Map.Entry<String, Integer> entry : SENTIMENT_WEIGHTS.entrySet()) {
            String word = entry.getKey();
            int weight = entry.getValue();
            int count = countOccurrences(lower, word);
            totalScore += count * weight;
        }

        return Math.max(-10, Math.min(10, totalScore));
    }

    private static int countOccurrences(String text, String word) {
        int count = 0, index = 0;
        while ((index = text.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        return count;
    }
}
