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

import java.util.HashSet;
import java.util.Set;

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


    @Override
    public boolean createFriendShipOnFirebaseChange(FriendShip friendShip) {
        db.beginTransaction();
        boolean result = false;
        try {
            Log.d("createFriendShip : ", "LOCAL FIREBASE");
            ContentValues contentValues = new ContentValues();
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_ID, friendShip.getId());
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS, friendShip.getStatus());
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE, friendShip.getCreatedDate());
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1, friendShip.getUser1().getId());
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2, friendShip.getUser2().getId());
            result = db.insert(MySQLiteHelper.TABLE_FRIENDSHIP, null, contentValues) > 0;
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
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_ID, friendShip.getId());
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS, friendShip.getStatus());
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE, friendShip.getCreatedDate());
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1, friendShip.getUser1().getId());
            contentValues.put(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2, friendShip.getUser2().getId());

            firebaseFriendShipDataSource.createFriendShip(friendShip);

            result = db.insert(MySQLiteHelper.TABLE_FRIENDSHIP, null, contentValues) > 0;
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
                + " WHERE " + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1 + "= ? AND " + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2 + "= ?"
                + "OR " + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1 + "= ? AND " + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2 + "= ?"
                + "ORDER BY " + MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " DESC"
                + " LIMIT 1 ", new String[]{user1.getId(), user2.getId(), user2.getId(), user1.getId()})) {

            if (c.moveToFirst()) {
                friendShip = new FriendShip();
                friendShip.setId(c.getString(0));
                friendShip.setCreatedDate(c.getString(1));
                friendShip.setStatus(c.getString(2));

                friendShip.setUser1(userDataSource.getUserById(c.getString(3)));
                friendShip.setUser2(userDataSource.getUserById(c.getString(4)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendShip;
    }

    @Override
    public FriendShip findFriendShipFirebase(FriendShip friendShip) {
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_FRIENDSHIP + " WHERE " + MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " = ? "
                        + " LIMIT 1 ",
                new String[]{friendShip.getId()})) {

            if (c.moveToFirst()) {
                return friendShip;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean updateFriendShip(FriendShip friendShip) {
        Log.d("updateFriendShip : ", "LOCAL");
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", friendShip.getStatus());
        return db.update(MySQLiteHelper.TABLE_FRIENDSHIP, contentValues, MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " = ? ",
                new String[]{String.valueOf(friendShip.getId())}) > 0;
    }

    @Override
    public Set<FriendShip> getAllFriendShipByUser(User user) {
        Set<FriendShip> friendShipSet = new HashSet<>();
        try (Cursor c = db.rawQuery(
                "SELECT * FROM " + MySQLiteHelper.TABLE_FRIENDSHIP +
                        " WHERE (" + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1 + " = ? OR " + MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2 + " = ?) " +
                        " AND " + MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS + " = ? " +
                        " ORDER BY " + MySQLiteHelper.COLUMN_FRIENDSHIP_ID + " DESC",
                new String[]{user.getId(), user.getId(), FriendShip.FriendShipStatus.ACCEPTED.getStatus()}
        )) {
            while (c.moveToNext()) {
                User user1 = userDataSource.getUserById(c.getString(3));
                User user2 = userDataSource.getUserById(c.getString(4));

                // check if the latest friendship between user is still friend
                FriendShip friendShip = findLastestFriendShip(user1, user2);

                // Check if the latest friendship is accepted
                if (friendShip != null && friendShip.getStatus().equals(FriendShip.FriendShipStatus.ACCEPTED.getStatus())) {
                    friendShipSet.add(friendShip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendShipSet;
    }


    @Override
    public void close() {
        databaseManager.closeDatabase();
    }
}
