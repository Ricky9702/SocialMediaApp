package com.example.h2ak.pojo;

import java.util.Date;

public class User {

    private String id;
    private String email;
    private String username;
    private Date createdDate;
    private String avatar;
    private String phone;

    public  User()
    {

    }
    public User(String id, String email, String username, String avatar, String phone) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.createdDate = new Date();
        this.avatar = avatar;
        this. phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
