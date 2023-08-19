package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;

import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebasePostCommentReactionDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.model.PostCommentReaction;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirebasePostCommentReactionDataSourceImpl implements FirebasePostCommentReactionDataSource {
    private DatabaseReference postCommentReactionRef;
    private static FirebasePostCommentReactionDataSourceImpl instance;
    private String currentUserId;
    private String TAG = "FirebasePostCommentReactionDataSourceImpl";

    private FirebasePostCommentReactionDataSourceImpl(String currentUserId) {
        postCommentReactionRef = FirebaseHelper.getDatabaseReferenceByPath("PostCommentReaction");
        this.currentUserId = currentUserId;
    }

    public static synchronized FirebasePostCommentReactionDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FirebasePostCommentReactionDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    @Override
    public boolean create(PostCommentReaction commentReaction) {
        boolean result =false;

        if (commentReaction.getId() == null || commentReaction.getId().isEmpty()) {
            Log.d(TAG, "create: id is null");
            return  false;
        } else if (commentReaction.getPostComment() == null) {
            Log.d(TAG, "create: post is null");
            return false;
        } else if (commentReaction.getCreatedDate() == null || commentReaction.getCreatedDate().isEmpty()) {
            Log.d(TAG, "create: createdDate is null");
            return false;
        } else if (commentReaction.getUser() == null) {
            Log.d(TAG, "create: User is null");
            return false;
        } else if (commentReaction.getType() == null || commentReaction.getType().isEmpty()) {
            Log.d(TAG, "create: type is null");
            return false;
        } else {

            postCommentReactionRef.child(commentReaction.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_ID).setValue(commentReaction.getId());
            postCommentReactionRef.child(commentReaction.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_TYPE).setValue(commentReaction.getType());
            postCommentReactionRef.child(commentReaction.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE).setValue(commentReaction.getCreatedDate());
            postCommentReactionRef.child(commentReaction.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_USER_ID).setValue(commentReaction.getUser().getId());
            postCommentReactionRef.child(commentReaction.getId()).child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID).setValue(commentReaction.getPostComment().getId());

            result =true;
        }
        return result;
    }

    @Override
    public boolean delete(PostCommentReaction commentReaction) {
        boolean result = false;
        if (commentReaction.getId() == null || commentReaction.getId().isEmpty()) {
            Log.d(TAG, "delete: id is null");
            return  false;
        } else if (commentReaction.getPostComment() == null) {
            Log.d(TAG, "delete: post is null");
            return false;
        } else if (commentReaction.getCreatedDate() == null || commentReaction.getCreatedDate().isEmpty()) {
            Log.d(TAG, "delete: createdDate is null");
            return false;
        } else if (commentReaction.getUser() == null) {
            Log.d(TAG, "delete: User is null");
            return false;
        } else if (commentReaction.getType() == null || commentReaction.getType().isEmpty()) {
            Log.d(TAG, "delete: type is null");
            return false;
        } else {
            result = postCommentReactionRef.child(commentReaction.getId()).removeValue().isSuccessful();
        }
        return result;
    }

    @Override
    public boolean update(PostCommentReaction commentReaction) {
        AtomicBoolean result = new AtomicBoolean(false);
        if (commentReaction.getId() == null || commentReaction.getId().isEmpty()) {
            Log.d(TAG, "update: id is null");
            return  false;
        } else if (commentReaction.getPostComment() == null) {
            Log.d(TAG, "update: post is null");
            return false;
        } else if (commentReaction.getCreatedDate() == null || commentReaction.getCreatedDate().isEmpty()) {
            Log.d(TAG, "update: createdDate is null");
            return false;
        } else if (commentReaction.getUser() == null) {
            Log.d(TAG, "update: User is null");
            return false;
        } else if (commentReaction.getType() == null || commentReaction.getType().isEmpty()) {
            Log.d(TAG, "update: type is null");
            return false;
        } else {
            Log.d(TAG, "update: " + commentReaction.getId());
            Map<String, Object> map = new HashMap<>();
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_ID, commentReaction.getId());
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_TYPE, commentReaction.getType());
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE, commentReaction.getCreatedDate());
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_USER_ID, commentReaction.getUser().getId());
            map.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID, commentReaction.getPostComment().getId());

            postCommentReactionRef.child(commentReaction.getId()).updateChildren(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    result.set(true);
                    Log.d(TAG, "update: update success");
                } else {
                    Log.d(TAG, "update: failed");
                }
            });
        }
        return result.get();
    }
}
