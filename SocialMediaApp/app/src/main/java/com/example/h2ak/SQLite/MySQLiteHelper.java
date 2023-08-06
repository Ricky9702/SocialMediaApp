package com.example.h2ak.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseUserDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseUserDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.User;

import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "social_network.db";
    private static final int DATABASE_VERSION = 5;


    // User
    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_IS_ACTIVE = "is_active";
    public static final String COLUMN_USER_IMAGE_AVATAR = "image_avatar";
    public static final String COLUMN_USER_IMAGE_COVER = "image_cover";
    public static final String COLUMN_USER_BIO = "bio";
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_BIRTHDAY = "birthday";
    public static final String COLUMN_USER_CREATED_DATE = "created_date";
    public static final String COLUMN_USER_USER_ROLE = "user_role";
    public static final String COLUMN_USER_IS_ONLINE = "is_online";
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " TEXT PRIMARY KEY NOT NULL, "
            + COLUMN_USER_NAME + " TEXT NOT NULL, "
            + COLUMN_USER_GENDER + " TEXT NOT NULL, "
            + COLUMN_USER_BIRTHDAY + " TEXT NOT NULL, "
            + COLUMN_USER_EMAIL + " TEXT NOT NULL, "
            + COLUMN_USER_IMAGE_AVATAR +" TEXT , "
            + COLUMN_USER_IMAGE_COVER + " TEXT, "
            + COLUMN_USER_BIO +  " " + "TEXT, "
            + COLUMN_USER_CREATED_DATE + " " + "TEXT, "
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL, "
            + COLUMN_USER_USER_ROLE + " TEXT NOT NULL, "
            + COLUMN_USER_IS_ONLINE + " INTEGER, "
            + COLUMN_USER_IS_ACTIVE + " INTEGER);";


    // Search History
    public static final String COLUMN_SEARCH_ID = "search_id";
    public static final String COLUMN_SEARCH_USER_1 = "user_1_id";
    public static final String COLUMN_SEARCH_USER_2 = "user_2_id";
    public static final String TABLE_SEARCH_HISTORY = "search_history";

    private static final String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE " + TABLE_SEARCH_HISTORY + " (" +
            COLUMN_SEARCH_ID + " TEXT PRIMARY KEY, " +
            COLUMN_SEARCH_USER_1 + " TEXT NOT NULL, " +
            COLUMN_SEARCH_USER_2 + " TEXT NOT NULL, " +
            "FOREIGN KEY (" + COLUMN_SEARCH_USER_1 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), " +
            "FOREIGN KEY (" + COLUMN_SEARCH_USER_2 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "));";

    // Inbox
    public static final String TABLE_INBOX = "inbox";
    public static final String COLUMN_INBOX_ID = "inbox_id";
    public static final String COLUMN_INBOX_TYPE = "type";
    public static final String COLUMN_INBOX_CONTENT = "content";
    public static final String COLUMN_INBOX_IS_READ = "is_read";
    public static final String COLUMN_INBOX_IS_ACTIVE = "is_active";
    public static final String COLUMN_INBOX_USER_1 = COLUMN_SEARCH_USER_1;
    public static final String COLUMN_INBOX_USER_2 = COLUMN_SEARCH_USER_2;
    public static final String COLUMN_INBOX_CREATED_DATE = "created_date";

    private static final String CREATE_TABLE_INBOX = "CREATE TABLE " + TABLE_INBOX
            + " (" + COLUMN_INBOX_ID + " TEXT PRIMARY KEY , "
            + COLUMN_INBOX_CONTENT + " TEXT NOT NULL, "
            + COLUMN_INBOX_CREATED_DATE + " TEXT NOT NULL, "
            + COLUMN_INBOX_TYPE + " TEXT NOT NULL, "
            + COLUMN_INBOX_IS_READ + " INTEGER NOT NULL, "
            + COLUMN_INBOX_IS_ACTIVE + " INTEGER NOT NULL, "
            + COLUMN_INBOX_USER_1 + " INTEGER, "
            + COLUMN_INBOX_USER_2 + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_INBOX_USER_1 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), "
            + "FOREIGN KEY (" + COLUMN_INBOX_USER_2 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "));";


    // FriendShip
    public static final String TABLE_FRIENDSHIP = "friendship";
    public static final String COLUMN_FRIENDSHIP_ID = "friendship_id";
    public static final String COLUMN_FRIENDSHIP_STATUS = "status";
    public static final String COLUMN_FRIENDSHIP_USER_1 = COLUMN_SEARCH_USER_1;
    public static final String COLUMN_FRIENDSHIP_USER_2 = COLUMN_SEARCH_USER_2;
    public static final String COLUMN_FRIENDSHIP_CREATED_DATE = "created_date";
    private static final String CREATE_TABLE_FRIENDSHIP = "CREATE TABLE " + TABLE_FRIENDSHIP
            + " ( " + COLUMN_FRIENDSHIP_ID + " TEXT  PRIMARY KEY, "
            + COLUMN_FRIENDSHIP_CREATED_DATE + " TEXT, "
            + COLUMN_FRIENDSHIP_STATUS + " TEXT, "
            + COLUMN_FRIENDSHIP_USER_1 + " INTEGER, "
            + COLUMN_FRIENDSHIP_USER_2 + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_FRIENDSHIP_USER_1 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), "
            + "FOREIGN KEY (" + COLUMN_FRIENDSHIP_USER_2 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "));";

    // Create Database

    private UserDataSource userDataSource;
    private Context context;
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Create multiple tables
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USER);
        database.execSQL(CREATE_TABLE_FRIENDSHIP);
        database.execSQL(CREATE_TABLE_INBOX);
        database.execSQL(CREATE_TABLE_SEARCH_HISTORY);

    }
    // Upgrade version database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDSHIP);
        // Recreate all tables
        onCreate(db);
    }
}