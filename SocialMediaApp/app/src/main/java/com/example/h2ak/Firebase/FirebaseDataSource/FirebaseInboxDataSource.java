package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.Inbox;

public interface FirebaseInboxDataSource {
    void createInbox(Inbox inbox);
    void deleteInbox(Inbox inbox);
    void updateInbox(Inbox inbox);
}
