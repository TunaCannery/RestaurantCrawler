package com.crawl.pojo;



public class ItemStoringJob {
    private double rating;
    private String restaurant;
    private String restaurantCode;
    private String category;
    private String item_id;
    private String item_name;
    private double item_price;

    public String getRestaurantCode() {
        return restaurantCode;
    }

    public void setRestaurantCode(String restaurantCode) {
        this.restaurantCode = restaurantCode;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public double getItem_price() {
        return item_price;
    }

    public void setItem_price(double item_price) {
        this.item_price = item_price;
    }



    public ItemStoringJob(double rating, String restaurant, String category, String item_id, String item_name, double item_price, String restaurantCode) {
        this.rating = rating;
        this.restaurant = restaurant;
        this.category = category;
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_price = item_price;
        this.restaurantCode = restaurantCode;
    }
}
