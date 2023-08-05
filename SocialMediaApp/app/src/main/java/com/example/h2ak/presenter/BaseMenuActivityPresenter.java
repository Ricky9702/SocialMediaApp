package com.example.h2ak.presenter;

import android.content.Context;
import android.util.Log;

import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.contract.BaseMenuActivityContract;
import com.example.h2ak.model.Inbox;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class BaseMenuActivityPresenter implements BaseMenuActivityContract.Presenter {

    private Context context;
    private BaseMenuActivityContract.View view;
    private InboxDataSource inboxDataSource;
    private FirebaseAuth firebaseAuth;

    public BaseMenuActivityPresenter(Context context, BaseMenuActivityContract.View view) {
        this.context = context;
        this.view = view;
        inboxDataSource = InboxDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void loadingListInboxUnRead() {
        List<Inbox> inboxList = inboxDataSource.getAllInboxesByUserId(firebaseAuth.getCurrentUser().getUid());
        long count = inboxList.stream().filter(inbox -> !inbox.isRead()).count();
        Log.d("loadingListInboxUnRead", "loadingListInboxUnRead: " + count);
        view.onCountListUnReadInbox(count);
    }
}
