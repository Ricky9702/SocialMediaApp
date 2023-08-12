package com.example.h2ak.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.contract.EditProfileActivityContract;
import com.example.h2ak.model.User;
import com.example.h2ak.view.activities.DisplayImageActivity;
import com.example.h2ak.view.activities.EditProfileActivity;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    private UserDataSource userDataSource;
    User currentUser;
    Context context;
    private String avatarUri;
    private String coverUri;

    public ProfileAdapter(Context context) {
        this.context = context;
        userDataSource = UserDataSourceImpl.getInstance(context);
    }


    public void onReload() {
        this.currentUser = userDataSource.getUserById(currentUser.getId());
        this.notifyDataSetChanged();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_profile_layout,parent,false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {

        String name = currentUser.getName();
        String bio = currentUser.getBio();
        String avatar;
        String cover;

        //Load the name
        holder.textViewProfileName.setText(name);

        //Load the bio
        if (bio != null && !bio.isEmpty()) {
            holder.textViewProfileBio.setVisibility(View.VISIBLE);
            holder.textViewProfileBio.setText(bio);
        } else {
            holder.textViewProfileBio.setVisibility(View.GONE);
        }

        if (coverUri != null && !coverUri.isEmpty())
            cover = coverUri;
        else
            cover = currentUser.getImageCover();

        if (avatarUri != null && !avatarUri.isEmpty())
            avatar = avatarUri;
        else
            avatar = currentUser.getImageAvatar();


        Log.d("FINAL COVER", cover != null ? cover : "null");
        Log.d("FINAL AVATAR", avatar != null ? avatar : "null");

        // Load the avatar image
        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(context)
                    .load(avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                    .placeholder(R.drawable.baseline_avatar_place_holder)// Disable memory caching to reload the image
                    .into(holder.imageViewProfileAvatar);
        } else {
            holder.imageViewProfileAvatar.setImageResource(R.drawable.baseline_avatar_place_holder);
        }

        // Load the cover image
        if (cover != null && !cover.isEmpty()) {
            Glide.with(context)
                    .load(cover)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                    .placeholder(R.color.not_active_icon)// Disable memory caching to reload the image
                    .into(holder.imageViewProfileBackground);
        } else {
            holder.imageViewProfileBackground.setImageResource(R.color.not_active_icon);
        }

        // click image, show bigger
        holder.imageViewProfileAvatar.setOnClickListener(view -> {
                Intent intent = new Intent(context, DisplayImageActivity.class);
                intent.putExtra("IMAGE_URI", currentUser.getImageAvatar());
                intent.putExtra("TYPE", "AVATAR");
                context.startActivity(intent);
        });

        holder.imageViewProfileBackground.setOnClickListener(view -> {
                Intent intent = new Intent(context, DisplayImageActivity.class);
                intent.putExtra("IMAGE_URI", currentUser.getImageCover());
            intent.putExtra("TYPE", "COVER");
                context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
        notifyDataSetChanged();
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
        notifyDataSetChanged();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProfileBackground;
        CircularImageView imageViewProfileAvatar;
        TextView textViewProfileName;
        TextView textViewProfileBio;
        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfileBackground = itemView.findViewById(R.id.imageViewProfileBackground);
            imageViewProfileAvatar = itemView.findViewById(R.id.imageViewProfileAvatar);
            textViewProfileName = itemView.findViewById(R.id.textViewProfileName);
            textViewProfileBio = itemView.findViewById(R.id.textViewProfileBio);
        }
    }
}
