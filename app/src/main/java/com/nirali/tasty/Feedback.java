package com.nirali.tasty;

public class Feedback {
    private String userID;
    private String itemID;
    private int review;

    // Default constructor required for calls to DataSnapshot.getValue(Feedback.class)
    public Feedback() {
    }

    public Feedback(String userID, String itemID, int review) {
        this.userID = userID;
        this.itemID = itemID;
        this.review = review;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }
}
