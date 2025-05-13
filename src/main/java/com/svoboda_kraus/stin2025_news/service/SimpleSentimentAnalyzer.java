package com.svoboda_kraus.stin2025_news.service;

import java.util.Arrays;
import java.util.List;

public class SimpleSentimentAnalyzer {

    private static final List<String> POSITIVE_WORDS = Arrays.asList(
            "profit", "gain", "growth", "increase", "success", "strong", "surge", "rise", "record",
            "improvement", "beat", "optimism", "bullish", "high", "expansion", "positive", "outperform",
            "up", "recovery", "win", "boom", "stability", "resilient", "achievement",
            "advancement", "progress", "boost", "favorable", "exceed", "best", "reward", "solid", "soar",
            "momentum", "upgrade", "lead", "leader", "dominance", "breakthrough", "innovation", "solution",
            "accelerate", "resurgence", "green", "record-breaking", "skyrocket", "milestone", "revival"
    );

    private static final List<String> NEGATIVE_WORDS = Arrays.asList(
            "loss", "decline", "crash", "drop", "weaken", "bad", "lab", "fall", "plunge", "down",
            "miss", "cut", "fear", "pessimism", "bearish", "low", "shrink", "negative", "underperform",
            "risk", "collapse", "layoff", "recession", "instability", "debt", "fraud", "scandal",
            "bankruptcy", "default", "downgrade", "lawsuit", "inflation", "deficit", "turmoil",
            "slump", "warning", "unemployment", "declining", "volatile", "crisis", "corruption",
            "stagnation", "fallout", "exposure", "hacker", "breach", "penalty", "violation"
    );

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
