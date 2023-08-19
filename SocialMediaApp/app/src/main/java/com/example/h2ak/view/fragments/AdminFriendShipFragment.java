package com.example.h2ak.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.adapter.AdminFriendShipAdapter;
import com.example.h2ak.adapter.AdminUserAdapter;
import com.example.h2ak.model.FriendShip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminFriendShipFragment extends Fragment {
    RecyclerView recyclerViewFriendShip;
    FloatingActionButton floatingButton;
    AdminFriendShipAdapter adapter;
    FriendShipDataSource friendShipDataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_friend_ship, container, false);
        recyclerViewFriendShip = view.findViewById(R.id.recyclerViewFriendShip);
        recyclerViewFriendShip.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new AdminFriendShipAdapter(this.getContext());
        recyclerViewFriendShip.setAdapter(adapter);
        floatingButton = view.findViewById(R.id.floatingButton);

        friendShipDataSource = FriendShipDataSourceImpl.getInstance(view.getContext());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.setFriendShipList(friendShipDataSource.getAllFriendShip());

        floatingButton.setOnClickListener(view -> {
            adapter.createFriendShip();
        });
    }
}