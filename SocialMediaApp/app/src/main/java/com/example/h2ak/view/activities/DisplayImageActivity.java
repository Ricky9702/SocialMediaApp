package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h2ak.R;
import com.example.h2ak.utils.ImageSizeValidationUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class DisplayImageActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageViewFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        imageViewFullScreen = findViewById(R.id.imageViewFullScreen);
        toolbar = findViewById(R.id.toolBar);

        toolbar.setTitle("Back to profile");
        toolbar.setTitleTextColor(getColor(R.color.black));
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        Intent intent = getIntent();
        if (intent != null) {
            String imageUri = intent.getStringExtra("IMAGE_URI");
            if (imageUri != null && !imageUri.isEmpty()) {
                Glide.with(DisplayImageActivity.this)
                        .load(imageUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewFullScreen);
            } else {
                imageViewFullScreen.setImageResource(R.drawable.baseline_avatar_not_active_24);
            }
        }
    }

}