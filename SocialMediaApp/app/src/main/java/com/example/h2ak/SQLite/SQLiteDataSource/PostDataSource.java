package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;

import java.util.Set;

public interface PostDataSource{
    boolean createPost(Post post);
    boolean deletePost(Post post);
    boolean updatePost(Post post);
    Post findPost(String id);
    Post getNewestPost(User user);
    Set<Post> getAllPostByUserId(String id);
    Set<Post> getAllPostByUserIdWithPrivacy(String id, String privacy1, String privacy2);
    Set<Post> getRandomPost();
}
