package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.PostCommentReaction;

public interface FirebasePostCommentReactionDataSource {
    boolean create(PostCommentReaction comment);
    boolean delete(PostCommentReaction comment);
    boolean update(PostCommentReaction comment);
}
