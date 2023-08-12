package com.example.h2ak.view.fragments;

import static androidx.core.content.ContextCompat.getColor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h2ak.R;
import com.example.h2ak.contract.HomeFragmentContract;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.HomeFragmentPresenter;
import com.example.h2ak.view.activities.ProfileActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

public class HomeFragment extends Fragment implements HomeFragmentContract.View {

    Toolbar toolbar;
    CircularImageView imageViewProfileAvatar;
    private HomeFragmentPresenter presenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Init views
        imageViewProfileAvatar = view.findViewById(R.id.imageViewProfileAvatar);

        // Edit toolBar
        toolbar = view.findViewById(R.id.toolBar);
        toolbar.setTitle("MyCompany");
        toolbar.setTitleTextColor(getColor(view.getContext(), R.color.black));

        // Events
        imageViewProfileAvatar.setOnClickListener(v -> {
            this.getActivity().overridePendingTransition(0, 0);
            startActivity(new Intent(this.getContext(), ProfileActivity.class));
        });

        //
        setPresenter(new HomeFragmentPresenter(this.getContext(), this));
        getPresenter().loadCurrentUser();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().loadCurrentUser();
    }

    @Override
    public void changeProfileAvatar(User user) {
        if (user != null) {
            String avatar = user.getImageAvatar();
            if (avatar != null && !avatar.isEmpty()) {
                Glide.with(this)
                        .load(avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.not_active_icon)// Disable memory caching to reload the image
                        .into(imageViewProfileAvatar);
            } else {
                imageViewProfileAvatar.setImageResource(R.drawable.baseline_avatar_place_holder);
            }
        }
    }

    public HomeFragmentPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(HomeFragmentPresenter presenter) {
        this.presenter = presenter;
    }
}