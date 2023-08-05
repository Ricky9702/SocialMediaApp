package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    private SearchHistoryDataSourceImpl(Context context) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        userDataSource = UserDataSourceImpl.getInstance(context);
    }

    public static synchronized SearchHistoryDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new SearchHistoryDataSourceImpl(context.getApplicationContext());
        }
        return instance;
    }


    @Override
    public boolean createSearchHistory(SearchHistory searchHistory) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.COLUMN_SEARCH_USER_1, searchHistory.getCurrentUser().getId());
        contentValues.put(MySQLiteHelper.COLUMN_SEARCH_USER_2, searchHistory.getSearchingUser().getId());

        return db.insert(MySQLiteHelper.TABLE_SEARCH_HISTORY, null, contentValues) > 0;
    }

    @Override
    public boolean deleteSearchHistory(SearchHistory searchHistory) {
        return db.delete(MySQLiteHelper.TABLE_SEARCH_HISTORY, MySQLiteHelper.COLUMN_SEARCH_ID + " = ? ", new String[]{String.valueOf(searchHistory.getId())}) > 0;
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
        searchHistory.setId(c.getInt(id));
        searchHistory.setCurrentUser(userDataSource.getUserById(c.getString(user1)));
        searchHistory.setSearchingUser(userDataSource.getUserById(c.getString(user2)));

        return searchHistory;
    }

}
