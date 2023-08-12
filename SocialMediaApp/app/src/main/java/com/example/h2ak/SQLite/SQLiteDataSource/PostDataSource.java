package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Post;

import java.util.Set;

public interface PostDataSource{
    boolean createPost(Post post);
    boolean deletePost(Post post);
    boolean updatePost(Post post);
    Post findPost(String id);
    Set<Post> getAllPostByUserId(String id);
    Set<Post> getAllPostByUserIdWithPrivacy(String id, String privacy1, String privacy2);
}
