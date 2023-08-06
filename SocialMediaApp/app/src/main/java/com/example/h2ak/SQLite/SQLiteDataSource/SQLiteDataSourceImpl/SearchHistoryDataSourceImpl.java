package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.SearchHistoryDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.SearchHistory;
import com.example.h2ak.model.User;

import java.util.HashSet;
import java.util.Set;

public class SearchHistoryDataSourceImpl implements SearchHistoryDataSource {

    private static SearchHistoryDataSourceImpl instance;
    private DatabaseManager databaseManager;
    private SQLiteDatabase db;
    private UserDataSource userDataSource;
    private String currentUserId;

    private SearchHistoryDataSourceImpl(Context context, String currentUserId) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        userDataSource = UserDataSourceImpl.getInstance(context);
        this.currentUserId = currentUserId;
    }

    public static synchronized SearchHistoryDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new SearchHistoryDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }


    @Override
    public boolean createSearchHistory(SearchHistory searchHistory, boolean allowInsertInFirebase) {
        db.beginTransaction();
        boolean result = false;
        try {
            ContentValues contentValues = new ContentValues();

            if (searchHistory.getId() == null || searchHistory.getId().isEmpty()) {
                Log.d("createSearchHistory: ", "id is null");
                return false;
            } else if (searchHistory.getCurrentUser() == null) {
                Log.d("createSearchHistory: ", "currentUser is null");
                return false;
            } else if (searchHistory.getSearchingUser() == null) {
                Log.d("createSearchHistory: ", "searchingUser is null");
                return false;
            } else {

                if (allowInsertInFirebase) {
                    // do insert into firebase
                }

                contentValues.put(MySQLiteHelper.COLUMN_SEARCH_ID, searchHistory.getId());
                contentValues.put(MySQLiteHelper.COLUMN_SEARCH_USER_1, searchHistory.getCurrentUser().getId());
                contentValues.put(MySQLiteHelper.COLUMN_SEARCH_USER_2, searchHistory.getSearchingUser().getId());
                result = db.insert(MySQLiteHelper.TABLE_SEARCH_HISTORY, null, contentValues) > 0;
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.d("createSearchHistory Error: ", ex.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public boolean deleteSearchHistory(SearchHistory searchHistory, boolean allowDeleteInFirebase) {
        db.beginTransaction();
        boolean result = false;
        try {

            if (searchHistory.getId() == null || searchHistory.getId().isEmpty()) {
                Log.d("deleteSearchHistory: ", "id is null");
                return false;
            } else if (searchHistory.getCurrentUser() == null) {
                Log.d("deleteSearchHistory: ", "currentUser is null");
                return false;
            } else if (searchHistory.getSearchingUser() == null) {
                Log.d("deleteSearchHistory: ", "searchingUser is null");
                return false;
            } else {

                if (allowDeleteInFirebase) {
                    // do delete in firebase
                }

                result = db.delete(MySQLiteHelper.TABLE_SEARCH_HISTORY,
                        MySQLiteHelper.COLUMN_SEARCH_ID + " = ? ",
                        new String[]{String.valueOf(searchHistory.getId())}) > 0;
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {

        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public Set<SearchHistory> getAllSearchHistoryByUser(User user) {
        Set<SearchHistory> searchHistorySet = new HashSet<>();
        try(Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_SEARCH_HISTORY
                + " WHERE " + MySQLiteHelper.COLUMN_SEARCH_USER_1 + " = ? "
                + " ORDER BY " + MySQLiteHelper.COLUMN_SEARCH_ID + " DESC ", new String[]{user.getId()})) {
            while (c.moveToNext()) {
                searchHistorySet.add(getSearchHistoryByCursor(c));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return searchHistorySet;
    }

    @Override
    public SearchHistory findSearchHistory(User currentUser, User searchingUser) {
        try(Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_SEARCH_HISTORY
                + " WHERE " + MySQLiteHelper.COLUMN_SEARCH_USER_1 + " = ? "
                + " AND " + MySQLiteHelper.COLUMN_SEARCH_USER_2 + " = ? "
                + " ORDER BY " + MySQLiteHelper.COLUMN_SEARCH_ID + " DESC "
                + " LIMIT 1 ", new String[]{currentUser.getId(), searchingUser.getId()})) {
            if (c.moveToFirst()) {
                return getSearchHistoryByCursor(c);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public SearchHistory getSearchHistoryByCursor(Cursor c) {
        int id = c.getColumnIndex(MySQLiteHelper.COLUMN_SEARCH_ID);
        int user1 = c.getColumnIndex(MySQLiteHelper.COLUMN_SEARCH_USER_1);
        int user2 = c.getColumnIndex(MySQLiteHelper.COLUMN_SEARCH_USER_2);

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setId(c.getString(id));
        searchHistory.setCurrentUser(userDataSource.getUserById(c.getString(user1)));
        searchHistory.setSearchingUser(userDataSource.getUserById(c.getString(user2)));

        return searchHistory;
    }

}
