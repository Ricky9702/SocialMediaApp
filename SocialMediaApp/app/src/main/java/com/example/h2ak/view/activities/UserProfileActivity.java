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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.h2ak.Firebase.FirebaseDataSync;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.adapter.DisplayFriendAdapter;
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
import java.util.stream.Collectors;

public class UserProfileActivity extends AppCompatActivity implements UserProfileAcitivtyContract.View {
    Toolbar toolBar;
    RecyclerView recyclerViewProfile, recyclerViewPostImage, recyclerViewFriends;
    DisplayFriendAdapter friendAdapter;
    ProfileAdapter profileAdapter;
    ProfilePostDisplayAdapter profileDisplayPostAdapter;
    TextView textViewPostPlaceHolder, textViewFriendsPlaceHolder, textViewFriendsCount, textViewPostCount;
    Button btnAddFriend;
    ProgressBar progressBar;
    private UserProfileAcitivtyContract.Presenter presenter;
    private String id;
    private Map<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // init views
        btnAddFriend = findViewById(R.id.btnAddFriend);
        progressBar = findViewById(R.id.progressBar);
        textViewFriendsCount = findViewById(R.id.textViewFriendsCount);
        textViewPostCount = findViewById(R.id.textViewPostCount);
        textViewPostPlaceHolder = findViewById(R.id.textViewPostPlaceHolder);
        textViewFriendsPlaceHolder = findViewById(R.id.textViewFriendsPlaceHolder);


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

        recyclerViewFriends = findViewById(R.id.recyclerViewFriends);
        friendAdapter = new DisplayFriendAdapter(this);
        recyclerViewFriends.setAdapter(friendAdapter);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        params = new HashMap<>();

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
            presenter.getFriendsByUserId(id);
            presenter.getAllPostByUserId(id);
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
            }
        }
    }

    @Override
    public void onUserRecieved(User user) {
        friendAdapter.setCurrentUser(user);
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

            Log.d("USERPROFILEACTIVITY", "params: " + params.get("id"));
            Log.d("USERPROFILEACTIVITY", "params: " + params.get("privacy1"));
            Log.d("USERPROFILEACTIVITY", "params: " + params.get("privacy2"));

            if (params.get("privacy2") != null) {
                profileDisplayPostAdapter.setPostList(postList.stream()
                        .filter(post -> !post.getPrivacy().equals(Post.PostPrivacy.ONLY_ME.getPrivacy())).collect(Collectors.toList()));
            }  else {
                profileDisplayPostAdapter.setPostList(postList.stream()
                        .filter(post -> post.getPrivacy().equals(Post.PostPrivacy.PUBLIC.getPrivacy())).collect(Collectors.toList()));
            }

            profileDisplayPostAdapter.setParams(this.params);

            Log.d("TAG", "onListPostRecieved: " + profileDisplayPostAdapter.getPostList().size());
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
            textViewFriendsCount.setText("(" + friends.size() + ")");
            recyclerViewFriends.setVisibility(View.VISIBLE);
            textViewFriendsPlaceHolder.setVisibility(View.GONE);
        } else {
            textViewFriendsCount.setText("");
            recyclerViewFriends.setVisibility(View.GONE);
            textViewFriendsPlaceHolder.setVisibility(View.VISIBLE);
        }
    }

    public UserProfileAcitivtyContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(UserProfileAcitivtyContract.Presenter presenter) {
        this.presenter = presenter;
    }
}