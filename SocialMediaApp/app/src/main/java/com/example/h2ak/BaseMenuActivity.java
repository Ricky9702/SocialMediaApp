package com.example.h2ak;

import com.example.h2ak.Fragments.CreateFragment;
import com.example.h2ak.Fragments.DiscoverFragment;
import com.example.h2ak.Fragments.HomeFragment;
import com.example.h2ak.Fragments.ImageFragment;
import com.example.h2ak.Fragments.InboxFragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.h2ak.databinding.ActivityBaseMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class BaseMenuActivity extends AppCompatActivity {
    private ActivityBaseMenuBinding binding;
    private FirebaseAuth firebaseAuth;

    private Map<Integer, Class<? extends Fragment>> fragmentMap = new HashMap<>();
    private Map<Integer, Map<String, Integer>> buttonMap = new HashMap<>();
    private Map<String, Integer> buttonInfo = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBaseMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

////        buttonMap.put(R.id.btnHome, buttonInfo.put("defaultImage", R.drawable.baseline_home_24));
//        buttonInfo.put("defaultColor", R.color);
//        buttonInfo.put("activeColor", R.id.textViewDiscover);
//        buttonInfo.put("textViewId", R.id.textViewImage);


        fragmentMap.put(R.id.btnHome, HomeFragment.class);
        fragmentMap.put(R.id.btnDiscover, DiscoverFragment.class);
        fragmentMap.put(R.id.btnCreate, CreateFragment.class);
        fragmentMap.put(R.id.btnImage, ImageFragment.class);
        fragmentMap.put(R.id.btnProfile, InboxFragment.class);
        replaceFragment(R.id.btnHome);

        binding.btnHome.setOnClickListener(view -> {
            replaceFragment(R.id.btnHome);
//                setActiveButton(R.id.btnHome, "baseline_home_24", "baseline_home_24_active");
        });

        binding.btnDiscover.setOnClickListener(view -> {
            replaceFragment(R.id.btnDiscover);
        });

        binding.btnCreate.setOnClickListener(view -> {
            replaceFragment(R.id.btnCreate);
        });

        binding.btnImage.setOnClickListener(view -> {
            replaceFragment(R.id.btnImage);
        });

        binding.btnProfile.setOnClickListener(view -> {
            replaceFragment(R.id.btnProfile);
        });


        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        }
        else {
            startActivity(new Intent(BaseMenuActivity.this, LoginActivity.class));
            finish();
        }
    }

//    private void setActiveButton(int idButton, String defaultImage, String activeImage) {
//        if (idButton >= 0  && !defaultImage.isEmpty() && !activeImage.isEmpty()) {
//            ImageButton btnActive = findViewById(idButton);
//            TextView textViewActive = findViewById(btnTextViewMap.get(idButton));
//            // Load default drawable
//            int defaultDrawableResId = getResources().getIdentifier(defaultImage, "drawable", getPackageName());
//            Drawable defaultDrawable = ContextCompat.getDrawable(this, defaultDrawableResId);
//
//            // Load active drawable
//            int activeDrawableResId = getResources().getIdentifier(activeImage, "drawable", getPackageName());
//            Drawable activeDrawable = ContextCompat.getDrawable(this, activeDrawableResId);
//
//            if (btnActive != null && textViewActive != null && defaultDrawable != null && activeDrawable != null) {
//                btnActive.setImageDrawable(activeDrawable);
//                textViewActive.setTextColor(ContextCompat.getColor(this, R.color.active));
//                    for (int id: btnTextViewMap.keySet()) {
//                        if (id != R.id.btnCreate && id != idButton) {
//                            ImageButton btnDefault = findViewById(id);
//                            TextView textDefault = findViewById(btnTextViewMap.get(id));
//                            btnDefault.setImageDrawable(defaultDrawable);
//                            textDefault.setTextColor(ContextCompat.getColor(this, R.color.not_active));
//                        }
//                    }
//            }
//        }
//    }





    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    private void replaceFragment(int itemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {
            fragmentTransaction.replace(R.id.frameLayout, fragmentMap.get(itemId).newInstance());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        fragmentTransaction.commit();
    }

}
