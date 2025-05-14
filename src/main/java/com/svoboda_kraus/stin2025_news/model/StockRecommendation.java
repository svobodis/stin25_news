package com.svoboda_kraus.stin2025_news.model;

public class StockRecommendation {
    private String name;
    private long date;
    private int rating;
    private Integer sell; // změna z int na Integer

    // Konstruktor
    public StockRecommendation(String name, long date, int rating, Integer sell) {
        this.name = name;
        this.date = date;
        this.rating = rating;
        this.sell = sell;
    }

    // Gettery a settery
    public String getName() { return name; }
    public long getDate() { return date; }
    public int getRating() { return rating; }
    public Integer getSell() { return sell; }

    public void setName(String name) { this.name = name; }
    public void setDate(long date) { this.date = date; }
    public void setRating(int rating) { this.rating = rating; }
    public void setSell(Integer sell) { this.sell = sell; }

    @Override
    public String toString() {
        return "StockRecommendation{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", rating=" + rating +
                '}';
    }
}
