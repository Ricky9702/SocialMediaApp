package com.example.h2ak.model;


import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String gender;
    private String birthday;
    private String password;
    private String imageAvatar;
    private String imageCover;
    private String bio;
    private String createdDate;
    String dateFormat;

    {
        Date date = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private boolean isActive;

    public User() {
        this.createdDate = dateFormat;
    }

    public User(int id, String name, String email, String gender, String birthday, String password, String imageAvatar, String imageCover, String bio, String createdDate, String dateFormat, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.password = password;
        this.imageAvatar = imageAvatar;
        this.imageCover = imageCover;
        this.bio = bio;
        this.createdDate = createdDate;
        this.dateFormat = dateFormat;
        this.isActive = isActive;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("ID: %d - Name: %s - Email: %s- Password: %s - Active: %s - Avatar: %s - Cover: %s - Bio: %s - createdDate: %s",
                this.id, this.name, this.email, this.password, this.isActive, this.imageAvatar, this.imageCover, this.bio, this.createdDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

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

    public String getImageAvatar() {
        return imageAvatar;
    }

    public void setImageAvatar(String imageAvatar) {
        this.imageAvatar = imageAvatar;
    }

    public String getImageCover() {
        return imageCover;
    }

    public void setImageCover(String imageCover) {
        this.imageCover = imageCover;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}