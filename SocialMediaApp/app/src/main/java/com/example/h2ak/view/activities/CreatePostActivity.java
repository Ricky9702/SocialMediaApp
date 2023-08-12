package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

import com.example.h2ak.R;
import com.example.h2ak.contract.CreatePostActivityContract;
import com.example.h2ak.view.fragments.CreatePostChooseImageFragment;

public class CreatePostActivity extends AppCompatActivity implements CreatePostActivityContract.View {
    Toolbar toolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("Create a new post");
        toolBar.setTitleTextColor(getColor(R.color.black));
        toolBar.setNavigationIcon(getDrawable(R.drawable.baseline_close_24));

        toolBar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutCreatePost, new CreatePostChooseImageFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}