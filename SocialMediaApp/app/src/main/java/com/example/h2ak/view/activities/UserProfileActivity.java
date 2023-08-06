package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.example.h2ak.R;
import com.example.h2ak.adapter.ProfileAdapter;
import com.example.h2ak.contract.UserProfileAcitivtyContract;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.UserProfileAcitivtyPresenter;

public class UserProfileActivity extends AppCompatActivity implements UserProfileAcitivtyContract.View {
    Toolbar toolBar;
    RecyclerView recyclerViewProfile;
    ProfileAdapter profileAdapter;
    Button btnAddFriend;
    ProgressBar progressBar;
    private UserProfileAcitivtyContract.Presenter presenter;
    private String id;

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

        // Get the user email from previous activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID")) {
            id = intent.getStringExtra("USER_ID");
            getPresenter().getUserById(id);
        }

        recyclerViewProfile.setAdapter(profileAdapter);

        toolBar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });



    }

    @Override
    public void onStatusRecieved(String status) {
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
            }

        }
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

    public UserProfileAcitivtyContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(UserProfileAcitivtyContract.Presenter presenter) {
        this.presenter = presenter;
    }
}