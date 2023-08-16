package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;

import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseInboxPostDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.model.InboxPost;
import com.google.firebase.database.DatabaseReference;

public class FirebaseInboxPostDataSourceImpl implements FirebaseInboxPostDataSource {

    private DatabaseReference inboxPostRef;
    private String currentUserId;
    private static FirebaseInboxPostDataSourceImpl instance;

    private FirebaseInboxPostDataSourceImpl(String currentUserId) {
        inboxPostRef = FirebaseHelper.getDatabaseReferenceByPath("InboxPost");
        this.currentUserId = currentUserId;
    }

    public static  synchronized FirebaseInboxPostDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FirebaseInboxPostDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    @Override
    public boolean createInboxPost(InboxPost inboxPost) {
        if (inboxPost.getId() == null || inboxPost.getId().isEmpty()) {
            Log.d("createInboxPost", "is is null");
            return false;
        } else if (inboxPost.getPost() == null) {
            Log.d("createInboxPost", "post is null");
            return false;
        } else {
            inboxPostRef.child(inboxPost.getId()).child(MySQLiteHelper.COLUMN_INBOX_POST_ID).setValue(inboxPost.getId());
            inboxPostRef.child(inboxPost.getId()).child(MySQLiteHelper.COLUMN_INBOX_POST_POST_ID).setValue(inboxPost.getPost().getId());
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        boolean result = false;
        if (id != null && !id.isEmpty()) {
            result = inboxPostRef.child(id).removeValue().isSuccessful();
        }
        return result;
    }
}
