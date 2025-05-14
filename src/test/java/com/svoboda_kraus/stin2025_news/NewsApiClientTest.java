package com.svoboda_kraus.stin2025_news;

import com.svoboda_kraus.stin2025_news.model.Article;
import com.svoboda_kraus.stin2025_news.model.NewsApiResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.*;
import com.svoboda_kraus.stin2025_news.service.NewsApiClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsApiClientTest {

    private WebClient mockWebClient;
    private WebClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;
    private WebClient.RequestHeadersSpec mockRequestHeadersSpec;
    private WebClient.ResponseSpec mockResponseSpec;

    private NewsApiClient client;

    @BeforeEach
    void setUp() {
        mockWebClient = mock(WebClient.class);
        mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        mockResponseSpec = mock(WebClient.ResponseSpec.class);

        client = new NewsApiClient() {
            {
                // override final field for testing
                java.lang.reflect.Field field = null;
                try {
                    field = NewsApiClient.class.getDeclaredField("webClient");
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                field.setAccessible(true);
                try {
                    field.set(this, mockWebClient);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testFetchNewsReturnsArticles() {
        // Prepare mock response
        Article article = new Article();
        article.setTitle("Test Article");

        NewsApiResponse mockResponse = new NewsApiResponse();
        mockResponse.setArticles(Arrays.asList(article));

        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(any(Function.class))).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(NewsApiResponse.class)).thenReturn(Mono.just(mockResponse));

        long epoch = Instant.now().getEpochSecond();
        List<Article> articles = client.fetchNews("test", epoch);

        assertNotNull(articles);
        assertEquals(1, articles.size());
        assertEquals("Test Article", articles.get(0).getTitle());
    }

    @Test
    void testFetchNewsReturnsEmptyListOnError() {
        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(any(Function.class))).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(NewsApiResponse.class)).thenReturn(Mono.error(new RuntimeException("Failed")));

        long epoch = Instant.now().getEpochSecond();
        List<Article> articles = client.fetchNews("fail", epoch);

        assertNotNull(articles);
        assertTrue(articles.isEmpty());
    }

    @Test
    void testFetchNewsReturnsEmptyListIfResponseIsNull() {
        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(any(Function.class))).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(NewsApiResponse.class)).thenReturn(Mono.empty());

        long epoch = Instant.now().getEpochSecond();
        List<Article> articles = client.fetchNews("null-response", epoch);

        assertNotNull(articles);
        assertTrue(articles.isEmpty());
    }

    @Test
    void testFetchNewsReturnsEmptyListIfArticlesAreNull() {
        NewsApiResponse emptyResponse = new NewsApiResponse(); // getArticles() returns null
        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(any(Function.class))).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(NewsApiResponse.class)).thenReturn(Mono.just(emptyResponse));

        long epoch = Instant.now().getEpochSecond();
        List<Article> articles = client.fetchNews("null-articles", epoch);

        assertNotNull(articles);
        assertTrue(articles.isEmpty());
    }


}
