package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseUserDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUserDataSourceImpl implements FirebaseUserDataSource {

    private DatabaseReference userRef;
    private static FirebaseUserDataSourceImpl instance;
    private String currentUserId;

    private FirebaseUserDataSourceImpl(String currentUserId) {
        userRef = FirebaseHelper.getDatabaseReferenceByPath("User");
        this.currentUserId = currentUserId;
    }

    public static synchronized FirebaseUserDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FirebaseUserDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }


    @Override
    public void createUser(User user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_ID).setValue(user.getId());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_BIO).setValue(user.getBio());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_BIRTHDAY).setValue(user.getBirthday());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_CREATED_DATE).setValue(user.getCreatedDate());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_USER_ROLE).setValue(user.getRole());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_EMAIL).setValue(user.getEmail());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_GENDER).setValue(user.getGender());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR).setValue(user.getImageAvatar());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_IMAGE_COVER).setValue(user.getImageCover());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_NAME).setValue(user.getName());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_IS_ACTIVE).setValue(user.isActive());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_IS_ONLINE).setValue(user.isOnline());
        userRef.child(currentUser.getUid()).child(MySQLiteHelper.COLUMN_USER_PASSWORD).setValue(user.getPassword());
    }

    public void getAllUsers() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    Log.d("USER NULL?", user == null ? "NULL" : "PLEASE NULL");
                    userList.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void updateOnlineField(User user) {
        userRef.child(user.getId()).child(MySQLiteHelper.COLUMN_USER_IS_ONLINE).setValue(user.isOnline());
    }

    @Override
    public void updateUser(User user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_NAME, user.getName());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_BIO, user.getBio());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_BIRTHDAY, user.getBirthday());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_GENDER, user.getGender());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR, user.getImageAvatar());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_IMAGE_COVER, user.getImageCover());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_PASSWORD, user.getPassword());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_USER_ROLE, user.getRole());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_IS_ONLINE, user.isOnline());
        updatedUserData.put(MySQLiteHelper.COLUMN_USER_IS_ACTIVE, user.isActive());

        userRef.child(currentUser.getUid()).updateChildren(updatedUserData)
                .addOnSuccessListener(runnable -> {
                    Log.d("FirebaseUpdateUser", "User data updated successfully!");
        }).addOnFailureListener(runnable -> {
                    Log.e("FirebaseUpdateUser", "Error updating user data: " + runnable.getMessage());
        });
    }

}
