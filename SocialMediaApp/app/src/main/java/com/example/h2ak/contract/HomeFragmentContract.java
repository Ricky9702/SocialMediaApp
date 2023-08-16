package com.example.h2ak.contract;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;

import java.util.List;

public interface HomeFragmentContract {

    interface View {
        void changeProfileAvatar(User user);

        void onListPostReceived(List<Post> postList);
    }

    interface Presenter {
        void loadCurrentUser();
        void loadFriendsPost();
    }
}
