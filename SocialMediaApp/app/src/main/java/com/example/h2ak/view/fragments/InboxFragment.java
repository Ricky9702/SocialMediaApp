package com.example.h2ak.view.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.h2ak.R;
import com.example.h2ak.adapter.InboxAdapter;
import com.example.h2ak.adapter.SpinnerGenderAdapter;
import com.example.h2ak.contract.InboxFragmentContract;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.presenter.InboxFragmentPresenter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InboxFragment extends Fragment implements InboxFragmentContract.View {

    Toolbar toolBar;
    TextView textViewInboxCount, textViewInboxUnRead, textViewFilter;
    RecyclerView recyclerView;
    InboxAdapter adapter;
    Spinner spinnerFilter;
    private InboxFragmentContract.Presenter presenter;
    LinearLayout linearLayoutFilter;
    private Map<String, String> params;
    SpinnerGenderAdapter spinnerGenderAdapter;
    String[] items = new String[]{"ALL", "POST_MESSAGE", "FRIEND_REQUEST", "MESSAGE"};
    TextInputEditText editTextSearch;

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

        spinnerFilter = view.findViewById(R.id.spinnerFilter);

        params = new HashMap<>();

        spinnerGenderAdapter = new SpinnerGenderAdapter(this.getContext(), items);
        spinnerGenderAdapter.setCount(items.length);
        spinnerFilter.setAdapter(spinnerGenderAdapter);
        spinnerFilter.setSelection(0);
        params.put("kw", items[0]);


        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
                params.put("kw", items[i]);
                presenter.getListInboxes(params);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        linearLayoutFilter = view.findViewById(R.id.linearLayoutFilter);
        linearLayoutFilter.setOnClickListener(view1 -> {
            spinnerFilter.performClick();
        });


        editTextSearch = view.findViewById(R.id.editTextSearch);

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
                if (kw != null) {
                    adapter.setKw(kw);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


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
            getPresenter().getListInboxes(params);
        }
    }

    public InboxFragmentContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(InboxFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}