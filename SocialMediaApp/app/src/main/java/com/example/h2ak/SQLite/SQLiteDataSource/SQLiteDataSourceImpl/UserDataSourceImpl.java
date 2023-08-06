package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseUserDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseUserDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.PasswordHashing;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDataSourceImpl implements UserDataSource {

    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private FirebaseUserDataSource firebaseUserDataSource;
    private static UserDataSourceImpl instance;
    private String currentUserId;
    private static int instanceCount = 0;

    private static AtomicInteger count = new AtomicInteger(0);

    private UserDataSourceImpl(Context context, String currentUserId) {
        firebaseUserDataSource = FirebaseUserDataSourceImpl.getInstance();
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        this.setCurrentUserId(currentUserId);
        Log.d("count x1", count.incrementAndGet()+"");
    }

    public static synchronized UserDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            Log.d("count X2", count.incrementAndGet()+"");
            instance = new UserDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        instanceCount++;
        return instance;
    }


    public void close() {
        databaseManager.closeDatabase();
    }


    @Override
    public boolean createUser(User user) {
        db.beginTransaction();
        boolean result = false;
        try {
            Log.d("createUser : ", "LOCAL");
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_USER_ID, user.getId().trim());
            values.put(MySQLiteHelper.COLUMN_USER_NAME, user.getName());
            values.put(MySQLiteHelper.COLUMN_USER_GENDER, user.getGender());
            values.put(MySQLiteHelper.COLUMN_USER_BIRTHDAY, user.getBirthday());
            values.put(MySQLiteHelper.COLUMN_USER_PASSWORD, user.getPassword()); // already hashed in register
            values.put(MySQLiteHelper.COLUMN_USER_EMAIL, user.getEmail());
            values.put(MySQLiteHelper.COLUMN_USER_BIO, user.getBio() != null ? user.getBio() : "");
            values.put(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR, user.getImageAvatar() != null ? user.getImageAvatar() : "");
            values.put(MySQLiteHelper.COLUMN_USER_IMAGE_COVER, user.getImageCover() != null ? user.getImageCover() : "");
            values.put(MySQLiteHelper.COLUMN_USER_CREATED_DATE, user.getCreatedDate());
            values.put(MySQLiteHelper.COLUMN_USER_IS_ACTIVE, user.isActive());
            values.put(MySQLiteHelper.COLUMN_USER_IS_ONLINE, user.isOnline());
            values.put(MySQLiteHelper.COLUMN_USER_USER_ROLE, user.getRole());
            result = db.insert(MySQLiteHelper.TABLE_USER, null, values) > 0;
            db.setTransactionSuccessful();
        } catch (Exception exception) {
            Log.d("CreateUser", exception.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public User getUserById(String id) {
        User user = null;
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_USER
                        + " WHERE " + MySQLiteHelper.COLUMN_USER_ID + " = ?",
                new String[]{id})) {
            //if user is exists
            if (cursor.moveToFirst()) {
                user = getUserByCursor(cursor);
            }
        }
        return user;
    }

    public User getUserByCursor(Cursor cursor) {
        // id - name - gender - birthday - email - imgAvatar - imgCover - bio - createdDate - password - isActive
        User user = new User();

        // index columns
        int id = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_ID);
        int name = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_NAME);
        int birthday = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_BIRTHDAY);
        int gender = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_GENDER);
        int email = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_EMAIL);
        int avatar = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR);
        int cover = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_IMAGE_COVER);
        int bio = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_BIO);
        int createdDate = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_CREATED_DATE);
        int password = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_PASSWORD);
        int isActive = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_IS_ACTIVE);
        int role = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_USER_ROLE);
        int isOnline = cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_IS_ONLINE);

        // set data
        if (id != -1) user.setId(cursor.getString(id));
        if (name != -1) user.setName(cursor.getString(name));
        if (birthday != -1) user.setBirthday(cursor.getString(birthday));
        if (gender != -1) user.setGender(cursor.getString(gender));
        if (email != -1) user.setEmail(cursor.getString(email));
        if (avatar != -1) user.setImageAvatar(cursor.getString(avatar));
        if (cover != -1) user.setImageCover(cursor.getString(cover));
        if (bio != -1) user.setBio(cursor.getString(bio));
        if (createdDate != -1) user.setCreatedDate(cursor.getString(createdDate));
        if (password != -1) user.setPassword(cursor.getString(password));
        if (isActive != -1) user.setActive(cursor.getInt(isActive) == 1 ? true : false);
        if (role != -1) user.setRole(cursor.getString(role));
        if (isOnline != -1) user.setOnline(cursor.getInt(isOnline) == 1);

        return user;
    }

    @Override
    public List<User> getAllUsers(Map<String, String> params) {
        List<User> userList = new ArrayList<>();

        if (params == null) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_USER, null);
            while (cursor.moveToNext()) {
                userList.add(getUserByCursor(cursor));
            }
        } else {
            String currentId = params.get("id");
            String kw = params.get("kw");

            if (kw != null && !kw.isEmpty()) {
                Cursor cursor = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_USER + " WHERE " +
                                MySQLiteHelper.COLUMN_USER_NAME + " LIKE ? AND " + MySQLiteHelper.COLUMN_USER_ID + " != ?",
                        new String[]{"%" + kw + "%", currentId});

                while (cursor.moveToNext())
                    userList.add(getUserByCursor(cursor));
            }

        }
        return userList;
    }

    @Override
    public boolean updateCurrentUser(User user) {
        Log.d("FirebaseUpdateUser: user", user.getEmail()+"");
        db.beginTransaction();
        try {
            User currentUser = this.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid());
            if (user.getId().equals(currentUser.getId())) {

                User firebaseUser = currentUser;
                if (currentUser != null && user != null) {

                    ContentValues contentValues = new ContentValues();

                    if (!currentUser.getName().equals(user.getName())) {
                        contentValues.put(MySQLiteHelper.COLUMN_USER_NAME, user.getName());
                        firebaseUser.setName(user.getName());
                    }

                    if (user.getBio() != null && !user.getBio().equals(currentUser.getBio())) {
                        contentValues.put(MySQLiteHelper.COLUMN_USER_BIO, user.getBio());
                        firebaseUser.setBio(user.getBio());
                    }
                    if (user.getImageAvatar() != null) {
                        contentValues.put(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR, user.getImageAvatar());
                        firebaseUser.setImageAvatar(user.getImageAvatar());
                    }

                    if (user.getImageCover() != null) {
                        contentValues.put(MySQLiteHelper.COLUMN_USER_IMAGE_COVER, user.getImageCover());
                        firebaseUser.setImageCover(user.getImageCover());
                    }

                    if (user.getBirthday() != null && !currentUser.getBirthday().equals(user.getBirthday())) {
                        contentValues.put(MySQLiteHelper.COLUMN_USER_BIRTHDAY, user.getBirthday());
                        firebaseUser.setBirthday(user.getBirthday());
                    }

                    if (user.getGender() != null && !currentUser.getGender().equals(user.getGender())) {
                        contentValues.put(MySQLiteHelper.COLUMN_USER_GENDER, user.getGender());
                        firebaseUser.setGender(user.getGender());
                    }

                if ( user.getPassword() != null && currentUser.getPassword() != null && !user.getPassword().equals(currentUser.getPassword()) && !PasswordHashing.verifyPassword(user.getPassword(), currentUser.getPassword())) {
                    String password = PasswordHashing.hashPassword(user.getPassword());
                    Log.d("password", password);
                    contentValues.put(MySQLiteHelper.COLUMN_USER_PASSWORD, password);
                    firebaseUser.setPassword(password);
                }

                    if (user.getRole() != null && !currentUser.getRole().equals(user.getRole())) {
                        contentValues.put(MySQLiteHelper.COLUMN_USER_USER_ROLE, user.getRole());
                        firebaseUser.setRole(user.getRole());
                    }

                    if (user.isActive() != currentUser.isActive()) {
                        contentValues.put(MySQLiteHelper.COLUMN_USER_IS_ACTIVE, user.isActive());
                    }
                    Log.d("Sqlite transaction", "Update current user");

                    firebaseUserDataSource.updateUser(firebaseUser);
                     return db.update(MySQLiteHelper.TABLE_USER, contentValues, MySQLiteHelper.COLUMN_USER_ID + "= ?",
                            new String[]{String.valueOf(user.getId())}) > 0;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Sqlite transaction", "Error updating" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return false;
    }

    @Override
    public boolean UpdateUserChangeOnFirebase(User user) {
        User found = this.getUserById(user.getId());
        Log.d("updateUser : ", "LOCAL FIREBASE");
        db.beginTransaction();
        boolean result = false;
        try {
            if (found != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_USER_NAME, user.getName());
                contentValues.put(MySQLiteHelper.COLUMN_USER_BIO, user.getBio());
                contentValues.put(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR, user.getImageAvatar());
                contentValues.put(MySQLiteHelper.COLUMN_USER_IMAGE_COVER, user.getImageCover());
                contentValues.put(MySQLiteHelper.COLUMN_USER_BIRTHDAY, user.getBirthday());
                contentValues.put(MySQLiteHelper.COLUMN_USER_GENDER, user.getGender());
                contentValues.put(MySQLiteHelper.COLUMN_USER_USER_ROLE, user.getRole());
                contentValues.put(MySQLiteHelper.COLUMN_USER_IS_ACTIVE, user.isActive());
                contentValues.put(MySQLiteHelper.COLUMN_USER_IS_ONLINE, user.isOnline());
                contentValues.put(MySQLiteHelper.COLUMN_USER_PASSWORD, user.getPassword());
                result = db.update(MySQLiteHelper.TABLE_USER, contentValues, MySQLiteHelper.COLUMN_USER_ID + "= ?",
                        new String[]{String.valueOf(user.getId())}) > 0;
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
    public void deleteUser(int userId) {

    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}
