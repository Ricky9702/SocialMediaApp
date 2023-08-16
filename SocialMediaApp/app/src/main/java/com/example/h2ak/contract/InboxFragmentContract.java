package com.example.h2ak.contract;

import com.example.h2ak.model.Inbox;

import java.util.List;
import java.util.Map;

public interface InboxFragmentContract {

    interface View{
        void onListInboxesRecieved(List<Inbox> inboxList);

        void onReadInbox(boolean flag);
    }

    interface Presenter{
        void getListInboxes(Map<String, String> params);
    }
}
