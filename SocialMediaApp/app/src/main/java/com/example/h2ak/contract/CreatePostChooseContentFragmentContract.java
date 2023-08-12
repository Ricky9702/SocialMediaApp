package com.example.h2ak.contract;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;

import java.util.List;

public interface CreatePostChooseContentFragmentContract {

    interface View {

        void showProgressbar(boolean b);

        void showMessage(String s);
    }

    interface Presenter {
        void createPost(Post post, List<PostImages> postImagesList);
    }
}
