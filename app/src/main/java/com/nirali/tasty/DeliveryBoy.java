package com.nirali.tasty;

public class DeliveryBoy {
    private String id;
    private String name;
    private String number;
    private String imagePath;
    private boolean status;

    // Empty constructor required for Firebase
    public DeliveryBoy() {}

    public DeliveryBoy(String id, String name, String number, String imagePath, boolean status) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.imagePath = imagePath;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

