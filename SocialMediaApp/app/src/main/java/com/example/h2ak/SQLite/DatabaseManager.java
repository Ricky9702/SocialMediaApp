package com.example.h2ak.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseManager {
    private static DatabaseManager databaseManager;
    private SQLiteDatabase db;
    private MySQLiteHelper helper;
    private static final Object lock = new Object();

    private DatabaseManager(Context context) {
        helper = new MySQLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            synchronized (lock) {
                if (databaseManager == null) {
                    databaseManager = new DatabaseManager(context.getApplicationContext());
                }
            }
        }
        return databaseManager;
    }

    public SQLiteDatabase getDatabase() {
        return this.db;
    }

    public synchronized void closeDatabase() {
        if (helper != null) {
            helper.close();
            databaseManager = null;
        }
    }
}

