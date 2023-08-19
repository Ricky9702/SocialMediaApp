package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;

import android.content.ContentValues;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebasePostImagesDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.model.PostImages;
import com.google.firebase.database.DatabaseReference;

public class FirebasePostImagesDataSourceImpl implements FirebasePostImagesDataSource {

    private DatabaseReference postImagesRef;
    private static FirebasePostImagesDataSourceImpl instance;
    private String currentUserId;
    private String TAG = "FirebasePostImagesDataSourceImpl";

    private FirebasePostImagesDataSourceImpl(String currentUserId) {
        postImagesRef = FirebaseHelper.getDatabaseReferenceByPath("PostImages");
        this.currentUserId = currentUserId;
    }

    public static synchronized FirebasePostImagesDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FirebasePostImagesDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }


    @Override
    public boolean create(PostImages postImages) {
        boolean result = false;
        if (postImages.getId() == null || postImages.getId().isEmpty()) {
            Log.d("createPostImages", "is null");
            return false;
        } else if (postImages.getImageUrl() == null || postImages.getImageUrl().isEmpty()) {
            Log.d("createPostImages", "imageUrl null");
            return false;
        } else if (postImages.getPost() == null) {
            Log.d("createPostImages", "post  null");
            return false;
        } else if (postImages.getCreatedDate() == null || postImages.getCreatedDate().isEmpty()) {
            Log.d("createPostImages", "createdDate  null");
            return false;
        } else {
            postImagesRef.child(postImages.getId()).child(MySQLiteHelper.COLUMN_POST_IMAGES_ID).setValue(postImages.getId());
            postImagesRef.child(postImages.getId()).child(MySQLiteHelper.COLUMN_POST_IMAGES_IMAGE_URL).setValue(postImages.getImageUrl());
            postImagesRef.child(postImages.getId()).child(MySQLiteHelper.COLUMN_POST_IMAGES_POST_ID).setValue(postImages.getPost().getId());
            postImagesRef.child(postImages.getId()).child(MySQLiteHelper.COLUMN_POST_CREATED_DATE).setValue(postImages.getCreatedDate());
            result = true;
        }
        return result;
    }

    @Override
    public boolean delete(PostImages postImages) {
        boolean result = false;
        if (postImages.getId() == null || postImages.getId().isEmpty()) {
            Log.d("createPostImages", "is null");
            return false;
        } else if (postImages.getImageUrl() == null || postImages.getImageUrl().isEmpty()) {
            Log.d("createPostImages", "imageUrl null");
            return false;
        } else if (postImages.getPost() == null) {
            Log.d("createPostImages", "post  null");
            return false;
        } else if (postImages.getCreatedDate() == null || postImages.getCreatedDate().isEmpty()) {
            Log.d("createPostImages", "createdDate  null");
            return false;
        } else {
            result = postImagesRef.child(postImages.getId()).removeValue().isSuccessful();
        }
        return result;
    }
}
