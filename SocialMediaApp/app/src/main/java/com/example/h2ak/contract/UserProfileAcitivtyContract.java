package com.example.h2ak.contract;

import com.example.h2ak.model.User;

public interface UserProfileAcitivtyContract {
    interface View {
        void onStatusRecieved(String status);
        void onUserRecieved(User user);
        void onSendMessage(String message);
        void onSendFriendRequestFailed(String message);
        void showProgressBar(boolean flag);
    }
    interface Presenter{
        void getUserById(String id);
        void createOrUpdateFriendRequest(User user2);
    }
}
