package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.view.fragments.AdminFriendShipFragment;
import com.example.h2ak.view.fragments.AdminPostFragment;
import com.example.h2ak.view.fragments.AdminUserFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {
    Toolbar toolbar;
    FrameLayout frameLayoutAdmin;
    TabLayout tabLayoutParent;
    Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        toolbar = findViewById(R.id.toolBar);
        frameLayoutAdmin = findViewById(R.id.frameLayoutAdmin);
        tabLayoutParent = findViewById(R.id.tabLayoutParent);

        toolbar.setTitle("Welcome back admin");
        toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        tabLayoutParent.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replaceFragment(new AdminUserFragment());
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new AdminFriendShipFragment());
                } else {
                    replaceFragment(new AdminPostFragment());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayoutParent.getTabAt(1).select();
        tabLayoutParent.getTabAt(0).select();
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutAdmin, fragment);
        fragmentTransaction.commit();
    }

}