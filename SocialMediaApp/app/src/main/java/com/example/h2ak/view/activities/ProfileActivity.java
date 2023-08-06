package com.example.h2ak.view.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.adapter.ProfileAdapter;
import com.example.h2ak.contract.ProfileActivityContract;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.ProfileActivityPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ProfileActivity extends AppCompatActivity implements ProfileActivityContract.View {
    Toolbar toolBar;

    private Button buttonLogout;
    Button btnEditProfile;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerViewProfile;
    ProfileAdapter profileAdapter;
    private ProfileActivityContract.Presenter presenter;
    private ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    String avatarUri = result.getData().getStringExtra("UPDATED_HAS_AVATAR");
                    String coverUri = result.getData().getStringExtra("UPDATED_HAS_COVER");
                    boolean updatedDone = result.getData() != null && result.getData().getBooleanExtra("UPDATED_DONE", true);
                    Log.d("Test avatarUri", avatarUri + "");
                    Log.d("Test coverUri", coverUri + "");

                    if (avatarUri != null && !avatarUri.isEmpty()) {
                        profileAdapter.setAvatarUri(avatarUri);
                    }
                    if (coverUri != null && !coverUri.isEmpty()) {
                        Log.d("TEST coverUri222", coverUri);
                        profileAdapter.setCoverUri(coverUri);
                    }
                    if (updatedDone) {
                        profileAdapter.onReload();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

        presenter = new ProfileActivityPresenter(this, this);
        presenter.getUser();
    }

    private void init() {

        //Get views
        btnEditProfile = findViewById(R.id.btnEditProfile);
        buttonLogout = findViewById(R.id.btnlogout);
        recyclerViewProfile = findViewById(R.id.recyclerViewProfile);
        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));
        firebaseAuth = FirebaseAuth.getInstance();
        profileAdapter = new ProfileAdapter(this);
        recyclerViewProfile.setAdapter(profileAdapter);


        //Edit toolBar
        toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("Profile");
        toolBar.setTitleTextColor(getColor(R.color.black));
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);


        btnEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        getButtonLogout().setOnClickListener(v -> {
            MyApp.getInstance().setUserOffline();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        toolBar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        profileAdapter.onReload();
    }

    @Override
    protected void onDestroy() {
        profileAdapter.setCoverUri(null);
        profileAdapter.setAvatarUri(null);
        super.onDestroy();
    }

    @Override
    public void loadUserInformation(User user) {
        if (user != null) {
            profileAdapter.setCurrentUser(user);
        }
    }

    public Button getButtonLogout() {
        return buttonLogout;
    }
}