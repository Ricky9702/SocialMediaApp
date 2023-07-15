package com.example.h2ak.view.activities;

import com.example.h2ak.R;
import com.example.h2ak.controllers.BaseMenuController;
import com.example.h2ak.controllers.ProfileImageChangeListener;
import com.example.h2ak.databinding.ActivityBaseMenuBinding;
import com.example.h2ak.view.fragments.CreateFragment;
import com.example.h2ak.view.fragments.DiscoverFragment;
import com.example.h2ak.view.fragments.HomeFragment;
import com.example.h2ak.view.fragments.FriendFragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.h2ak.view.fragments.InboxFragment;
import com.example.h2ak.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class BaseMenuActivity extends AppCompatActivity implements ProfileImageChangeListener {
    private ActivityBaseMenuBinding binding;
    private FirebaseAuth firebaseAuth;
    private BaseMenuController baseMenuController;

    private Map<Integer, Map<String, Integer>> buttonInfoMap = new HashMap<>();
    private int previousButtonId = -1;

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

        baseMenuController = new BaseMenuController(BaseMenuActivity.this);
        baseMenuController.changeProfileImage();


        addButtonInfo(R.id.btnHome, R.drawable.baseline_home_24, R.drawable.baseline_home_24_active, R.id.textViewHome);
        addButtonInfo(R.id.btnDiscover, R.drawable.baseline_search_24, R.drawable.baseline_search_active, R.id.textViewDiscover);
        addButtonInfo(R.id.btnFriend, R.drawable.baseline_group_24, R.drawable.baseline_group_24_active, R.id.textViewFriend);
        addButtonInfo(R.id.btnInbox, R.drawable.baseline_notifications_none_24, R.drawable.baseline_notifications_none_24_active, R.id.textViewInbox);

        btnHome.setOnClickListener(view -> {
            replaceFragment(new HomeFragment());
            setActiveButton(R.id.btnHome);
        });

        btnDiscover.setOnClickListener(view -> {
            replaceFragment(new DiscoverFragment());
            setActiveButton(R.id.btnDiscover);
        });

        btnCreate.setOnClickListener(view -> {
            replaceFragment(new CreateFragment());
        });

        btnFriend.setOnClickListener(view -> {
            replaceFragment(new FriendFragment());
            setActiveButton(R.id.btnFriend);
        });

        btnInbox.setOnClickListener(view -> {
            replaceFragment(new InboxFragment());
            setActiveButton(R.id.btnInbox);
        });

        binding.imageViewProfileAvatar.setOnClickListener(view -> {
            startActivity(new Intent(BaseMenuActivity.this, ProfileActivity.class));
        });

        btnHome.performClick();
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
        //replace FrameLayout to others fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onProfileImageChanged(String profileImageUrl) {
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Picasso.get().load(profileImageUrl).into(binding.imageViewProfileAvatar);
        } else {
            Drawable placeholderDrawable = ContextCompat.getDrawable(BaseMenuActivity.this, R.drawable.baseline_person_24);
            Picasso.get().load(R.drawable.baseline_person_24).placeholder(placeholderDrawable).into(binding.imageViewProfileAvatar);
        }
    }

}