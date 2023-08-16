package com.example.h2ak.contract;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;

import java.util.List;

public interface ProfileActivityContract {

    interface View {
        void loadUserInformation(User user);

        void onPostListRecieved(List<Post> postList);

        void onSetFriendsReceived(List<User> collect);
    }

    interface Presenter {
        void getUser();
        void getAllPost();
        void getFriends();
    }
}
