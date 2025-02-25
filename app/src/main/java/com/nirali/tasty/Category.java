package com.nirali.tasty;

public class Category {

    private String categoryId;  // Ensure this exists in Category class
    int  Status;
    public String name;
    public String iurl;
    private boolean active;

    public Category()
    {

    }

    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Category(String name,String iurl)
    {
        this.categoryId = getCategoryId();
        this.name = name;
        this.iurl = iurl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
