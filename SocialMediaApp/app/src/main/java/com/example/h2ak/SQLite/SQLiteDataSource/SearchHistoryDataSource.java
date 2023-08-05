package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.SearchHistory;
import com.example.h2ak.model.User;

import java.util.Set;

public interface SearchHistoryDataSource {
    boolean createSearchHistory(SearchHistory searchHistory);
    boolean deleteSearchHistory(SearchHistory searchHistory);
    Set<SearchHistory> getAllSearchHistoryByUser(User user);
    SearchHistory findSearchHistory(User currentUser, User searchingUser);
}
