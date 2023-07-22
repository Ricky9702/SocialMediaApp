package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.h2ak.R;
import com.example.h2ak.contract.ProfileActivityContract;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.BaseMenuPresenter;
import com.example.h2ak.presenter.ProfileActivityPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements ProfileActivityContract.View {
    Toolbar toolBar;

    Button buttonLogout, btnEditProfile;
    CircularImageView imageViewProfileAvatar;
    ImageView imageViewProfileBackground;
    TextView textViewProfileName, textViewProfileBio;
    FirebaseAuth firebaseAuth;
    private ProfileActivityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

        presenter = new ProfileActivityPresenter(this);
        presenter.getUser();
    }

    private void init() {

        //Get views
        btnEditProfile = findViewById(R.id.btnEditProfile);
        buttonLogout = findViewById(R.id.btnlogout);
        imageViewProfileBackground = findViewById(R.id.imageViewProfileBackground);
        imageViewProfileAvatar = findViewById(R.id.imageViewProfileAvatar);
        textViewProfileName = findViewById(R.id.textViewProfileName);
        textViewProfileBio = findViewById(R.id.textViewProfileBio);
        firebaseAuth = FirebaseAuth.getInstance();


        //Edit toolBar
        toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("Profile");
        toolBar.setTitleTextColor(getColor(R.color.black));
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);


        btnEditProfile.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        buttonLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        toolBar.setNavigationOnClickListener(view -> {
            startActivity(new Intent(this, BaseMenuActivity.class));
            finish();
        });

    }

    @Override
    public void loadUserInformation(User user) {
        if (user != null) {

            String name = user.getName();
            String bio = user.getBio();
            String avatar = user.getImageAvatar();
            String cover = user.getImageCover();

            //Load the name
            textViewProfileName.setText(name);

            //Load the bio
            if (bio != null && !bio.isEmpty()) {
                textViewProfileBio.setVisibility(View.VISIBLE);
                user.setBio(bio);
            } else {
                textViewProfileBio.setVisibility(View.GONE);
            }

            // Load the avatar image
            if (avatar != null && !avatar.isEmpty()) {
                Picasso.get().load(avatar).into(imageViewProfileAvatar);
            } else {
                imageViewProfileAvatar.setImageResource(R.drawable.baseline_avatar_place_holder);
            }

            // Load the cover image
            if (cover != null && !cover.isEmpty()) {
                Picasso.get().load(cover).into(imageViewProfileBackground);
            } else {
                imageViewProfileBackground.setImageResource(R.color.not_active_icon);
            }
        }
    }
}