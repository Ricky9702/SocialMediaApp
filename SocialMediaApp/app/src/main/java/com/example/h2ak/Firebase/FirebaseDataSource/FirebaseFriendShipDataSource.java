package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.FriendShip;

public interface FirebaseFriendShipDataSource {
    void createFriendShip(FriendShip friendShip);
    void updateFriendShip(FriendShip friendShip);
    void deleteFriendShip(FriendShip friendShip);
}
