package com.nirali.tasty;

import java.util.List;

public class OrderItem {
    private String address;
    private List<String> itemIDs;
    private List<String> itemNames;
    private String orderDate;
    private String orderID;
    private String paymentMethod;
    private List<Integer> quantities;
    private String status;
    private int totalAmount;
    private String trackingNumber;
    private String userID;

    private String itemImageURL;


    // Empty constructor for Firebase
    public OrderItem() {}

    

    // Getters and setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<String> getItemIDs() { return itemIDs; }
    public void setItemIDs(List<String> itemIDs) { this.itemIDs = itemIDs; }

    public List<String> getItemNames() { return itemNames; }
    public void setItemNames(List<String> itemNames) { this.itemNames = itemNames; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<Integer> getQuantities() { return quantities; }
    public void setQuantities(List<Integer> quantities) { this.quantities = quantities; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getItemImageURL() {
        return itemImageURL; // Replace this with the actual field in your OrderItem class.
    }

}


