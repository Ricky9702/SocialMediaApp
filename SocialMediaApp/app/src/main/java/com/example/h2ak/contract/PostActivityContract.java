package com.example.h2ak.contract;

import com.example.h2ak.model.Post;

import java.util.List;

public interface PostActivityContract {
    interface View {
        void onListPostRecieved(List<Post> postList);
    }
    interface Presenter {
        void getAllPostByUserIdWithPrivacy(String id, String privacy1, String privacy2);
        void getPostByListId(List<String> list);
    }
}
