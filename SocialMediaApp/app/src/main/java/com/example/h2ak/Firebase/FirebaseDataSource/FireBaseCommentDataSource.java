package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.PostComment;

public interface FireBaseCommentDataSource {
    boolean create(PostComment postComment);
    boolean delete(PostComment postComment);
    boolean update(PostComment postComment);
}
