package com.svoboda_kraus.stin2025_news.service;

import java.util.HashMap;
import java.util.Map;

public class SimpleSentimentAnalyzer {

    private static final Map<String, Integer> SENTIMENT_WEIGHTS = new HashMap<>();

    static {
        // Pozitivní slova s váhami
        SENTIMENT_WEIGHTS.put("profit", 2);
        SENTIMENT_WEIGHTS.put("gain", 2);
        SENTIMENT_WEIGHTS.put("growth", 2);
        SENTIMENT_WEIGHTS.put("success", 3);
        SENTIMENT_WEIGHTS.put("strong", 2);
        SENTIMENT_WEIGHTS.put("surge", 3);
        SENTIMENT_WEIGHTS.put("rise", 1);
        SENTIMENT_WEIGHTS.put("record", 2);
        SENTIMENT_WEIGHTS.put("optimism", 2);
        SENTIMENT_WEIGHTS.put("bullish", 2);
        SENTIMENT_WEIGHTS.put("soar", 3);
        SENTIMENT_WEIGHTS.put("breakthrough", 4);
        SENTIMENT_WEIGHTS.put("innovation", 3);

        // Negativní slova s váhami
        SENTIMENT_WEIGHTS.put("loss", -2);
        SENTIMENT_WEIGHTS.put("decline", -2);
        SENTIMENT_WEIGHTS.put("crash", -4);
        SENTIMENT_WEIGHTS.put("drop", -2);
        SENTIMENT_WEIGHTS.put("bad", -1);
        SENTIMENT_WEIGHTS.put("fall", -2);
        SENTIMENT_WEIGHTS.put("plunge", -3);
        SENTIMENT_WEIGHTS.put("scandal", -3);
        SENTIMENT_WEIGHTS.put("bankruptcy", -4);
        SENTIMENT_WEIGHTS.put("fraud", -3);
        SENTIMENT_WEIGHTS.put("recession", -3);
        SENTIMENT_WEIGHTS.put("layoff", -2);
        SENTIMENT_WEIGHTS.put("collapse", -4);
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

        // Omezíme rozsah výsledku
        if (totalScore > 10) return 10;
        if (totalScore < -10) return -10;
        return totalScore;
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
