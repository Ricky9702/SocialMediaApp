package com.example.h2ak.contract;

import com.example.h2ak.model.User;

import java.util.List;

public interface FriendFragmentContract {
    interface View {
        void onFriendListRecieved(List<User> userList);
    }
    interface Presenter {
        void getFriendList();
    }
}
