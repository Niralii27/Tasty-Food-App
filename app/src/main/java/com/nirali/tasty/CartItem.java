package com.nirali.tasty;

//public class CartItem {
//
//    private String productID;  // Add this field if it doesn’t exist
//
//    private String productName;
//
//
//    private String productImageUrl;
//    private float originalPrice;
//    private float totalPrice;
//    private int quantity;
//

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productID;
    private String productName;
    private int quantity;
    private String productImageUrl;
    private float originalPrice;
    private float totalPrice;

    public CartItem(String productID, String productName, String productImageUrl, float originalPrice, float totalPrice, int quantity) {
        this.productID = productID;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.originalPrice = originalPrice;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public CartItem(String productName, String productImageUrl, float originalPrice, float totalPrice, int quantity) {
        this.productID = productID;

        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.originalPrice = originalPrice;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    // Getters

    public String getProductID() {
        return productID;
    }
    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }
}
