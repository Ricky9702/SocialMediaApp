package com.example.h2ak.view.fragments;

import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.h2ak.R;
import com.example.h2ak.adapter.DisplayFriendAdapter;
import com.example.h2ak.adapter.ProfilePostDisplayAdapter;
import com.example.h2ak.contract.DiscoverFragmentContract;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.DiscoverFragmentPresenter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DiscoverFragment extends Fragment implements DiscoverFragmentContract.View {
    RelativeLayout relativeLayoutFriends;
    TextInputEditText editTextSearch;
    RecyclerView recyclerViewPostImage, recyclerViewFriends;
    ProfilePostDisplayAdapter profileDisplayPostAdapter;
    DisplayFriendAdapter friendAdapter;
    private DiscoverFragmentContract.Presenter presenter;
    private String TAG = "DiscoverFragment";
    private Map<String, String> params;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_discover, container, false);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        relativeLayoutFriends = view.findViewById(R.id.relativeLayoutFriends);

        editTextSearch.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new SearchFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        recyclerViewPostImage = view.findViewById(R.id.recyclerViewPostImage);

        profileDisplayPostAdapter = new ProfilePostDisplayAdapter(this.getContext());

        profileDisplayPostAdapter.setPostActivityLauncher(registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == -1) {
                        boolean isChange = result.getData() != null && result.getData().getBooleanExtra("UPDATED", false);
                        if (isChange) {
                            presenter.getRandomPostList();
                        }
                    }
                }));

        recyclerViewPostImage.setAdapter(profileDisplayPostAdapter);
        recyclerViewPostImage.setLayoutManager(new GridLayoutManager(this.getContext(), 3));

        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        friendAdapter = new DisplayFriendAdapter(this.getContext());
        recyclerViewFriends.setAdapter(friendAdapter);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        params = new HashMap<>();
        presenter = new DiscoverFragmentPresenter(this.getContext() ,this);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.getRandomPostList();
        presenter.getRandomUser();
    }

    @Override
    public void onRandomPostListReceived(List<Post> postList) {
        if (postList != null && !postList.isEmpty()) {
            Log.d(TAG, "onRandomPostListReceived: " + postList.size());
            profileDisplayPostAdapter.setPostList(postList);
            params.put("random", "random");
            profileDisplayPostAdapter.setParams(params);
        }
    }

    @Override
    public void onSuggestUserListReceived(List<User> suggestUser) {
        if (suggestUser != null && !suggestUser.isEmpty()) {
            Collections.shuffle(suggestUser);
            friendAdapter.setFriends(suggestUser);
            relativeLayoutFriends.setVisibility(View.VISIBLE);
        } else {
            relativeLayoutFriends.setVisibility(View.GONE);
        }
    }
}