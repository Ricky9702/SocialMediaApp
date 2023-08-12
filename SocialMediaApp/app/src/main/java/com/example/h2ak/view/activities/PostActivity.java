package com.example.h2ak.view.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.h2ak.R;
import com.example.h2ak.adapter.PostAdapter;
import com.example.h2ak.contract.PostActivityContract;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.presenter.PostActivityPresenter;

import java.util.List;

public class PostActivity extends AppCompatActivity implements PostActivityContract.View {
    Toolbar toolbar;
    PostAdapter postDisplayAdapter;
    RecyclerView recyclerViewPosts;
    private PostActivityContract.Presenter presenter;
    private int scrollToPosition = -1; // if - 1 then no need to scroll
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

         toolbar = findViewById(R.id.toolBar);
         toolbar.setTitle("Posts");
         toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

         toolbar.setNavigationOnClickListener(view -> {
             onBackPressed();
             finish();
         });


        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));

        postDisplayAdapter = new PostAdapter(this, flag -> {
            Intent resultIntent = new Intent();
            if (flag) {
                resultIntent.putExtra("UPDATED", true);
            }
            setResult(RESULT_OK, resultIntent);
        });

        recyclerViewPosts.setAdapter(postDisplayAdapter);

        presenter = new PostActivityPresenter(this, this);






    }

    @Override
    protected void onStart() {
        super.onStart();

        // fetch post from previous activity
        if (getIntent() != null) {
            scrollToPosition = getIntent().getIntExtra("postPosition", -1);

            String id = getIntent().getStringExtra("id");
            Log.d("params2", "onBindViewHolder: "+ id);
            String privacy1 = getIntent().getStringExtra("privacy1");
            Log.d("params2", "onBindViewHolder: "+ privacy1);
            String privacy2 = getIntent().getStringExtra("privacy2");
            Log.d("params2", "onBindViewHolder: "+ privacy2);

            if (id != null && !id.isEmpty()) {

                // the user is stranger
                if (privacy2 == null || privacy2.isEmpty()) {
                    presenter.getAllPostByUserIdWithPrivacy(id, privacy1, null);
                } else {
                    // the user is friends
                    presenter.getAllPostByUserIdWithPrivacy(id, privacy1, privacy2);
                }
                postDisplayAdapter.setEnableAction(false);
            } else {
                // this is the current user
                presenter.getAllPostByUserIdWithPrivacy(null, null, null);
                postDisplayAdapter.setEnableAction(true);
            }
        }

        // scoll to postion
        if (scrollToPosition >= 0) {
            recyclerViewPosts.post(() -> {
                // Scroll the RecyclerView to the desired position
                recyclerViewPosts.scrollToPosition(scrollToPosition);
            });
        }

        // open gallery in post adapter for add a new image
        postDisplayAdapter.setGalleryActivityResultLauncher(registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) Log.d("TAG", "onCreate: " + imageUri.toString());

                    long currentTimeMillis = System.currentTimeMillis();

                    PostImages postImages = new PostImages(imageUri.toString(), postDisplayAdapter.getCurrentPost());

                    postImages.setCreatedDate(postImages.getCreatedDate() + "_" + currentTimeMillis);

                    Log.d("TAG", "postImages createdDate: " + postImages.getCreatedDate());

                    // ask image slider adapter to change
                    postDisplayAdapter.getAdapter().addPostImages(postImages);

                }
            }
        }));
    }

    @Override
    public void onListPostRecieved(List<Post> postList) {
        if (!postList.isEmpty()) {
            postDisplayAdapter.setPostList(postList);
        } else {
            Log.d("TAG", "onMapPostRecieved: empty ");
        }

    }
}