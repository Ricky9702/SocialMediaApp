package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.Post;

public interface FirebasePostDataSource {
    boolean create(Post post);
    boolean update(Post post);
    boolean delete(Post post);
}
