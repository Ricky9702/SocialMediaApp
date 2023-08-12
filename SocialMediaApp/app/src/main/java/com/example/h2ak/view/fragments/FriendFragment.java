package com.example.h2ak.view.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.h2ak.R;
import com.example.h2ak.adapter.FriendAdapter;
import com.example.h2ak.contract.FriendFragmentContract;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.FriendFragmentPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendFragment extends Fragment implements FriendFragmentContract.View{
    Toolbar toolbar;
    EditText editTextSearch;
    private FriendAdapter friendAdapter;
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
        editTextSearch = view.findViewById(R.id.editTextSearch);

        recyclerView = view.findViewById(R.id.recyclerViewFriendList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        setFriendAdapter(new FriendAdapter(this.getContext()));
        recyclerView.setAdapter(getFriendAdapter());

        setPresenter(new FriendFragmentPresenter(this, this.getContext()));
        getPresenter().getFriendList();

        Map<String, String> params = new HashMap<>();
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String kw = editTextSearch.getText().toString().trim();
                if (kw != null && !kw.isEmpty()) {
                    params.put("kw", kw);
                } else {
                    params.remove("kw");
                }
                friendAdapter.setFilterForList(params);
            }
        });

        return view;
    }

    @Override
    public void onFriendListRecieved(List<User> userList) {
        if (userList != null) {
            textViewFriendCount.setText(String.format("Friend list (%d)", userList.size()));
            getFriendAdapter().setUserList(userList);
            getFriendAdapter().setOriginalUserList(userList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public FriendFragmentContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(FriendFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public FriendAdapter getFriendAdapter() {
        return friendAdapter;
    }

    public void setFriendAdapter(FriendAdapter friendAdapter) {
        this.friendAdapter = friendAdapter;
    }
}