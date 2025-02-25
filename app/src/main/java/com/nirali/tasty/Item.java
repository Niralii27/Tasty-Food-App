package com.nirali.tasty;

public class Item {
    private String itemID;
    private String name;
    private int quantity;
    private String price;

    public Item(String itemID, String name, int quantity, String price) {
        this.itemID = itemID;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}
