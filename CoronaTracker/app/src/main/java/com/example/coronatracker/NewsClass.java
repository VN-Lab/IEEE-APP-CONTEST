package com.example.coronatracker;

public class NewsClass {
    public NewsClass(String title, String text, String url, String imageUrl) {
        this.title = title;
        this.text = text;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    private String title,text,url,imageUrl;

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
