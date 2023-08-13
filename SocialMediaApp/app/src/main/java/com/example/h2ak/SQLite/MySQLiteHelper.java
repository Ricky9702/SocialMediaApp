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
    public static final String TABLE_USER = "user";
    public static final String COLUMN_POST_USER_ID = "user_id";
    public static final String COLUMN_USER_ID = COLUMN_POST_USER_ID;
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_IS_ACTIVE = "is_active";
    public static final String COLUMN_USER_IMAGE_AVATAR = "image_avatar";
    public static final String COLUMN_USER_IMAGE_COVER = "image_cover";
    public static final String COLUMN_USER_BIO = "bio";
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_BIRTHDAY = "birthday";
    public static final String COLUMN_POST_REACTION_CREATED_DATE = "created_date";
    public static final String COLUMN_USER_CREATED_DATE = COLUMN_POST_REACTION_CREATED_DATE;
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
    public static final String COLUMN_POST_REACTION_TYPE = "type";
    public static final String COLUMN_INBOX_TYPE = COLUMN_POST_REACTION_TYPE;
    public static final String COLUMN_POST_CONTENT = "content";
    public static final String COLUMN_INBOX_CONTENT = COLUMN_POST_CONTENT;
    public static final String COLUMN_INBOX_IS_READ = "is_read";
    public static final String COLUMN_INBOX_IS_ACTIVE = "is_active";
    public static final String COLUMN_INBOX_USER_1 = COLUMN_SEARCH_USER_1;
    public static final String COLUMN_INBOX_USER_2 = COLUMN_SEARCH_USER_2;
    public static final String COLUMN_INBOX_CREATED_DATE = COLUMN_POST_REACTION_CREATED_DATE;

    private static final String CREATE_TABLE_INBOX = "CREATE TABLE " + TABLE_INBOX
            + " (" + COLUMN_INBOX_ID + " TEXT PRIMARY KEY , "
            + COLUMN_INBOX_CONTENT + " TEXT NOT NULL, "
            + COLUMN_INBOX_CREATED_DATE + " TEXT NOT NULL, "
            + COLUMN_INBOX_TYPE + " TEXT NOT NULL, "
            + COLUMN_INBOX_IS_READ + " INTEGER NOT NULL, "
            + COLUMN_INBOX_IS_ACTIVE + " INTEGER NOT NULL, "
            + COLUMN_INBOX_USER_1 + " INTEGER, "
            + COLUMN_INBOX_USER_2 + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_INBOX_USER_1 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, "
            + "FOREIGN KEY (" + COLUMN_INBOX_USER_2 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE );";


    // FriendShip
    public static final String TABLE_FRIENDSHIP = "friendship";
    public static final String COLUMN_FRIENDSHIP_ID = "friendship_id";
    public static final String COLUMN_FRIENDSHIP_STATUS = "status";
    public static final String COLUMN_FRIENDSHIP_USER_1 = COLUMN_SEARCH_USER_1;
    public static final String COLUMN_FRIENDSHIP_USER_2 = COLUMN_SEARCH_USER_2;
    public static final String COLUMN_FRIENDSHIP_CREATED_DATE = COLUMN_POST_REACTION_CREATED_DATE;
    private static final String CREATE_TABLE_FRIENDSHIP = "CREATE TABLE " + TABLE_FRIENDSHIP
            + " ( " + COLUMN_FRIENDSHIP_ID + " TEXT  PRIMARY KEY, "
            + COLUMN_FRIENDSHIP_CREATED_DATE + " TEXT, "
            + COLUMN_FRIENDSHIP_STATUS + " TEXT, "
            + COLUMN_FRIENDSHIP_USER_1 + " INTEGER, "
            + COLUMN_FRIENDSHIP_USER_2 + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_FRIENDSHIP_USER_1 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, "
            + "FOREIGN KEY (" + COLUMN_FRIENDSHIP_USER_2 + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE );";

    // Post
    public static final String TABLE_POST = "post";
    public static final String COLUMN_POST_ID = TABLE_POST + "_id";
    public static final String COLUMN_POST_CREATED_DATE = COLUMN_POST_REACTION_CREATED_DATE;
    public static final String COLUMN_POST_PRIVACY = "post_privacy";

    private static final String CREATE_TABLE_POST = "CREATE TABLE " + TABLE_POST
            + " ( " + COLUMN_POST_ID + " TEXT PRIMARY KEY, "
            + COLUMN_POST_CONTENT + " TEXT, "
            + COLUMN_POST_USER_ID + " TEXT NOT NULL,"
            + COLUMN_POST_CREATED_DATE + " TEXT NOT NULL, "
            + COLUMN_POST_PRIVACY + " TEXT NOT NULL, "
            + " FOREIGN KEY ( " + COLUMN_POST_USER_ID + " ) REFERENCES " + TABLE_USER  + "( "+ COLUMN_USER_ID  +" )" +" ON DELETE CASCADE )";

    // Post Images
    public static final String TABLE_POST_IMAGES = "post_images";
    public static final String COLUMN_POST_IMAGES_ID = "post_images_id";
    public static final String COLUMN_POST_IMAGES_IMAGE_URL = "image_url";
    public static final String COLUMN_POST_IMAGES_POST_ID = "post_id";
    public static final String COLUMN_POST_IMAGES_CREATED_DATE = COLUMN_POST_REACTION_CREATED_DATE;

    private static final String CREATE_TABLE_POST_IMAGES = " CREATE TABLE " + TABLE_POST_IMAGES + " (" +
            " " + COLUMN_POST_IMAGES_ID + " TEXT PRIMARY KEY, " +
            " " + COLUMN_POST_IMAGES_IMAGE_URL + " TEXT NOT NULL, " +
            " " + COLUMN_POST_IMAGES_POST_ID + " TEXT NOT NULL, " +
            " " + COLUMN_POST_IMAGES_CREATED_DATE +  " TEXT NOT NULL,  " +
            " FOREIGN KEY ( " + COLUMN_POST_IMAGES_POST_ID + " ) REFERENCES " + TABLE_POST + "( " +  COLUMN_POST_ID + " )" + " ON DELETE CASCADE )";



    // Post Reaction
    public static final String TABLE_POST_REACTION = "post_reaction";
    public static final String COLUMN_POST_REACTION_ID = TABLE_POST_REACTION + "_id";
    public static final String COLUMN_POST_REACTION_USER_ID = TABLE_POST_REACTION + "_user_id";
    public static final String COLUMN_POST_REACTION_POST_ID = TABLE_POST_REACTION + "_post_id";
    private static final String CREATE_TABLE_POST_REACTION = " CREATE TABLE " + TABLE_POST_REACTION + " (" +
            " " + COLUMN_POST_REACTION_ID + " TEXT PRIMARY KEY, " +
            " " + COLUMN_POST_REACTION_TYPE + " TEXT NOT NULL, " +
            " " + COLUMN_POST_REACTION_CREATED_DATE + " TEXT NOT NULL, " +
            " " + COLUMN_POST_REACTION_USER_ID + " TEXT NOT NULL, " +
            COLUMN_POST_REACTION_POST_ID + " TEXT NOT NULL, " +
            " FOREIGN KEY ( " + COLUMN_POST_REACTION_USER_ID + " ) REFERENCES " + TABLE_USER + "( " +  COLUMN_USER_ID + " )" + " ON DELETE CASCADE , " +
            " FOREIGN KEY ( " + COLUMN_POST_REACTION_POST_ID + " ) REFERENCES " + TABLE_POST + "( " + COLUMN_POST_ID  + " )"  + " ON DELETE CASCADE ) ";


    // Post Comment
    public static final String TABLE_POST_COMMENT = "post_comment";
    public static final String COLUMN_POST_COMMENT_ID = TABLE_POST_COMMENT + "_id";
    public static final String COLUMN_POST_COMMENT_USER_ID = TABLE_POST_COMMENT + "_user_id";
    public static final String COLUMN_POST_COMMENT_POST_ID = TABLE_POST_COMMENT + "_post_id";
    public static final String COLUMN_POST_COMMENT_PARENT_ID = TABLE_POST_COMMENT + "_parent_id";
    public static final String COLUMN_POST_COMMENT_CREATED_DATE = "created_date";
    public static final String COLUMN_POST_COMMENT_CONTENT = "content";
    private static final String CREATE_TABLE_POST_COMMENT = " CREATE TABLE " + TABLE_POST_COMMENT + " (" +
            " " + COLUMN_POST_COMMENT_ID + " TEXT PRIMARY KEY, " +
            " " + COLUMN_POST_COMMENT_CREATED_DATE + " TEXT NOT NULL, " +
            " " + COLUMN_POST_COMMENT_CONTENT + " TEXT NOT NULL, " +
            " " + COLUMN_POST_COMMENT_USER_ID + " TEXT NOT NULL, " +
            " " + COLUMN_POST_COMMENT_PARENT_ID + " TEXT NOT NULL, " +
            COLUMN_POST_COMMENT_POST_ID + " TEXT NOT NULL, " +
            " FOREIGN KEY ( " + COLUMN_POST_COMMENT_PARENT_ID + " ) REFERENCES " + TABLE_POST_COMMENT + "( " +  COLUMN_POST_COMMENT_ID + " )" + " ON DELETE CASCADE , " +
            " FOREIGN KEY ( " + COLUMN_POST_COMMENT_USER_ID + " ) REFERENCES " + TABLE_USER + "( " +  COLUMN_USER_ID + " )" + " ON DELETE CASCADE , " +
            " FOREIGN KEY ( " + COLUMN_POST_COMMENT_POST_ID + " ) REFERENCES " + TABLE_POST + "( " + COLUMN_POST_ID  + " )"  + " ON DELETE CASCADE ) ";
    
    // Post Comment Reaction
    public static final String TABLE_POST_COMMENT_REACTION = "post_comment_reaction";
    public static final String COLUMN_POST_COMMENT_REACTION_ID = TABLE_POST_COMMENT_REACTION + "_id";
    public static final String COLUMN_POST_COMMENT_REACTION_USER_ID = TABLE_POST_COMMENT_REACTION + "_user_id";
    public static final String COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID = TABLE_POST_COMMENT_REACTION + "_post_comment_id";
    public static final String COLUMN_POST_COMMENT_REACTION_TYPE = "type";
    public static final String COLUMN_POST_COMMENT_REACTION_CREATED_DATE = "created_date" ;
    public static final String CREATE_TABLE_POST_COMMENT_REACTION = " CREATE TABLE " + TABLE_POST_COMMENT_REACTION + " (" +
            " " + COLUMN_POST_COMMENT_REACTION_ID + " TEXT PRIMARY KEY, " +
            " " + COLUMN_POST_COMMENT_REACTION_TYPE + " TEXT NOT NULL, " +
            " " + COLUMN_POST_COMMENT_REACTION_CREATED_DATE + " TEXT NOT NULL, " +
            " " + COLUMN_POST_COMMENT_REACTION_USER_ID + " TEXT NOT NULL, " +
            COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID + " TEXT NOT NULL, " +
            " FOREIGN KEY ( " + COLUMN_POST_COMMENT_REACTION_USER_ID + " ) REFERENCES " + TABLE_USER + "( " +  COLUMN_USER_ID + " )" + " ON DELETE CASCADE , " +
            " FOREIGN KEY ( " + COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID + " ) REFERENCES " + TABLE_POST_COMMENT + "( " + COLUMN_POST_COMMENT_ID  + " )"  + " ON DELETE CASCADE ) ";


    // Create Database
    private Context context;
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Create multiple tables
    @Override
    public void onCreate(SQLiteDatabase database) {
//        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
//        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDSHIP);
//        database.execSQL("DROP TABLE IF EXISTS " + TABLE_INBOX);
//        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_HISTORY);
        database.execSQL(CREATE_TABLE_USER);
        database.execSQL(CREATE_TABLE_FRIENDSHIP);
        database.execSQL(CREATE_TABLE_INBOX);
        database.execSQL(CREATE_TABLE_SEARCH_HISTORY);
        database.execSQL(CREATE_TABLE_POST);
        database.execSQL(CREATE_TABLE_POST_IMAGES);
        database.execSQL(CREATE_TABLE_POST_REACTION);
        database.execSQL(CREATE_TABLE_POST_COMMENT);
        database.execSQL(CREATE_TABLE_POST_COMMENT_REACTION);

    }
    // Upgrade version database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        // Drop all tables
        // Recreate all tables
        onCreate(db);
    }
}