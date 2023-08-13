package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostReaction;
import com.example.h2ak.model.User;

import java.util.Set;

public interface PostReactionDataSource {

    boolean create(PostReaction postReaction);
    boolean delete(PostReaction postReaction);
    Set<PostReaction> getAllReactionByPost(Post post);
    PostReaction find(Post post, String currentUserId);
}
