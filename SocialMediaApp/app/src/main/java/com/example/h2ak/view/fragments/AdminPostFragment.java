package com.example.h2ak.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.adapter.AdminPostAdapter;

import java.util.stream.Collectors;


public class AdminPostFragment extends Fragment {
    AdminPostAdapter adapter;
    RecyclerView recyclerViewPosts;
    private PostDataSource postDataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_post, container, false);
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);

        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        postDataSource = PostDataSourceImpl.getInstance(getContext());

        adapter = new AdminPostAdapter(getContext());

        adapter.setPostList(postDataSource.getAllPost().stream().collect(Collectors.toList()));

        recyclerViewPosts.setAdapter(adapter);

        return view;
    }
}