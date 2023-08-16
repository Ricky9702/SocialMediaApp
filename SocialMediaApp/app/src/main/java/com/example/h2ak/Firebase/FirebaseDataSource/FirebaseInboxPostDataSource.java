package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.InboxPost;

public interface FirebaseInboxPostDataSource {
    boolean createInboxPost(InboxPost inboxPost);

    boolean delete(String id);
}
