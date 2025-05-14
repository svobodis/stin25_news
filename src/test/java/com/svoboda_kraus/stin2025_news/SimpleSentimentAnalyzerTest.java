package com.svoboda_kraus.stin2025_news;


import com.svoboda_kraus.stin2025_news.service.SimpleSentimentAnalyzer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleSentimentAnalyzerTest {

    @Test
    void testPositiveText() {
        String text = "This year saw strong growth and record profit with a huge boom in sales.";
        int result = SimpleSentimentAnalyzer.analyze(text);
        assertTrue(result > 0, "Expected positive sentiment score");
    }

    @Test
    void testNegativeText() {
        String text = "The company reported a loss, decline in revenue and looming bankruptcy.";
        int result = SimpleSentimentAnalyzer.analyze(text);
        assertTrue(result < 0, "Expected negative sentiment score");
    }

    @Test
    void testNeutralText() {
        String text = "Today is Monday and the sky is blue.";
        int result = SimpleSentimentAnalyzer.analyze(text);
        assertEquals(0, result, "Expected neutral sentiment score");
    }

    @Test
    void testNullInput() {
        int result = SimpleSentimentAnalyzer.analyze(null);
        assertEquals(0, result, "Expected 0 for null input");
    }

    @Test
    void testScoreIsClampedToMax10() {
        String text = "profit gain growth success strong high up win boom boost soar leader innovation "
                + "record milestone upgrade reward dominate breakthrough skyrocket";
        int result = SimpleSentimentAnalyzer.analyze(text);
        assertEquals(10, result);
    }

    @Test
    void testScoreIsClampedToMinMinus10() {
        String text = "loss decline crash drop fall plunge down miss fear risk collapse layoff bankruptcy "
                + "default recession fraud scandal corruption warning volatility penalty crisis";
        int result = SimpleSentimentAnalyzer.analyze(text);
        assertEquals(-10, result);
    }
}
