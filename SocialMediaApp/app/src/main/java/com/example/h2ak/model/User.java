package com.example.h2ak.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.h2ak.utils.TextInputLayoutUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String email;
    private String gender;
    private String birthday;
    private String password;
    private String imageAvatar = "";
    private String imageCover = "";
    private String bio = "";
    private String createdDate;
    private boolean isActive;
    private UserRole userRole;
    private String role;
    private boolean isOnline;

    {
        this.createdDate = TextInputLayoutUtils.simpleDateFormat.format(new Date());
        this.isOnline = false;
        this.userRole = UserRole.ROLE_USER;
        this.role = userRole.getRole();
    }


    public User() {
    }

    public User(String id, String name, String email, String gender, String birthday, String password, String imageAvatar, String imageCover, String bio, String createdDate, String dateFormat, boolean isActive) {
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
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isActive == user.isActive && isOnline == user.isOnline && id.equals(user.id) && name.equals(user.name) && email.equals(user.email) && gender.equals(user.gender) && birthday.equals(user.birthday) && password.equals(user.password) && imageAvatar.equals(user.imageAvatar) && imageCover.equals(user.imageCover) && bio.equals(user.bio) && createdDate.equals(user.createdDate) && role.equals(user.role);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        return result;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public enum UserRole {
        ROLE_ADMIN("ADMIN"),
        ROLE_USER("USER");

        private String role;

        UserRole(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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