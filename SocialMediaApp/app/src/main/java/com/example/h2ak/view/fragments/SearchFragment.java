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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.h2ak.R;
import com.example.h2ak.adapter.ProfileItemAdapter;
import com.example.h2ak.adapter.SearchHistoryAdapter;
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
    RecyclerView recyclerViewSearchProfile, recyclerViewSearchHistory;
    TextView textViewUserNotFound, textViewRecent;
    ImageButton btnBack;
    ProfileItemAdapter adapter;
    SearchHistoryAdapter searchHistoryAdapter;
    RelativeLayout relativeLayoutSearchHistory;
    private SearchFragmentContract.Presenter presenter;
    private Map<String, String> params;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // init view
        textViewRecent = view.findViewById(R.id.textViewRecent);
        textViewUserNotFound = view.findViewById(R.id.textViewUserNotFound);
        textLayoutSearch = view.findViewById(R.id.textLayoutSearch);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerViewSearchProfile = view.findViewById(R.id.recyclerViewSearchProfile);
        recyclerViewSearchHistory = view.findViewById(R.id.recyclerViewSearchHistory);
        relativeLayoutSearchHistory = view.findViewById(R.id.relativeLayoutSearchHistory);
        btnBack = view.findViewById(R.id.btnBack);

        adapter = new ProfileItemAdapter(this.getContext());
        recyclerViewSearchProfile.setAdapter(adapter);
        recyclerViewSearchProfile.setLayoutManager(new LinearLayoutManager(this.getContext()));

        searchHistoryAdapter = new SearchHistoryAdapter(this.getContext(), this);
        recyclerViewSearchHistory.setAdapter(searchHistoryAdapter);
        recyclerViewSearchHistory.setLayoutManager(new LinearLayoutManager(this.getContext()));



        presenter = new SearchFragmentPresenter(this, this.getContext());

        textLayoutSearch.setOnClickListener(view1 -> {
            recyclerViewSearchProfile.setVisibility(View.VISIBLE);
            relativeLayoutSearchHistory.setVisibility(View.GONE);
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

        presenter.getALLSearchHistory();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onUserListRecieved(List<User> userList) {
        if (!editTextSearch.getText().toString().trim().isEmpty()) {
            if (userList.size() == 0) {
                relativeLayoutSearchHistory.setVisibility(View.GONE);
                textViewUserNotFound.setVisibility(View.VISIBLE);
            } else {
                textViewUserNotFound.setVisibility(View.GONE);
                relativeLayoutSearchHistory.setVisibility(View.GONE);
            }
        } else {
            presenter.getALLSearchHistory();
            relativeLayoutSearchHistory.setVisibility(View.VISIBLE);
            textViewUserNotFound.setVisibility(View.GONE);
        }
        adapter.setUserList(userList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchHistoryRecieved(List<User> userList) {
        if (userList.size() > 0) {
            searchHistoryAdapter.setUserList(userList);
            searchHistoryAdapter.notifyDataSetChanged();
            textViewRecent.setText("Recent");
        } else {
            textViewRecent.setText("No recent searches");
        }
    }

    @Override
    public void onDeleteAllSearchHistory() {
        this.textViewRecent.setText("No recent searches");
    }
}