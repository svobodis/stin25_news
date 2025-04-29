package com.svoboda_kraus.stin2025_news.service;

import java.util.Arrays;
import java.util.List;

public class SimpleSentimentAnalyzer {

    private static final List<String> POSITIVE_WORDS = Arrays.asList("profit", "gain", "growth", "increase", "success", "strong");
    private static final List<String> NEGATIVE_WORDS = Arrays.asList("loss", "decline", "crash", "drop", "weaken", "bad", "lab");

    public static String analyze(String text) {
        if (text == null) return "neutral";

        String lower = text.toLowerCase();
        long positives = POSITIVE_WORDS.stream().filter(lower::contains).count();
        long negatives = NEGATIVE_WORDS.stream().filter(lower::contains).count();

        if (positives > negatives) return "positive";
        if (negatives > positives) return "negative";
        return "neutral";
    }
}
