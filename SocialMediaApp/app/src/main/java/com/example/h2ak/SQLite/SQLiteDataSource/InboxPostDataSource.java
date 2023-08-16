package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Inbox;
import com.example.h2ak.model.InboxPost;

public interface InboxPostDataSource {
    boolean create(InboxPost inboxPost);
    boolean delete(String id);
    InboxPost find(String id);
}
