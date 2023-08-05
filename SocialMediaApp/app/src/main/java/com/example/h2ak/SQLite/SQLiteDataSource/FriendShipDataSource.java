package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.User;

import java.util.Set;

public interface FriendShipDataSource {
    boolean createFriendShipOnFirebaseChange(FriendShip friendShip);
    boolean createFriendShip(FriendShip friendShip);
    FriendShip findLastestFriendShip(User user1, User user2);
    FriendShip findFriendShipFirebase(FriendShip friendShip);
    boolean updateFriendShip(FriendShip friendShip);
    Set<FriendShip> getAllFriendShipByUser(User user);
    void close();
}
