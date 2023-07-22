package com.example.h2ak.datasource.datasourceImpl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.h2ak.database.DatabaseManager;
import com.example.h2ak.database.MySQLiteHelper;
import com.example.h2ak.datasource.UserDataSource;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.PasswordHashing;

import java.util.List;

public class UserDataSourceImpl implements UserDataSource {

    private SQLiteDatabase db;
    private DatabaseManager databaseManager;

    public UserDataSourceImpl(Context context) {
        databaseManager = DatabaseManager.getInstance(context);
        open();
    }

    private void open() {
        db = databaseManager.getDatabase();
    }

    private void close() {
        databaseManager.closeDatabase();
    }


    @Override
    public User addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, user.getName());
        values.put(MySQLiteHelper.COLUMN_GENDER, user.getGender());
        values.put(MySQLiteHelper.COLUMN_BIRTHDAY, user.getBirthday());
        values.put(MySQLiteHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(MySQLiteHelper.COLUMN_EMAIL, user.getEmail());
        values.put(MySQLiteHelper.BIO, user.getBio());
        values.put(MySQLiteHelper.IMAGE_AVATAR, user.getImageAvatar());
        values.put(MySQLiteHelper.IMAGE_COVER, user.getImageCover());
        values.put(MySQLiteHelper.CREATED_DATE, user.getCreatedDate());
        values.put(MySQLiteHelper.COLUMN_IS_ACTIVE, user.isActive());

        long result = db.insert(MySQLiteHelper.TABLE_USER, null, values);
        return result != -1 ? user : null;
    }

    @Override
    public User getUserById(int userId) {
        User user = null;
        Cursor cursor = db.query(
                //table name
                MySQLiteHelper.TABLE_USER,
                //select columns
                new String[]{
                        MySQLiteHelper.COLUMN_ID_USER,
                        MySQLiteHelper.COLUMN_NAME,
                        MySQLiteHelper.COLUMN_GENDER,
                        MySQLiteHelper.COLUMN_BIRTHDAY,
                        MySQLiteHelper.COLUMN_PASSWORD,
                        MySQLiteHelper.COLUMN_EMAIL,
                        MySQLiteHelper.BIO,
                        MySQLiteHelper.IMAGE_AVATAR,
                        MySQLiteHelper.IMAGE_COVER,
                        MySQLiteHelper.CREATED_DATE,
                        MySQLiteHelper.COLUMN_IS_ACTIVE
                },
                //where
                MySQLiteHelper.COLUMN_ID_USER + "= ?",
                //pass id
                new String[]{String.valueOf(userId)}, null, null, null);

        //if user is exists
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setGender(cursor.getString(2));
            user.setBirthday(cursor.getString(3));
            user.setPassword(cursor.getString(4));
            user.setEmail(cursor.getString(5));
            user.setBio(cursor.getString(6));
            user.setImageAvatar(cursor.getString(7));
            user.setImageCover(cursor.getString(8));
            user.setCreatedDate(cursor.getString(9));
            user.setActive(cursor.getInt(10) == 1);
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = null;
        Cursor cursor = db.query(
                //table name
                MySQLiteHelper.TABLE_USER,
                //select columns
                new String[]{
                        MySQLiteHelper.COLUMN_ID_USER,
                        MySQLiteHelper.COLUMN_NAME,
                        MySQLiteHelper.COLUMN_GENDER,
                        MySQLiteHelper.COLUMN_BIRTHDAY,
                        MySQLiteHelper.COLUMN_PASSWORD,
                        MySQLiteHelper.COLUMN_EMAIL,
                        MySQLiteHelper.BIO,
                        MySQLiteHelper.IMAGE_AVATAR,
                        MySQLiteHelper.IMAGE_COVER,
                        MySQLiteHelper.CREATED_DATE,
                        MySQLiteHelper.COLUMN_IS_ACTIVE
                },
                //where
                MySQLiteHelper.COLUMN_EMAIL + "= ?",
                //pass id
                new String[]{String.valueOf(email)}, null, null, null);

        //if user is exists
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setGender(cursor.getString(2));
            user.setBirthday(cursor.getString(3));
            user.setPassword(cursor.getString(4));
            user.setEmail(cursor.getString(5));
            user.setBio(cursor.getString(6));
            user.setImageAvatar(cursor.getString(7));
            user.setImageCover(cursor.getString(8));
            user.setCreatedDate(cursor.getString(9));
            user.setActive(cursor.getInt(10) == 1);
        }
        return user;
    }


    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.IMAGE_AVATAR, user.getImageAvatar());
        contentValues.put(MySQLiteHelper.IMAGE_COVER, user.getImageCover());
        contentValues.put(MySQLiteHelper.BIO, user.getBio());
        contentValues.put(MySQLiteHelper.COLUMN_NAME, user.getName());
        contentValues.put(MySQLiteHelper.COLUMN_BIRTHDAY, user.getBirthday());
        contentValues.put(MySQLiteHelper.COLUMN_GENDER, user.getGender());
//        contentValues.put(MySQLiteHelper.COLUMN_EMAIL, user.getEmail());
        contentValues.put(MySQLiteHelper.COLUMN_PASSWORD, PasswordHashing.hashPassword(user.getPassword()));

        int result = db.update(MySQLiteHelper.TABLE_USER, contentValues, MySQLiteHelper.COLUMN_ID_USER + "= ?",
                new String[]{String.valueOf(user.getId())});

        return result > 0;
    }

    @Override
    public boolean updateActiveUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.COLUMN_IS_ACTIVE, user.isActive());

        int result = db.update(MySQLiteHelper.TABLE_USER, contentValues, MySQLiteHelper.COLUMN_ID_USER + "= ?",
                new String[]{String.valueOf(user.getId())});
        return result > 0;
    }


    @Override
    public void deleteUser(int userId) {

    }
}
