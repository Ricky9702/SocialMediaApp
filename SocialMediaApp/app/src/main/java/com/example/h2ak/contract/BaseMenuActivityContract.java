package com.example.h2ak.contract;

import com.example.h2ak.model.Inbox;

import java.util.List;

public interface BaseMenuActivityContract {
    interface View {
        void onCountListUnReadInbox(long count);
    }

    interface Presenter{
        void loadingListInboxUnRead();
    }
}
