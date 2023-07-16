package com.example.h2ak.controllers;

import androidx.annotation.NonNull;

import com.example.h2ak.firebase.FirebaseHelper;
import com.example.h2ak.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserController {
    private FirebaseUser firebaseUser;

    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getUser(UserService userService) {
        if (firebaseUser != null) {
            Query query = FirebaseHelper.getFirebaseDatabaseUser().child(firebaseUser.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //Get user
                        User user = snapshot.getValue(User.class);
                        //Callback interface getUser
                            userService.getUser(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
