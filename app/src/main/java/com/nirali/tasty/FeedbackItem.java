package com.nirali.tasty;

public class FeedbackItem {
    private String itemID;
    private String itemName;
    private String itemImage;
    private String username;
    private String review;

    public FeedbackItem(String itemID, String itemName, String itemImage, String username, String review) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.username = username;
        this.review = review;
    }

    public String getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getUsername() {
        return username;
    }

    public String getReview() {
        return review;
    }
}
