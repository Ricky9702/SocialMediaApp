package com.example.h2ak.view.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.h2ak.Firebase.FirebaseDataSync;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.adapter.ProfileAdapter;
import com.example.h2ak.adapter.ProfilePostDisplayAdapter;
import com.example.h2ak.contract.UserProfileAcitivtyContract;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.UserProfileAcitivtyPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity implements UserProfileAcitivtyContract.View {
    Toolbar toolBar;
    RecyclerView recyclerViewProfile, recyclerViewPostImage;
    ProfileAdapter profileAdapter;
    ProfilePostDisplayAdapter profileDisplayPostAdapter;
    Button btnAddFriend;
    ProgressBar progressBar;
    private UserProfileAcitivtyContract.Presenter presenter;
    private String id;
    private Map<String, String> params = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // init views
        btnAddFriend = findViewById(R.id.btnAddFriend);
        progressBar = findViewById(R.id.progressBar);


        //Edit toolBar
        toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("Profile");
        toolBar.setTitleTextColor(getColor(R.color.black));
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        // Init recyclerView
        recyclerViewProfile = findViewById(R.id.recyclerViewProfile);

        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));

        profileAdapter = new ProfileAdapter(this);

        setPresenter(new UserProfileAcitivtyPresenter(this, this));

        recyclerViewPostImage = findViewById(R.id.recyclerViewPostImage);

        profileDisplayPostAdapter = new ProfilePostDisplayAdapter(this);

        recyclerViewPostImage.setAdapter(profileDisplayPostAdapter);

        recyclerViewPostImage.setLayoutManager(new GridLayoutManager(this, 3));




        profileDisplayPostAdapter.setPostActivityLauncher(registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {

                    }
                }));


        recyclerViewProfile.setAdapter(profileAdapter);

        toolBar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get the user id from previous activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID")) {
            id = intent.getStringExtra("USER_ID");

            if (id.equals(MyApp.getInstance().getCurrentUserId())) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }

            params.put("id", id);
            params.put("privacy1", Post.PostPrivacy.PUBLIC.getPrivacy());
            getPresenter().getUserById(id);
            presenter.getAllPostByUserId(id, Post.PostPrivacy.PUBLIC.getPrivacy(), null);
            profileDisplayPostAdapter.setParams(params);
        }
    }

    @Override
    public void onStatusReceived(String status) {
        if (status != null) {

            if (status.equals(FriendShip.FriendShipStatus.PENDING.getStatus())) {
                btnAddFriend.setText("Cancel request");

                Drawable drawableLeft = ContextCompat.getDrawable(this, R.drawable.baseline_schedule_send_24);

                btnAddFriend.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);


            } else if (status.equals(FriendShip.FriendShipStatus.DELETED.getStatus())) {
                btnAddFriend.setText("Add Friend");

                Drawable drawableLeft = ContextCompat.getDrawable(this, R.drawable.baseline_person_add_alt_1_24);

                btnAddFriend.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);

            } else {
                btnAddFriend.setText("Friends");

                Drawable drawableLeft = ContextCompat.getDrawable(this, R.drawable.baseline_friends);

                btnAddFriend.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);

                params.put("privacy2", Post.PostPrivacy.FRIENDS.getPrivacy());

                presenter.getAllPostByUserId(id, Post.PostPrivacy.PUBLIC.getPrivacy(), Post.PostPrivacy.FRIENDS.getPrivacy());

            }
        }
        profileDisplayPostAdapter.setParams(params);

    }

    @Override
    public void onUserRecieved(User user) {
        profileAdapter.setCurrentUser(user);
        profileAdapter.onReload();

        // Set on click event on button add friend
        btnAddFriend.setOnClickListener(view -> {
            getPresenter().createOrUpdateFriendRequest(user);
        });
    }

    @Override
    public void onSendMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendFriendRequestFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar(boolean flag) {
        if (flag) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onListPostRecieved(List<Post> postList) {
        if (postList != null && !postList.isEmpty()) {
            profileDisplayPostAdapter.setPostList(postList);
        }
    }

    public UserProfileAcitivtyContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(UserProfileAcitivtyContract.Presenter presenter) {
        this.presenter = presenter;
    }
}