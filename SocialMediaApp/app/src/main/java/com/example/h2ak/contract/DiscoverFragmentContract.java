package com.example.h2ak.contract;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;

import java.util.List;

public class DiscoverFragmentContract {
    public interface View {

        void onRandomPostListReceived(List<Post> postList);

        void onSuggestUserListReceived(List<User> collect);
    }
    public interface Presenter{
        void getRandomPostList();
        void getRandomUser();
    }
}
