package com.nirali.tasty;

public class Product {

    private String categoryId;  // Ensure this exists in Product class
    private String productID;  // Add productID field

    private Boolean active;

    int  Status;
    public String name;
    public String imageUrl;
    public String description;

    public String price;
    private double averageRating;  // Add this field for average rating


    public  Product()
{

}
    public Product(String productID, String categoryId, String name, String imageUrl, String price, String description) {
        this.productID = productID;
        this.categoryId = categoryId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
    }


    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getPrice()
    {
        return price;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    public boolean isActive() { return active; } // Getter for active
    public void setActive(boolean active) { this.active = active; }

    public Product(String name,String imageUrl,String price,String description)
    {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
    }

}
