package com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseFriendShipDataSource;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.model.FriendShip;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class FirebaseFriendShipDataSourceImpl implements FirebaseFriendShipDataSource {
    private DatabaseReference friendShipRef;
    private static FirebaseFriendShipDataSourceImpl instance;
    private String currentUserId;

    private FirebaseFriendShipDataSourceImpl(String currentUserId) {
        friendShipRef = FirebaseHelper.getDatabaseReferenceByPath("FriendShip");
        this.currentUserId = currentUserId;
    }

    public static synchronized FirebaseFriendShipDataSourceImpl getInstance() {
        if (instance == null) {
            instance = new FirebaseFriendShipDataSourceImpl(MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    @Override
    public void createFriendShip(FriendShip friendShip) {
        friendShipRef.child(friendShip.getId()).child(MySQLiteHelper.COLUMN_FRIENDSHIP_ID).setValue(friendShip.getId());
        friendShipRef.child(friendShip.getId()).child(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1).setValue(friendShip.getUser1().getId());
        friendShipRef.child(friendShip.getId()).child(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2).setValue(friendShip.getUser2().getId());
        friendShipRef.child(friendShip.getId()).child(MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE).setValue(friendShip.getCreatedDate());
        friendShipRef.child(friendShip.getId()).child(MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS).setValue(friendShip.getStatus());
    }
}
