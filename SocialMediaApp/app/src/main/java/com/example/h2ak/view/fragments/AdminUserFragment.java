package com.example.h2ak.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.adapter.AdminUserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminUserFragment extends Fragment {
    RecyclerView recyclerViewUsers;
    FloatingActionButton floatingButton;
    AdminUserAdapter adapter;
    UserDataSource userDataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_user, container, false);
        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new AdminUserAdapter(this.getContext());
        recyclerViewUsers.setAdapter(adapter);
        floatingButton = view.findViewById(R.id.floatingButton);

        userDataSource = UserDataSourceImpl.getInstance(this.getContext());

        adapter.setUserList(userDataSource.getAllUsers(null));
        floatingButton.setOnClickListener(v -> {
            adapter.createUser();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}