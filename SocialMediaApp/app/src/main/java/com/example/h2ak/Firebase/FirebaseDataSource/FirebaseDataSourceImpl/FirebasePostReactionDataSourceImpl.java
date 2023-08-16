package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;

import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebasePostReactionDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.model.PostReaction;
import com.google.firebase.database.DatabaseReference;

public class FirebasePostReactionDataSourceImpl implements FirebasePostReactionDataSource {
    private DatabaseReference postReactionRef;
    private static FirebasePostReactionDataSourceImpl instance;
    private String currentUserId;
    private String TAG = "FirebasePostReactionDataSourceImpl";

    private FirebasePostReactionDataSourceImpl(String currentUserId) {
        postReactionRef = FirebaseHelper.getDatabaseReferenceByPath("PostReaction");
        this.currentUserId = currentUserId;
    }

    public static synchronized FirebasePostReactionDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FirebasePostReactionDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    @Override
    public boolean create(PostReaction postReaction) {
        boolean result = false;
        if (postReaction.getId() == null || postReaction.getId().isEmpty()) {
            Log.d("PostReactionDataSourceImpl", "create: id is null");
            return  false;
        } else if (postReaction.getPost() == null) {
            Log.d("PostReactionDataSourceImpl", "create: post is null");
            return false;
        } else if (postReaction.getCreatedDate() == null || postReaction.getCreatedDate().isEmpty()) {
            Log.d("PostReactionDataSourceImpl", "create: createdDate is null");
            return false;
        } else if (postReaction.getUser() == null) {
            Log.d("PostReactionDataSourceImpl", "create: User is null");
            return false;
        } else if (postReaction.getType() == null || postReaction.getType().isEmpty()) {
            Log.d("PostReactionDataSourceImpl", "create: type is null");
            return false;
        } else {
            postReactionRef.child(postReaction.getId()).child(MySQLiteHelper.COLUMN_POST_REACTION_ID).setValue(postReaction.getId());
            postReactionRef.child(postReaction.getId()).child(MySQLiteHelper.COLUMN_POST_REACTION_TYPE).setValue(postReaction.getType());
            postReactionRef.child(postReaction.getId()).child(MySQLiteHelper.COLUMN_POST_REACTION_CREATED_DATE).setValue(postReaction.getCreatedDate());
            postReactionRef.child(postReaction.getId()).child(MySQLiteHelper.COLUMN_POST_REACTION_USER_ID).setValue(postReaction.getUser().getId());
            postReactionRef.child(postReaction.getId()).child(MySQLiteHelper.COLUMN_POST_REACTION_POST_ID).setValue(postReaction.getPost().getId());
            result =  true;
        }
        return result;
    }

    @Override
    public boolean delete(PostReaction postReaction) {
        boolean result = false;
        if (postReaction.getId() != null && !postReaction.getId().isEmpty()) {
            result = postReactionRef.child(postReaction.getId()).removeValue().isSuccessful();
        }
        return result;
    }
}
