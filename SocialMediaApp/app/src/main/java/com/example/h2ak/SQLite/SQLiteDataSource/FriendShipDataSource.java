package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.User;

import java.util.List;
import java.util.Set;

public interface FriendShipDataSource {
    boolean createFriendShip(FriendShip friendShip);
    FriendShip findLastestFriendShip(User user1, User user2);
    FriendShip findFriendShip(FriendShip friendShip);
    boolean updateFriendShip(FriendShip friendShip);
    boolean updateFriendShipOnFirebaseChange(FriendShip friendShip);
    Set<FriendShip> getAllFriendShipByUser(User user);
    Set<User> getMutualFriends(User user1, User user2);
    Set<User> getFriendsByUser(User user);
    void close();

    boolean delete(FriendShip friendShip);

    List<FriendShip> getAllFriendShip();
}
