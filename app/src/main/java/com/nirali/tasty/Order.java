package com.nirali.tasty;

import java.util.ArrayList;

public class Order {
    private String orderID; // Unique identifier for the order
    private String userID; // User who placed the order
    private String deliveryAddress; // Delivery address for the order
    private double totalAmount; // Total amount for the order
    private String status; // Current status of the order (e.g., "Pending", "Shipped")
    private String orderDate; // Date the order was placed
    private ArrayList<Item> items; // List of items in the order

    // Default constructor required for Firebase
    public Order() {
    }

    // Parameterized constructor for initializing order details
    public Order(String orderID, String userID, String deliveryAddress, double totalAmount, String status, String orderDate, ArrayList<Item> items) {
        this.orderID = orderID;
        this.userID = userID;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.items = items;
    }

    // Getters and setters for all fields

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID='" + orderID + '\'' +
                ", userID='" + userID + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", items=" + items +
                '}';
    }
}
