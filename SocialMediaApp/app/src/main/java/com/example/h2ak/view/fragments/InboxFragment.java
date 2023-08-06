package com.example.h2ak.view.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.h2ak.R;
import com.example.h2ak.adapter.InboxAdapter;
import com.example.h2ak.contract.InboxFragmentContract;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.presenter.InboxFragmentPresenter;

import java.util.List;


public class InboxFragment extends Fragment implements InboxFragmentContract.View {

    Toolbar toolBar;
    TextView textViewInboxCount, textViewInboxUnRead;
    RecyclerView recyclerView;
    InboxAdapter adapter;
    private InboxFragmentContract.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_inbox, container, false);


        toolBar = view.findViewById(R.id.toolBar);
        toolBar.setTitle("Notifications");
        toolBar.setTitleTextColor(Color.BLACK);

        textViewInboxCount = view.findViewById(R.id.textViewInboxCount);
        textViewInboxUnRead = view.findViewById(R.id.textViewInboxUnRead);

        recyclerView = view.findViewById(R.id.recyclerViewInbox);
        adapter = new InboxAdapter(this.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        presenter = new InboxFragmentPresenter(this, this.getContext());
        presenter.getListInboxes();

        return view;
    }

    @Override
    public void onListInboxesRecieved(List<Inbox> inboxList) {
        if (inboxList != null) {
            adapter.setInboxList(inboxList);
            textViewInboxCount.setText(String.format("Inbox List (%d)", inboxList.size()));
            textViewInboxUnRead.setText(String.format("Unread (%d)", inboxList.stream().filter(inbox -> !inbox.isRead()).count()));
        }
    }

    @Override
    public void onReadInbox(boolean flag) {
        if (flag) {
            getPresenter().getListInboxes();
        }
    }

    public InboxFragmentContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(InboxFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }
}