package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.User;

import java.util.List;
import java.util.Map;

public interface UserDataSource {
    //Add User
    boolean createUser(User user);
    User getUserById(String id);
    //Get All User
    List<User> getAllUsers(Map<String, String> params);
    //Update User by id
    boolean updateCurrentUser(User user);
    boolean updateUserChangeOnFirebase(User user);
    //Delete User by id
    boolean deleteUser(User user);
}