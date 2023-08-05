package com.example.h2ak.contract;

import com.example.h2ak.model.User;

import java.util.List;
import java.util.Map;

public interface SearchFragmentContract {

    interface View {
        void onUserListRecieved(List<User> userList);
        void onSearchHistoryRecieved(List<User> userList);
    }

    interface Presenter {
        void getAllUsers(Map<String, String> params);
        void getALLSearchHistory();
    }

}
