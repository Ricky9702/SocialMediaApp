package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Inbox;

import java.util.List;

public interface InboxDataSource {
    boolean createInbox(Inbox inbox);
    List<Inbox> getAllInboxesByUserId(String id);
    Inbox findInboxFriendRequest(String userId1, String userId2);
    Inbox findInbox(Inbox inbox);
    boolean deleteInbox(Inbox inbox);
    boolean updateInbox(Inbox inbox);
}
