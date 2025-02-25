package com.nirali.tasty;

public class Contact {
    private String name;
    private String email;
    private String number;
    private String address;
    private String message;

    // Default constructor required for Firebase
    public Contact() {}

    // Constructor
    public Contact(String name, String email, String number, String address, String message) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.address = address;
        this.message = message;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
