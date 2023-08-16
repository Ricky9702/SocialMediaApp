package com.example.h2ak.view.activities;

import com.example.h2ak.R;
import com.example.h2ak.contract.BaseMenuActivityContract;
import com.example.h2ak.databinding.ActivityBaseMenuBinding;
import com.example.h2ak.presenter.BaseMenuActivityPresenter;
import com.example.h2ak.view.fragments.DiscoverFragment;
import com.example.h2ak.view.fragments.HomeFragment;
import com.example.h2ak.view.fragments.FriendFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.h2ak.view.fragments.InboxFragment;

import java.util.HashMap;
import java.util.Map;

public class BaseMenuActivity extends AppCompatActivity implements BaseMenuActivityContract.View {
    TextView textViewInboxCounts;

    private ActivityBaseMenuBinding binding;
    private Map<Integer, Map<String, Integer>> buttonInfoMap = new HashMap<>();
    private int previousButtonId = -1;
    private BaseMenuActivityContract.Presenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    private void init() {

        binding = ActivityBaseMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get views
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnDiscover = findViewById(R.id.btnDiscover);
        ImageButton btnCreate = findViewById(R.id.btnCreate);
        ImageButton btnFriend = findViewById(R.id.btnFriend);
        ImageButton btnInbox = findViewById(R.id.btnInbox);


        View linearLayoutHome = findViewById(R.id.linearLayoutHome);
        View linearLayoutDiscover = findViewById(R.id.linearLayoutDiscover);
        View linearLayoutCreate = findViewById(R.id.linearLayoutCreate);
        View linearLayoutFriend = findViewById(R.id.linearLayoutFriend);
        View linearLayoutInbox = findViewById(R.id.linearLayoutInbox);

        // textView friend count
        textViewInboxCounts = findViewById(R.id.textViewInboxCounts);

        //Set bottom nav button fields
        addButtonInfo(R.id.btnHome, R.drawable.baseline_home_24, R.drawable.baseline_home_24_active, R.id.textViewHome);
        addButtonInfo(R.id.btnDiscover, R.drawable.baseline_search_24, R.drawable.baseline_search_active, R.id.textViewDiscover);
        addButtonInfo(R.id.btnFriend, R.drawable.baseline_group_24, R.drawable.baseline_group_24_active, R.id.textViewFriend);
        addButtonInfo(R.id.btnInbox, R.drawable.baseline_notifications_none_24, R.drawable.baseline_notifications_none_24_active, R.id.textViewInbox);

        linearLayoutHome.setOnClickListener(view -> {
            replaceFragment(new HomeFragment());
            setActiveButton(R.id.btnHome);
        });

        linearLayoutDiscover.setOnClickListener(view -> {
            replaceFragment(new DiscoverFragment());
            setActiveButton(R.id.btnDiscover);
        });

        linearLayoutCreate.setOnClickListener(view -> {
            startActivity(new Intent(this, CreatePostActivity.class));
        });

        linearLayoutFriend.setOnClickListener(view -> {
            replaceFragment(new FriendFragment());
            setActiveButton(R.id.btnFriend);
        });

        linearLayoutInbox.setOnClickListener(view -> {
            replaceFragment(new InboxFragment());
            setActiveButton(R.id.btnInbox);
            textViewInboxCounts.setVisibility(View.GONE);
        });

        linearLayoutHome.performClick();

        setPresenter(new BaseMenuActivityPresenter(this, this));
        getPresenter().loadingListInboxUnRead();

    }

    private void addButtonInfo(int buttonId, int defaultImageResId, int activeImageResId, int textViewId) {
        Map<String, Integer> buttonInfo = new HashMap<>();
        buttonInfo.put("defaultImage", defaultImageResId);
        buttonInfo.put("activeImage", activeImageResId);
        buttonInfo.put("textViewId", textViewId);
        buttonInfoMap.put(buttonId, buttonInfo);
    }


    private void setActiveButton(int activeButtonId) {
        try {
            //set default previous button
            setDefaultButton(previousButtonId);
            //get views
            ImageButton activeButton = findViewById(activeButtonId);
            Map<String, Integer> activeButtonInfo = buttonInfoMap.get(activeButtonId);
            TextView textViewActive = findViewById(activeButtonInfo.get("textViewId"));
            //set views fields
            activeButton.setImageDrawable(getDrawable(activeButtonInfo.get("activeImage")));
            textViewActive.setTextColor(getColor(R.color.active_icon));
            //assign current button to previous button
            previousButtonId = activeButtonId;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void setDefaultButton(int previousButtonId) {
        if (previousButtonId != -1) {
            try {
                //get views
                ImageButton previousButton = findViewById(previousButtonId);
                Map<String, Integer> previousButtonInfo = buttonInfoMap.get(previousButtonId);
                TextView textViewPrevious = findViewById(previousButtonInfo.get("textViewId"));
                //set views fields
                previousButton.setImageDrawable(getDrawable(previousButtonInfo.get("defaultImage")));
                textViewPrevious.setTextColor(getColor(R.color.not_active_icon));
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onCountListUnReadInbox(long count) {
        if (count > 0) {
            textViewInboxCounts.setText(String.valueOf(count));
            textViewInboxCounts.setVisibility(View.VISIBLE);
        }
        else textViewInboxCounts.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        getPresenter().loadingListInboxUnRead();
        super.onStart();
    }

    public BaseMenuActivityContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(BaseMenuActivityContract.Presenter presenter) {
        this.presenter = presenter;
    }
}