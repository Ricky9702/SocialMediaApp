package com.example.h2ak.database;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseHelper {

    private static final FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;

    static {
        firebaseDatabase = FirebaseDatabase.getInstance("https://socialmediaapp-32653-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference();
    }

    public static final DatabaseReference getFirebaseDatabaseUser() {
        return firebaseDatabase.getReference().child("User");
    }

    public static final DatabaseReference getDatabaseReferenceByNode(String node) {
        databaseReference = firebaseDatabase.getReference(node);
        if (databaseReference != null)
            return databaseReference;
        return null;
    }

    public static final FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

}
