package com.example.h2ak.presenter;

import android.content.Context;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.SearchHistoryDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SearchHistoryDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.contract.SearchFragmentContract;
import com.example.h2ak.model.SearchHistory;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchFragmentPresenter implements SearchFragmentContract.Presenter {

    Context context;
    private SearchFragmentContract.View view;
    private UserDataSource userDataSource;
    private SearchHistoryDataSource searchHistoryDataSource;
    private FirebaseAuth firebaseAuth;

    public SearchFragmentPresenter(SearchFragmentContract.View view, Context context) {
        this.context = context;
        this.view = view;
        userDataSource = UserDataSourceImpl.getInstance(context);
        searchHistoryDataSource = SearchHistoryDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getAllUsers(Map<String, String> params) {
        params.put("id", firebaseAuth.getCurrentUser().getUid());
        Log.d("DiscoverFragmentPresenter", userDataSource.getAllUsers(params).size() + "");
        view.onUserListRecieved(userDataSource.getAllUsers(params).stream().filter(user -> user.isActive()).collect(Collectors.toList()));
    }

    @Override
    public void getALLSearchHistory() {
        User currentUser = userDataSource.getUserById(firebaseAuth.getCurrentUser().getUid());
        List<User> userList = new LinkedList<>();
        Set<SearchHistory> searchHistorySet = searchHistoryDataSource.getAllSearchHistoryByUser(currentUser);
        if (!searchHistorySet.isEmpty()) {
            searchHistorySet.forEach(searchHistory -> {
                userList.add(searchHistory.getSearchingUser());
            });
            Log.d("getALLSearchHistory", "searchHistorySet is : " + searchHistorySet.size());
            view.onSearchHistoryRecieved(userList);
        } else {
            Log.d("getALLSearchHistory", "searchHistorySet is empty: " + searchHistorySet.size());
        }
    }
}
