package com.hfad.bruceleequotes;

public class Quote {
    int id;
    String quote;
    boolean viewed, favorite;

    public Quote(int id, String quote, boolean viewed, boolean favorite) {
        this.id = id;
        this.quote = quote;
        this.viewed = viewed;
        this.favorite = favorite;
    }

    public int getId(){
        return id;
    }

    public String getQuote(){
        return quote;
    }

    public boolean getViewed() {
        return viewed;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
