package com.example.h2ak;

import com.example.h2ak.Fragments.CreateFragment;
import com.example.h2ak.Fragments.DiscoverFragment;
import com.example.h2ak.Fragments.HomeFragment;
import com.example.h2ak.Fragments.ImageFragment;
import com.example.h2ak.Fragments.InboxFragment;
import com.example.h2ak.R;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.h2ak.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private Map<Integer, Class<? extends Fragment>> fragmentMap = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
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
    }
    void replaceFragment(int itemId) {
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
