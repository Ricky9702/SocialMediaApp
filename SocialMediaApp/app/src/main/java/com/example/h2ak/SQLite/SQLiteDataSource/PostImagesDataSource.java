package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;

import java.util.Set;

public interface PostImagesDataSource {
    boolean createPostImages(PostImages postImages);
    boolean deletePostImages(PostImages postImages);
    PostImages getPostImagesById(String id);
    Set<PostImages> getAllPostImagesByPost(Post post);
    PostImages getFirstPostImages(Post post);
}
