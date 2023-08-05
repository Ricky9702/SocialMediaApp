package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseInboxDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.model.Inbox;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class FirebaseInboxDataSourceImpl implements FirebaseInboxDataSource {

    private DatabaseReference inboxRef;
    private String currentUserId;
    private static FirebaseInboxDataSourceImpl instance;

    private FirebaseInboxDataSourceImpl(String currentUserId) {
        inboxRef = FirebaseHelper.getDatabaseReferenceByPath("Inbox");
        this.currentUserId = currentUserId;
    }

    public static  synchronized FirebaseInboxDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FirebaseInboxDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }


    @Override
    public void createInbox(Inbox inbox) {
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_ID).setValue(inbox.getId());
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_CONTENT).setValue(inbox.getContent());
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_CREATED_DATE).setValue(inbox.getCreatedDate());
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_TYPE).setValue(inbox.getType());
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_IS_READ).setValue(inbox.isRead());
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE).setValue(inbox.isActive());
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_USER_1).setValue(inbox.getUserRecieveRequest().getId());
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_USER_2).setValue(inbox.getUserSentRequest().getId());
    }

    @Override
    public void deleteInbox(Inbox inbox) {
        inboxRef.child(inbox.getId()).removeValue()
                .addOnSuccessListener(unused -> Log.d("deleteInbox", "deleteInbox success"))
                .addOnFailureListener(e -> Log.d("deleteInbox", "deleteInbox failed"));
    }

    @Override
    public void updateInbox(Inbox inbox) {
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_IS_READ).setValue(inbox.isRead());
        inboxRef.child(inbox.getId()).child(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE).setValue(inbox.isActive());
    }
}
