package com.example.h2ak;

import com.example.h2ak.Fragments.CreateFragment;
import com.example.h2ak.Fragments.DiscoverFragment;
import com.example.h2ak.Fragments.HomeFragment;
import com.example.h2ak.Fragments.ImageFragment;
import com.example.h2ak.Fragments.InboxFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBaseMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentMap.put(R.id.homeMenu, HomeFragment.class);
        fragmentMap.put(R.id.discoverMenu, DiscoverFragment.class);
        fragmentMap.put(R.id.createMenu, CreateFragment.class);
        fragmentMap.put(R.id.imageMenu, ImageFragment.class);
        fragmentMap.put(R.id.inboxMenu, InboxFragment.class);

        replaceFragment(R.id.homeMenu);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            replaceFragment(item.getItemId());
            return true;
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

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.log_out_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.actionLogout) {
                // Handle logout menu item click
                firebaseAuth.signOut();
                checkUserStatus();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profileMenu) {
            // Handle profileMenu click
            View view = findViewById(R.id.profileMenu); // Replace with your profileMenu view id
            showPopupMenu(view);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
