package com.example.h2ak;

import com.example.h2ak.databinding.ActivityBaseMenuBinding;
import com.example.h2ak.fragments.CreateFragment;
import com.example.h2ak.fragments.DiscoverFragment;
import com.example.h2ak.fragments.HomeFragment;
import com.example.h2ak.fragments.FriendFragment;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.h2ak.fragments.ProfileFragment;
import com.example.h2ak.utils.ValidationUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class BaseMenuActivity extends AppCompatActivity {
    private ActivityBaseMenuBinding binding;
    private FirebaseAuth firebaseAuth;

    private Map<Integer, Map<String, Integer>> buttonMap = new HashMap<>();
    private int previousButtonId = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
        private void init () {
            binding = ActivityBaseMenuBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            firebaseAuth = FirebaseAuth.getInstance();

            addButtonInfo(R.id.btnHome, R.drawable.baseline_home_24, R.drawable.baseline_home_24_active, R.id.textViewHome);
            addButtonInfo(R.id.btnDiscover, R.drawable.baseline_search_24, R.drawable.baseline_search_active, R.id.textViewDiscover);
            addButtonInfo(R.id.btnFriend, R.drawable.baseline_group_24, R.drawable.baseline_group_24_active, R.id.textViewFriend);
            addButtonInfo(R.id.btnProfile, R.drawable.baseline_person_24, R.drawable.baseline_person_24_active, R.id.textViewProfile);



            binding.btnHome.setOnClickListener(view -> {
                replaceFragment(new HomeFragment());
                setActiveButton(R.id.btnHome);
            });

            binding.btnDiscover.setOnClickListener(view -> {
                replaceFragment(new DiscoverFragment());
                setActiveButton(R.id.btnDiscover);
            });

            binding.btnCreate.setOnClickListener(view -> {
                replaceFragment(new CreateFragment());
            });

            binding.btnFriend.setOnClickListener(view -> {
                replaceFragment(new FriendFragment());
                setActiveButton(R.id.btnFriend);
            });

            binding.btnProfile.setOnClickListener(view -> {
                replaceFragment(new ProfileFragment());
                setActiveButton(R.id.btnProfile);
            });

            binding.btnHome.performClick();
        }

    private void addButtonInfo(int buttonId, int defaultImageResId, int activeImageResId, int textViewId) {
        Map<String, Integer> buttonInfo = new HashMap<>();
        buttonInfo.put("defaultImage", defaultImageResId);
        buttonInfo.put("activeImage", activeImageResId);
        buttonInfo.put("textViewId", textViewId);
        buttonMap.put(buttonId, buttonInfo);
    }


    private void setActiveButton(int activeButtonId) {
        if (previousButtonId != -1) {
            ImageButton previousButton = findViewById(previousButtonId);
            Map<String, Integer> previousButtonInfo = buttonMap.get(previousButtonId);
            TextView textViewPrevious = findViewById(previousButtonInfo.get("textViewId"));

            previousButton.setImageDrawable(getDrawable(previousButtonInfo.get("defaultImage")));
            textViewPrevious.setTextColor(getColor(R.color.not_active));
        }
        ImageButton activeButton = findViewById(activeButtonId);
        Map<String, Integer> activeButtonInfo= buttonMap.get(activeButtonId);
        TextView textViewActive = findViewById(activeButtonInfo.get("textViewId"));

        activeButton.setImageDrawable(getDrawable(activeButtonInfo.get("activeImage")));
        textViewActive.setTextColor(getColor(R.color.active));

        previousButtonId = activeButtonId;
    }





    @Override
    protected void onStart() {
        super.onStart();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}
