package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.PostReaction;

public interface FirebasePostReactionDataSource {
    boolean create(PostReaction postReaction);
    boolean delete(PostReaction postReaction);
}
