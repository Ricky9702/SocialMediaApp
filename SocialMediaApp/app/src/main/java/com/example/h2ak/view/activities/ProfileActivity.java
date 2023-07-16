package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.h2ak.R;
import com.example.h2ak.controllers.UserController;
import com.example.h2ak.controllers.UserService;
import com.example.h2ak.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity{
    private Toolbar toolBar;

    private Button buttonLogout, btnEditProfile;
    private CircularImageView imageViewProfileAvatar;
    private TextView textViewProfileName;
    private UserController userController;
    private FirebaseAuth firebaseAuth;

    {
        userController = new UserController();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

        userController.getUser(user -> {
            if (user != null) {
                //get data
                String id = user.getId();
                String email = user.getEmail();
                String username = user.getName();
                Date createdDate = user.getCreatedDate();
                String avatar = user.getAvatar();
                String phone = user.getPhone();
                //set data
                textViewProfileName.setText(email);
                if (avatar != null && !avatar.isEmpty()) {
                    Picasso.get().load(avatar).into(imageViewProfileAvatar);
                } else {
                    Drawable placeholderDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_person_24);
                    Picasso.get().load(R.drawable.baseline_person_24).placeholder(placeholderDrawable).into(imageViewProfileAvatar);
                }
            }
        });

    }
    private void init() {
        //Get views
        btnEditProfile = findViewById(R.id.btnEditProfile);
        buttonLogout = findViewById(R.id.btnlogout);
        imageViewProfileAvatar = findViewById(R.id.imageViewProfileAvatar);
        textViewProfileName = findViewById(R.id.textViewProfileName);
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
            onBackPressed();
            finish();
        });

    }

}