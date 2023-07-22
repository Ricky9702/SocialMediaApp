package com.example.h2ak.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseManager {
    private static DatabaseManager databaseManager;
    private SQLiteDatabase db;

    private DatabaseManager(Context context) {
        MySQLiteHelper helper = new MySQLiteHelper(context);
        db = helper.getReadableDatabase();
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context.getApplicationContext());
        }
        return databaseManager;
    }


    public SQLiteDatabase getDatabase() {
        return this.db;
    }

    public synchronized void closeDatabase() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
