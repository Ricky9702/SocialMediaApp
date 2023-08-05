package com.example.h2ak.presenter;

import android.content.Context;
import android.util.Log;

import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.contract.InboxFragmentContract;
import com.example.h2ak.model.Inbox;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

public class InboxFragmentPresenter implements InboxFragmentContract.Presenter {

    private InboxFragmentContract.View view;
    private Context context;
    private InboxDataSource inboxDataSource;
    private FirebaseAuth firebaseAuth;

    public InboxFragmentPresenter(InboxFragmentContract.View view, Context context) {
        this.context = context;
        this.view = view;
        inboxDataSource = InboxDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getListInboxes() {
        List<Inbox> inboxList =
                inboxDataSource.getAllInboxesByUserId(firebaseAuth.getCurrentUser().getUid());
        Log.d("InboxPresenter", inboxList.size() + "");

        Collections.sort(inboxList, (inbox1, inbox2) -> {
            // Sorting in descending order (latest items first)
            return inbox2.getId().compareTo(inbox1.getId());
        });

        view.onListInboxesRecieved(inboxList);
    }
}
