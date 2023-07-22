package com.example.h2ak.datasource;

import com.example.h2ak.model.User;

import java.util.List;

public interface UserDataSource {
    //Add User
    User addUser(User user);
    //Get User
    User getUserById(int userId);
    User getUserByEmail(String email);
    //Get All User
    List<User> getAllUsers();
    //Update User by id
    boolean updateUser(User user);
    boolean updateActiveUser(User user);
    //Delete User by id
    void deleteUser(int userId);

}
