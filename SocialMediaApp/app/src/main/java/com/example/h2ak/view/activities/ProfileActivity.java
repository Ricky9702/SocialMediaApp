package com.example.h2ak.view.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.adapter.DisplayFriendAdapter;
import com.example.h2ak.adapter.ProfileAdapter;
import com.example.h2ak.adapter.ProfilePostDisplayAdapter;
import com.example.h2ak.contract.ProfileActivityContract;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.ProfileActivityPresenter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileActivityContract.View {
    Toolbar toolBar;

    private Button buttonLogout;
    Button btnEditProfile;
    RecyclerView recyclerViewProfile, recyclerViewPostImage, recyclerViewFriends;
    DisplayFriendAdapter friendAdapter;
    ProfileAdapter profileAdapter;
    ProfilePostDisplayAdapter profileDisplayPostAdapter;
    TextView textViewPostPlaceHolder, textViewFriendsPlaceHolder, textViewFriendsCount, textViewPostCount;
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter = new ProfileActivityPresenter(this, this);
        presenter.getUser();

        profileDisplayPostAdapter.setPostActivityLauncher(registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        boolean isChange = result.getData() != null && result.getData().getBooleanExtra("UPDATED", false);
                        if (isChange) {
                            presenter.getAllPost();
                        }
                    }
                }));
        presenter.getAllPost();
        presenter.getFriends();
    }

    private void init() {

        //Get views
        btnEditProfile = findViewById(R.id.btnEditProfile);
        buttonLogout = findViewById(R.id.btnlogout);
        textViewFriendsCount = findViewById(R.id.textViewFriendsCount);
        textViewPostCount = findViewById(R.id.textViewPostCount);
        textViewFriendsPlaceHolder = findViewById(R.id.textViewFriendsPlaceHolder);
        textViewPostPlaceHolder = findViewById(R.id.textViewPostPlaceHolder);
        recyclerViewProfile = findViewById(R.id.recyclerViewProfile);
        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));
        profileAdapter = new ProfileAdapter(this);
        recyclerViewProfile.setAdapter(profileAdapter);

        recyclerViewPostImage = findViewById(R.id.recyclerViewPostImage);

        profileDisplayPostAdapter = new ProfilePostDisplayAdapter(this);

        recyclerViewPostImage.setAdapter(profileDisplayPostAdapter);
        recyclerViewPostImage.setLayoutManager(new GridLayoutManager(this, 3));

        friendAdapter = new DisplayFriendAdapter(this);

        recyclerViewFriends = findViewById(R.id.recyclerViewFriends);
        recyclerViewFriends.setAdapter(friendAdapter);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));



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

    @Override
    public void onPostListRecieved(List<Post> postList) {
        if (postList != null && !postList.isEmpty()) {
            profileDisplayPostAdapter.setPostList(postList);
            textViewPostCount.setText("(" + profileDisplayPostAdapter.getPostList().size() + ")");
            recyclerViewPostImage.setVisibility(View.VISIBLE);
            textViewPostPlaceHolder.setVisibility(View.GONE);
        } else {
            textViewPostCount.setText("");
            recyclerViewPostImage.setVisibility(View.GONE);
            textViewPostPlaceHolder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSetFriendsReceived(List<User> friends) {
        if (friends != null && !friends.isEmpty()) {
            friendAdapter.setFriends(friends);
            textViewFriendsCount.setText("("+friends.size()+")");
            recyclerViewFriends.setVisibility(View.VISIBLE);
            textViewFriendsPlaceHolder.setVisibility(View.GONE);
        } else {
            textViewFriendsCount.setText("");
            recyclerViewFriends.setVisibility(View.GONE);
            textViewFriendsPlaceHolder.setVisibility(View.VISIBLE);
        }
    }

    public Button getButtonLogout() {
        return buttonLogout;
    }
}