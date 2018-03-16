package com.pixectra.app.Models;

/**
 * Created by XCODER on 2/10/2018.
 */

public class Product {
    private String id;
    private String type;
    private String title;
    private String url;
    private int maxPics;
    private int minPics;
    private int price;
    private String priceDesc;

    public Product() {

    }

    public Product(String type, String title, String url, int maxPics, int minPics, int price, String priceDesc) {
        this.type = type;
        this.title = title;
        this.url = url;
        this.maxPics = maxPics;
        this.minPics = minPics;
        this.price = price;
        this.priceDesc = priceDesc;
        id = type + "~" + title;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getMaxPics() {
        return maxPics;
    }

    public int getMinPics() {
        return minPics;
    }

    public int getPrice() {
        return price;
    }

    public String getPriceDesc() {
        return priceDesc;
    }
}
