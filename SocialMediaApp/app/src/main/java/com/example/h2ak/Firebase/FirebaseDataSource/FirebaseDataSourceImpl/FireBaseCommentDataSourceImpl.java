package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;

import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FireBaseCommentDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.model.PostComment;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FireBaseCommentDataSourceImpl implements FireBaseCommentDataSource {
    private DatabaseReference postCommentRef;
    private static FireBaseCommentDataSourceImpl instance;
    private String currentUserId;
    private String TAG = "FireBaseCommentDataSourceImpl";

    private FireBaseCommentDataSourceImpl(String currentUserId) {
        postCommentRef = FirebaseHelper.getDatabaseReferenceByPath("PostComment");
        this.currentUserId = currentUserId;
    }

    public static synchronized FireBaseCommentDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FireBaseCommentDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }
    @Override
    public boolean create(PostComment comment) {
        boolean result = false;
        if (comment.getId() == null || comment.getId().isEmpty()) {
            Log.d(TAG, "create: id is null");
            return false;
        } else if (comment.getCreatedDate() == null || comment.getCreatedDate().isEmpty()) {
            Log.d(TAG, "create: createdDate is null");
            return false;
        } else if (comment.getContent() == null || comment.getContent().isEmpty()) {
            Log.d(TAG, "create: content is null");
            return false;
        } else if (comment.getUser() == null) {
            Log.d(TAG, "create: user is null");
            return false;
        } else if (comment.getPost() == null) {
            Log.d(TAG, "create: post is null");
            return false;
        } else {

            Log.d(TAG, "create: "+ comment.getParent()+"");

            postCommentRef.child(comment.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_ID).setValue(comment.getId());
            postCommentRef.child(comment.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE).setValue(comment.getCreatedDate());
            postCommentRef.child(comment.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_CONTENT).setValue(comment.getContent());
            postCommentRef.child(comment.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_USER_ID).setValue(comment.getUser().getId());
            postCommentRef.child(comment.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_POST_ID).setValue(comment.getPost().getId());
            result = true;
        }
        return result;
    }

    @Override
    public boolean delete(PostComment postComment) {
        boolean result = false;
        if (postComment.getId() != null && !postComment.getId().isEmpty()) {
            result = postCommentRef.child(postComment.getId()).removeValue().isSuccessful();
        }
        return result;
    }

    @Override
    public boolean update(PostComment comment) {
        AtomicBoolean result = new AtomicBoolean(false);
        if (comment.getId() == null || comment.getId().isEmpty()) {
            Log.d(TAG, "create: id is null");
            return false;
        } else if (comment.getCreatedDate() == null || comment.getCreatedDate().isEmpty()) {
            Log.d(TAG, "create: createdDate is null");
            return false;
        } else if (comment.getContent() == null || comment.getContent().isEmpty()) {
            Log.d(TAG, "create: content is null");
            return false;
        } else if (comment.getUser() == null) {
            Log.d(TAG, "create: user is null");
            return false;
        } else if (comment.getPost() == null) {
            Log.d(TAG, "create: post is null");
            return false;
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_ID, comment.getId());
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE, comment.getCreatedDate());
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_CONTENT, comment.getContent());
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_USER_ID, comment.getUser().getId());
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_POST_ID, comment.getPost().getId());

            postCommentRef.child(comment.getId()).updateChildren(map).addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   result.set(true);
               }
            });

        }
        return result.get();
    }

    @Override
    public void updateParentComment(PostComment comment, PostComment parent) {
        Log.d(TAG, "updateParentComment: " +  comment.getParent().getId());
        postCommentRef.child(comment.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_PARENT_ID).setValue(parent.getId());
    }
}
