package com.example.h2ak.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.h2ak.R;
import com.example.h2ak.adapter.ProfileItemAdapter;
import com.example.h2ak.contract.SearchFragmentContract;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.SearchFragmentPresenter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements SearchFragmentContract.View {
    TextInputLayout textLayoutSearch;
    TextInputEditText editTextSearch;
    RecyclerView recyclerViewSearchProfile;
    TextView textViewUserNotFound;
    ImageButton btnBack;
    ProfileItemAdapter adapter;
    private SearchFragmentContract.Presenter presenter;
    private Map<String, String> params;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // init view
        textViewUserNotFound = view.findViewById(R.id.textViewUserNotFound);
        textLayoutSearch = view.findViewById(R.id.textLayoutSearch);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerViewSearchProfile = view.findViewById(R.id.recyclerViewSearchProfile);
        btnBack = view.findViewById(R.id.btnBack);

        adapter = new ProfileItemAdapter(this.getContext());
        recyclerViewSearchProfile.setAdapter(adapter);
        recyclerViewSearchProfile.setLayoutManager(new LinearLayoutManager(this.getContext()));

        presenter = new SearchFragmentPresenter(this, this.getContext());

        textLayoutSearch.setOnClickListener(view1 -> {
            recyclerViewSearchProfile.setVisibility(View.VISIBLE);
        });

        btnBack.setOnClickListener(view1 -> {
            getParentFragmentManager().popBackStack();
        });


        params = new HashMap<>();

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
                params.put("kw", kw);
                presenter.getAllUsers(params);
            }
        });

        editTextSearch.requestFocus();

        return view;
    }

    @Override
    public void onUserListRecieved(List<User> userList) {
        if (!editTextSearch.getText().toString().trim().isEmpty()) {
            if (userList.size() == 0) {
                textViewUserNotFound.setVisibility(View.VISIBLE);
            } else {
                textViewUserNotFound.setVisibility(View.GONE);
            }
        } else {
            textViewUserNotFound.setVisibility(View.GONE);
        }
        adapter.setUserList(userList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchHistoryRecieved(List<User> userList) {

    }
}