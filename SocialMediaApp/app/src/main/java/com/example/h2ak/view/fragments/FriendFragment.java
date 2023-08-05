package com.example.h2ak.view.fragments;

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
import com.example.h2ak.adapter.FriendAdapter;
import com.example.h2ak.contract.FriendFragmentContract;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.FriendFragmentPresenter;

import java.util.List;

public class FriendFragment extends Fragment implements FriendFragmentContract.View{
    Toolbar toolbar;
    FriendAdapter friendAdapter;
    RecyclerView recyclerView;
    TextView textViewFriendCount;
    private View view;
    private FriendFragmentContract.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        toolbar = view.findViewById(R.id.toolBar);
        toolbar.setTitle("Friends");

        textViewFriendCount = view.findViewById(R.id.textViewFriendCount);

        recyclerView = view.findViewById(R.id.recyclerViewFriendList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        friendAdapter = new FriendAdapter(this.getContext());
        recyclerView.setAdapter(friendAdapter);

        setPresenter(new FriendFragmentPresenter(this, this.getContext()));
        getPresenter().getFriendList();

        return view;
    }

    @Override
    public void onFriendListRecieved(List<User> userList) {
        if (userList != null) {
            textViewFriendCount.setText(String.format("Friend list (%d)", userList.size()));
            friendAdapter.setUserList(userList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().getFriendList();
    }

    public FriendFragmentContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(FriendFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }
}