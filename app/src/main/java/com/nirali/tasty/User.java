package com.nirali.tasty;

public class User {
    private String id;

    private String name;
    private String email;
    private String number;
    private String birthDate;
    private String profilePicture;

    // Empty constructor for Firebase
    public User() {}

    public User(String id,String name, String email, String phone, String address, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = phone;
        this.birthDate = address;
        this.profilePicture = profilePicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture)
    {
        this.profilePicture = profilePicture;
    }


}
