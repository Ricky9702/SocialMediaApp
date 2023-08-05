package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.User;

import java.util.List;

public interface FirebaseUserDataSource {
    void createUser(User user);
    void updateUser(User user);
    void getAllUsers();
    void updateOnlineField(User user);
}
