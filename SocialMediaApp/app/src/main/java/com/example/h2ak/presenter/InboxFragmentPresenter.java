package com.example.h2ak.presenter;

import android.content.Context;
import android.util.Log;

import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxPostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxPostDataSourceImpl;
import com.example.h2ak.contract.InboxFragmentContract;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InboxFragmentPresenter implements InboxFragmentContract.Presenter {

    private InboxFragmentContract.View view;
    private Context context;
    private InboxPostDataSource inboxPostDataSource;
    private InboxDataSource inboxDataSource;
    private FirebaseAuth firebaseAuth;

    public InboxFragmentPresenter(InboxFragmentContract.View view, Context context) {
        this.context = context;
        this.view = view;
        inboxPostDataSource = InboxPostDataSourceImpl.getInstance(context);
        inboxDataSource = InboxDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getListInboxes(Map<String, String> params) {
        List<Inbox> inboxList =
                inboxDataSource.getAllInboxesByUserId(firebaseAuth.getCurrentUser().getUid());
        Log.d("InboxPresenter", inboxList.size() + "");

        Collections.sort(inboxList, (inbox1, inbox2) -> {
            Date date1 = parseDateFromString(inbox1.getCreatedDate());
            Date date2 = parseDateFromString(inbox2.getCreatedDate());

            // Sorting in descending order (latest items first)
            return date2.compareTo(date1);
        });

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                if (kw.equals("ALL")) {
                    view.onListInboxesRecieved(inboxList);
                }else if (kw.equals("UN_READ")) {
                    view.onListInboxesRecieved(inboxList.stream()
                            .filter(inbox -> !inbox.isRead()).collect(Collectors.toList()));
                } else {
                    view.onListInboxesRecieved(inboxList.stream()
                            .filter(inbox -> inbox.getType().equals(kw)).collect(Collectors.toList()));
                }
            }
        } else {
            view.onListInboxesRecieved(inboxList);
        }

    }

    @Override
    public void deleteInbox() {
        List<Inbox> inboxList =
                inboxDataSource.getAllInboxesByUserId(firebaseAuth.getCurrentUser().getUid());

        if (!inboxList.isEmpty()) {
            inboxList.forEach(inbox -> {
                inboxPostDataSource.delete(inbox.getId());
                inboxDataSource.deleteInbox(inbox);
            });
        }
        view.onListInboxesRecieved(new ArrayList<>());
    }

    private Date parseDateFromString(String dateString) {
        try {
            return TextInputLayoutUtils.simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Return a default date in case of parsing error
        }
    }
}
