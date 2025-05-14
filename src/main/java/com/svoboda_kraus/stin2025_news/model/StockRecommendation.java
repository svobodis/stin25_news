package com.svoboda_kraus.stin2025_news.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockRecommendation {
    private String  name;
    private long    date;
    private int     rating;
    private Integer sell;

    public StockRecommendation() { }

    // Konstruktor pro Zprávy → pošle jen name, date, rating
    public StockRecommendation(String name, long date, int rating) {
        this.name   = name;
        this.date   = date;
        this.rating = rating;
    }

    // Plný konstruktor
    public StockRecommendation(String name, long date, int rating, Integer sell) {
        this.name   = name;
        this.date   = date;
        this.rating = rating;
        this.sell   = sell;
    }

    // getters + setters…
    public String  getName()   { return name; }
    public void    setName(String n)   { name   = n; }
    public long    getDate()   { return date; }
    public void    setDate(long d)      { date   = d; }
    public int     getRating() { return rating; }
    public void    setRating(int r)     { rating = r; }
    public Integer getSell()   { return sell; }
    public void    setSell(Integer s)   { sell   = s; }
}
