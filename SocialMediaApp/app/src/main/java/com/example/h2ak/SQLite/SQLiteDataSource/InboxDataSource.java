package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Inbox;

import java.util.List;

public interface InboxDataSource {
    boolean createInbox(Inbox inbox);
    boolean createInboxOnFirebaseChange(Inbox inbox);
    boolean deleteInboxOnFirebaseChange(Inbox inbox);
    List<Inbox> getAllInboxesByUserId(String id);
    Inbox findInboxFriendRequest(String userId1, String userId2);
    Inbox findInboxOnFirebaseChange(Inbox inbox);
    boolean deleteInbox(Inbox inbox);
    boolean updateInbox(Inbox inbox);
    boolean updateInboxOnFirebaseChange(Inbox inbox);
}
