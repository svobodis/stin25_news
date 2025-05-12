package com.svoboda_kraus.stin2025_news.model;

public class StockRecommendation {

    private String name;
    private long date;
    private int rating;
    private int sell; // 0 = koupit, 1 = prodat

    public StockRecommendation() {
    }

    public StockRecommendation(String name, long date, int rating, int sell) {
        this.name = name;
        this.date = date;
        this.rating = rating;
        this.sell = sell;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }

    public int getRating() {
        return rating;
    }

    public int getSell() {
        return sell;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setSell(int sell) {
        this.sell = sell;
    }

   
    @Override
    public String toString() {
        return "StockRecommendation{" +
               "name='" + name + '\'' +
               ", date=" + date +
               ", rating=" + rating +
               ", sell=" + sell +
               '}';
    }
    
}

