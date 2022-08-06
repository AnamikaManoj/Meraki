package com.example.meraki.model;

public class User {

    private String Name;
    private String Email;
    private String UserName;
    private String bio;
    private String imageurl;
    private String id;

    public User() {
    }

    public User(String name, String email, String username, String bio, String imageurl, String id) {
        this.Name = name;
        this.Email = email;
        this.UserName = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getUsername() {
        return UserName;
    }

    public void setUsername(String username) {
        this.UserName = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
