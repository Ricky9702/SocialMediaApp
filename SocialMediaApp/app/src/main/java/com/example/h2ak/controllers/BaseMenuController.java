package com.example.h2ak.controllers;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.h2ak.R;
import com.example.h2ak.database.FirebaseDatabaseHelper;
import com.example.h2ak.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BaseMenuController {
    private FirebaseUser firebaseUser;
    private ProfileImageChangeListener profileImageChangeListener;

    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public BaseMenuController(ProfileImageChangeListener instanceClassImplements) {
        profileImageChangeListener = instanceClassImplements;
    }

    public void changeProfileImage() {
        if (firebaseUser != null) {
            Query query = FirebaseDatabaseHelper.getFirebaseDatabaseUser().child(firebaseUser.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //Get user
                        User user = snapshot.getValue(User.class);
                        String avatar = user.getAvatar();
                        //Instance class which implements ProfileImageChangeListener will call the method
                        if (profileImageChangeListener != null)
                            profileImageChangeListener.onProfileImageChanged(avatar);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
