package com.example.h2ak.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.h2ak.R;
import com.example.h2ak.database.FirebaseDatabaseHelper;
import com.example.h2ak.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolBar;

    private Button buttonLogout;
    private CircularImageView imageViewProfileAvatar;
    private TextView textViewProfileName;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }
    private void init() {
        //Get views
        buttonLogout = findViewById(R.id.btnlogout);
        imageViewProfileAvatar = findViewById(R.id.imageViewProfileAvatar);
        textViewProfileName = findViewById(R.id.textViewProfileName);


        //Edit toolBar
        toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("Profile");
        toolBar.setTitleTextColor(getColor(R.color.black));
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);


        updateProfile();

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
    public void updateProfile() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabaseHelper.getFirebaseDatabaseUser();
        if (databaseReference == null) {
            throw new RuntimeException("Database not found");
        }

        if (user != null) {
            Query query = databaseReference.child(user.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        String id = user.getId();
                        String email = user.getEmail();
                        String username = user.getName();
                        Date createdDate = user.getCreatedDate();
                        String avatar = user.getAvatar();
                        String phone = user.getPhone();

                        // Use the retrieved user data as needed
                        textViewProfileName.setText(email);
                        Log.d("Hello sir", "length is " + avatar.length());
                        if (avatar != null && !TextUtils.isEmpty(avatar)) {
                            Picasso.get().load(avatar).into(imageViewProfileAvatar);
                        } else {
                            Drawable placeholderDrawable = ContextCompat.getDrawable(ProfileActivity.this, R.drawable.baseline_person_24);
                            Picasso.get().load(R.drawable.baseline_person_24).placeholder(placeholderDrawable).into(imageViewProfileAvatar);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }
}