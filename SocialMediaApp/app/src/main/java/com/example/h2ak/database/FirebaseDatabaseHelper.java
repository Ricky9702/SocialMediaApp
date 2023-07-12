package com.example.h2ak.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseHelper {
    private static final DatabaseReference firebaseDatabase;

    static {
        firebaseDatabase = FirebaseDatabase.getInstance("https://socialmediaapp-32653-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    }

    public static final DatabaseReference getFirebaseDatabaseUser()
    {
        return firebaseDatabase.child("User");
    }
}
