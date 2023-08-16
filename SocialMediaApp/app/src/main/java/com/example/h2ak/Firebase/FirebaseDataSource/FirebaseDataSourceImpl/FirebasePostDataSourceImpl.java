package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;

import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebasePostDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.model.Post;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirebasePostDataSourceImpl implements FirebasePostDataSource {
    private DatabaseReference postRef;
    private static FirebasePostDataSourceImpl instance;
    private String currentUserId;
    private String TAG = "FirebasePostDataSourceImpl";

    private FirebasePostDataSourceImpl(String currentUserId) {
        postRef = FirebaseHelper.getDatabaseReferenceByPath("Post");
        this.currentUserId = currentUserId;
    }

    public static synchronized FirebasePostDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FirebasePostDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }


    @Override
    public boolean create(Post post) {
        boolean result;
        if (post.getId() == null || post.getId().isEmpty()) {
            Log.d("createPostFirebase: ", "id is null");
            return false;
        } else if (post.getContent() == null) {
            Log.d("createPostFirebase: ", "content is null");
            return false;
        } else if (post.getCreatedDate() == null || post.getCreatedDate().isEmpty()) {
            Log.d("createPostFirebase: ", "createdDate is null");
            return false;
        } else if (post.getUser() == null) {
            Log.d("createPostFirebase: ", "user is null");
            return false;
        } else if (post.getPrivacy() == null || post.getPrivacy().isEmpty()) {
            Log.d("createPostFirebase: ", "privacy is null");
            return false;
        } else {
            postRef.child(post.getId()).child(MySQLiteHelper.COLUMN_POST_ID).setValue(post.getId());
            postRef.child(post.getId()).child(MySQLiteHelper.COLUMN_POST_CONTENT).setValue(post.getContent());
            postRef.child(post.getId()).child(MySQLiteHelper.COLUMN_POST_CREATED_DATE).setValue(post.getCreatedDate());
            postRef.child(post.getId()).child(MySQLiteHelper.COLUMN_POST_USER_ID).setValue(post.getUser().getId());
            postRef.child(post.getId()).child(MySQLiteHelper.COLUMN_POST_PRIVACY).setValue(post.getPrivacy());
            result = true;
        }
        return result;
    }

    @Override
    public boolean update(Post post) {
        AtomicBoolean result = new AtomicBoolean(false);
        if (post.getId() == null || post.getId().isEmpty()) {
            Log.d("updatePostFirebase: ", "id is null");
            return false;
        } else if (post.getContent() == null) {
            Log.d("updatePostFirebase: ", "content is null");
            return false;
        } else if (post.getCreatedDate() == null || post.getCreatedDate().isEmpty()) {
            Log.d("updatePostFirebase: ", "createdDate is null");
            return false;
        } else if (post.getUser() == null) {
            Log.d("updatePostFirebase: ", "user is null");
            return false;
        } else if (post.getPrivacy() == null || post.getPrivacy().isEmpty()) {
            Log.d("updatePostFirebase: ", "privacy is null");
            return false;
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put(MySQLiteHelper.COLUMN_POST_ID, post.getId());
            map.put(MySQLiteHelper.COLUMN_POST_CONTENT, post.getContent());
            map.put(MySQLiteHelper.COLUMN_POST_PRIVACY, post.getPrivacy());
            map.put(MySQLiteHelper.COLUMN_POST_USER_ID, post.getUser().getId());
            map.put(MySQLiteHelper.COLUMN_POST_CREATED_DATE, post.getCreatedDate());
            postRef.child(post.getId()).updateChildren(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "update: success");
                    result.set(true);
                } else {
                    Log.d(TAG, "update: failed");
                }
            });
        }
        return result.get();
    }

    @Override
    public boolean delete(Post post) {
        boolean result = false;
        if (post.getId() != null && !post.getId().isEmpty()) {
            result = postRef.child(post.getId()).removeValue().isSuccessful();
        }
        return result;
    }
}
