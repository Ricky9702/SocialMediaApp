package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseFriendShipDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseFriendShipDataSource;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseUserDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendShipDataSourceImpl implements FriendShipDataSource {
    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private UserDataSource userDataSource;
    private FirebaseFriendShipDataSource firebaseFriendShipDataSource;
    private String currentUserId;
    private static FriendShipDataSourceImpl instance;

    private FriendShipDataSourceImpl(Context context, String currentUserId) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        userDataSource = UserDataSourceImpl.getInstance(context);
        firebaseFriendShipDataSource = FirebaseFriendShipDataSourceImpl.getInstance();
        this.currentUserId = currentUserId;
    }

    public static synchronized FriendShipDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new FriendShipDataSourceImpl(context, MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }


    public boolean createFriendShipOnFirebaseChange(FriendShip friendShip) {
        db.beginTransaction();
        boolean result = false;
        try {
            Log.d("createFriendShip : ", "LOCAL FIREBASE");

            if (friendShip.getId() == null || friendShip.getId().isEmpty()) {
                Log.d("createFriendShip : ", "id is null");
                return false;
            } else if (friendShip.getStatus() == null || friendShip.getStatus().isEmpty()) {
                Log.d("createFriendShip : ", "status is null");
                return false;
            } else if (friendShip.getUser1() == null) {
                Log.d("createFriendShip : ", "user1 is null");
                return false;
            } else if (friendShip.getUser2() == null) {
                Log.d("createFriendShip : ", "user2 is null");
                return false;
            } else if (friendShip.getCreatedDate() == null || friendShip.getCreatedDate().isEmpty()) {
                Log.d("createFriendShip : ", "createdDate is null");

            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_ID, friendShip.getId());
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS, friendShip.getStatus());
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE, friendShip.getCreatedDate());
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1, friendShip.getUser1().getId());
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2, friendShip.getUser2().getId());
                result = db.insert(MySQLiteHelper.TABLE_FRIENDSHIP, null, contentValues) > 0;
            }
            db.setTransactionSuccessful();
        } catch (Exception exception) {
            Log.d("createFriendShip: ", exception.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public boolean createFriendShip(FriendShip friendShip) {
        db.beginTransaction();
        boolean result = false;
        try {
            Log.d("createFriendShip : ", "LOCAL");
            ContentValues contentValues = new ContentValues();

            if (friendShip.getId() == null || friendShip.getId().isEmpty()) {
                Log.d("createFriendShip", "createFriendShip: id is null or empty");
                return false;
            } else if (friendShip.getCreatedDate() == null || friendShip.getCreatedDate().isEmpty()) {
                Log.d("createFriendShip", "createFriendShip: createdDate is null or empty");
                return false;
            } else if (friendShip.getStatus() == null || friendShip.getStatus().isEmpty()) {
                Log.d("createFriendShip", "createFriendShip: status is null or empty");
                return false;
            } else if (friendShip.getUser1() == null) {
                Log.d("createFriendShip", "user 1: id is null or empty");
                return false;
            } else if (friendShip.getUser2() == null) {
                Log.d("createFriendShip", "user 2: id is null or empty");
                return false;
            } else {
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_ID, friendShip.getId());
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS, friendShip.getStatus());
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE, friendShip.getCreatedDate());
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1, friendShip.getUser1().getId());
                contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2, friendShip.getUser2().getId());

                if (findFriendShip(friendShip) == null) {
                    result = db.insert(MySQLiteHelper.TABLE_FRIENDSHIP, null, contentValues) > 0;
                    if (result) {
                        Log.d("createFriendShip", "create to firebase");
                        firebaseFriendShipDataSource.createFriendShip(friendShip);
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception exception) {
            Log.d("createFriendShip: ", exception.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public FriendShip findLastestFriendShip(User user1, User user2) {

        FriendShip friendShip = null;

        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_FRIENDSHIP
                + " WHERE (" + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1 + "= ? AND " + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2 + "= ? )"
                + " OR (" + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1 + "= ? AND " + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2 + "= ? )"
                + " ORDER BY " + MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE + " DESC "
                + " LIMIT 1 ", new String[]{user1.getId(), user2.getId(), user2.getId(), user1.getId()})) {

            if (c.moveToFirst()) {
                friendShip = new FriendShip();
                friendShip.setId(c.getString(0));
                friendShip.setCreatedDate(c.getString(1));
                friendShip.setStatus(c.getString(2));
                friendShip.setUser1(userDataSource.getUserById(c.getString(3)));
                friendShip.setUser2(userDataSource.getUserById(c.getString(4)));
                friendShip.setFriendShipStatus(FriendShip.FriendShipStatus.valueOf(friendShip.getStatus()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendShip;
    }

    @Override
    public FriendShip findFriendShip(FriendShip friendShip) {
        FriendShip friendShip1 = null;
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_FRIENDSHIP
                        + " WHERE " + MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " = ? "
                        + " LIMIT 1 ",
                new String[]{friendShip.getId()})) {

            if (c.moveToFirst()) {
                friendShip1 = new FriendShip();
                friendShip1.setId(c.getString(0));
                friendShip1.setCreatedDate(c.getString(1));
                friendShip1.setStatus(c.getString(2));
                friendShip1.setFriendShipStatus(FriendShip.FriendShipStatus.valueOf(friendShip1.getStatus()));
                friendShip1.setUser1(userDataSource.getUserById(c.getString(3)));
                friendShip1.setUser2(userDataSource.getUserById(c.getString(4)));
                return friendShip1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendShip1;
    }


    @Override
    public boolean updateFriendShip(FriendShip friendShip) {
        boolean result = false;
        if(this.updateFriendShipOnFirebaseChange(friendShip)) {
            firebaseFriendShipDataSource.updateFriendShip(friendShip);
            result = true;
        }
        return result;
    }

    @Override
    public boolean updateFriendShipOnFirebaseChange(FriendShip friendShip) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (friendShip.getId() == null || friendShip.getId().isEmpty()) {
                Log.d("createFriendShip", "createFriendShip: id is null or empty");
                return false;
            } else if (friendShip.getCreatedDate() == null || friendShip.getCreatedDate().isEmpty()) {
                Log.d("createFriendShip", "createFriendShip: createdDate is null or empty");
                return false;
            } else if (friendShip.getStatus() == null || friendShip.getStatus().isEmpty()) {
                Log.d("createFriendShip", "createFriendShip: status is null or empty");
                return false;
            } else if (friendShip.getUser1() == null) {
                Log.d("createFriendShip", "user 1: id is null or empty");
                return false;
            } else if (friendShip.getUser2() == null) {
                Log.d("createFriendShip", "user 2: id is null or empty");
                return false;
            } else {
                Log.d("updateFriendShip : ", "LOCAL");
                FriendShip found = findFriendShip(friendShip);
                if (found != null && !found.equals(friendShip)) {
                    ContentValues contentValues = new ContentValues();

                    if (!friendShip.getUser1().getId().equals(found.getUser1().getId())) {
                        contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1, friendShip.getUser1().getId());
                    } else if (!friendShip.getUser2().getId().equals(found.getUser2().getId())) {
                        contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2, friendShip.getUser2().getId());
                    }

                    contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS, friendShip.getStatus());

                    result = db.update(MySQLiteHelper.TABLE_FRIENDSHIP, contentValues, MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " = ? ",
                            new String[]{String.valueOf(friendShip.getId())}) > 0;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {

        } finally {
            db.endTransaction();;
        }
        return result;
    }

    @Override
    public Set<FriendShip> getAllFriendShipByUser(User user) {
        Set<FriendShip> friendShipSet = new HashSet<>();
        try (Cursor c = db.rawQuery(
                "SELECT * FROM " + MySQLiteHelper.TABLE_FRIENDSHIP
                        + " WHERE (" + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1 + " = ? OR " + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2 + " = ?) "
                        + " AND  " + MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS + " = ? "
                        + " ORDER BY " + MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " DESC",
                new String[]{user.getId(), user.getId(), FriendShip.FriendShipStatus.ACCEPTED.getStatus()}
        )) {
            while (c.moveToNext()) {

                Log.d("FriendShipId?", c.getString(0));
                User user1 = userDataSource.getUserById(c.getString(3));
                User user2 = userDataSource.getUserById(c.getString(4));

                FriendShip friendShip1 = new FriendShip();
                friendShip1.setId(c.getString(0));
                friendShip1.setCreatedDate(c.getString(1));
                friendShip1.setStatus(c.getString(2));
                friendShip1.setUser1(user1);
                friendShip1.setUser2(user2);
                friendShip1.setFriendShipStatus(FriendShip.FriendShipStatus.valueOf(friendShip1.getStatus()));

                friendShipSet.add(friendShip1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendShipSet;
    }

    @Override
    public Set<User> getMutualFriends(User user1, User user2) {

        Set<User> mutualFriends = new HashSet<>();

        Set<User> user1Friends = getFriendsByUser(user1);
        Set<User> user2Friends = getFriendsByUser(user2);

        mutualFriends.addAll(user1Friends);

        mutualFriends.retainAll(user2Friends);


        return mutualFriends.stream()
                .filter(user -> !user.getId().equals(user1.getId()) && !user.getId().equals(user2.getId()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getFriendsByUser(User user) {
        Set<FriendShip> user1Friends = getAllFriendShipByUser(user);
        // Get all friends from user1 (include user1)
        Set<User> user1FriendSet = user1Friends.stream()
                .flatMap(friendShip -> Stream.of(friendShip.getUser1(), friendShip.getUser2()))
                .collect(Collectors.toSet());
        return user1FriendSet.stream().filter(user1 -> !user1.getId().equals(user.getId())).collect(Collectors.toSet());
    }

    @Override
    public void close() {
        databaseManager.closeDatabase();
    }

    @Override
    public boolean delete(FriendShip friendShip) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (findFriendShip(friendShip) != null) {
                result = db.delete(MySQLiteHelper.TABLE_FRIENDSHIP,
                        MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " = ? ",
                        new String[]{friendShip.getId()}) > 0;
                if (result) {
                    firebaseFriendShipDataSource.deleteFriendShip(friendShip);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public List<FriendShip> getAllFriendShip() {
        Set<FriendShip> friendShipSet = new HashSet<>();
        try (Cursor c = db.rawQuery(
                "SELECT * FROM " + MySQLiteHelper.TABLE_FRIENDSHIP
                        + " ORDER BY " + MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " DESC", null)) {
            while (c.moveToNext()) {
                Log.d("FriendShipId?", c.getString(0));
                User user1 = userDataSource.getUserById(c.getString(3));
                User user2 = userDataSource.getUserById(c.getString(4));

                FriendShip friendShip1 = new FriendShip();
                friendShip1.setId(c.getString(0));
                friendShip1.setCreatedDate(c.getString(1));
                friendShip1.setStatus(c.getString(2));
                friendShip1.setUser1(user1);
                friendShip1.setUser2(user2);
                friendShip1.setFriendShipStatus(FriendShip.FriendShipStatus.valueOf(friendShip1.getStatus()));

                friendShipSet.add(friendShip1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendShipSet.stream().collect(Collectors.toList());
    }
}
