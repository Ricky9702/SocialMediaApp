package com.example.h2ak.contract;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;

import java.util.List;

public interface UserProfileAcitivtyContract {
    interface View {
        void onStatusReceived(String status);
        void onUserRecieved(User user);
        void onSendMessage(String message);
        void onSendFriendRequestFailed(String message);
        void showProgressBar(boolean flag);

        void onListPostRecieved(List<Post> postList);
    }
    interface Presenter{
        void getUserById(String id);
        void createOrUpdateFriendRequest(User user2);
        void getAllPostByUserId(String id, String privacy1, String privacy2);
    }
}
