package com.example.omadmin.models;

public class FoodRecyclerModel {
    private String name;
    private String foodUrl;
    private String restaurantID;

    public FoodRecyclerModel() {
    }

    public FoodRecyclerModel(String name, String foodUrl, String restaurantID) {
        this.name = name;
        this.foodUrl = foodUrl;
        this.restaurantID = restaurantID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodUrl() {
        return foodUrl;
    }

    public void setFoodUrl(String foodUrl) {
        this.foodUrl = foodUrl;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }
}
