package com.example.h2ak.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostImagesDataSourceImpl;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.view.activities.PostActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfilePostDisplayAdapter extends RecyclerView.Adapter<ProfilePostDisplayAdapter.ViewHolder> {

    private List<Post> postList;
    PostImagesDataSource postImagesDataSource;
    Context context;
    private Map<String, String> params;

    private ActivityResultLauncher<Intent> postActivityLauncher;

    public ProfilePostDisplayAdapter(Context context) {
        this.context = context;
        postList = new ArrayList<>();
        postImagesDataSource = PostImagesDataSourceImpl.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_display_post_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);


        // Resize the post image
        int imageSize = calculateImageSize();
        holder.imageViewPostFirstImage.getLayoutParams().width = imageSize;
        holder.imageViewPostFirstImage.getLayoutParams().height = (int) (imageSize + (imageSize * 0.3));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.imageViewPostFirstImage.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, (int) (2 * context.getResources().getDisplayMetrics().density)); // Add vertical spacing

        Log.d("TAG", "onBindViewHolder: " + postImagesDataSource.getAllPostImagesByPost(post).size());

        PostImages postImages = postImagesDataSource.getFirstPostImages(post);

        if (postImages != null) {
            Glide.with(context)
                    .load(postImages.getImageUrl())
                    .placeholder(R.color.not_active_icon)
                    .error(R.drawable.baseline_report_gmailerrorred_24)
                    .into(holder.imageViewPostFirstImage);
        }

        if (postImagesDataSource.getAllPostImagesByPost(post).size() > 1) {
            holder.imageViewGalleryPost.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewGalleryPost.setVisibility(View.GONE);
        }

        holder.relativeLayoutParent.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostActivity.class);
            intent.putExtra("postPosition", position);
            if (params != null) {
                String id = params.get("id");
                Log.d("params", "onBindViewHolder: "+ id);
                String privacy1 = params.get("privacy1");
                Log.d("params", "onBindViewHolder: "+ privacy1);
                String privacy2 = params.get("privacy2");
                Log.d("params", "onBindViewHolder: "+ privacy2);

                if (id != null && !id.isEmpty()) {
                    intent.putExtra("id", id);
                }

                if (privacy1 != null && !privacy1.isEmpty()) {
                    intent.putExtra("privacy1", privacy1);
                }

                if (privacy2 != null && !privacy2.isEmpty()) {
                    intent.putExtra("privacy2", privacy2);
                }

            }
            postActivityLauncher.launch(intent);
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        this.notifyDataSetChanged();
    }

    public ActivityResultLauncher<Intent> getPostActivityLauncher() {
        return postActivityLauncher;
    }

    public void setPostActivityLauncher(ActivityResultLauncher<Intent> postActivityLauncher) {
        this.postActivityLauncher = postActivityLauncher;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayoutParent;
        ImageView imageViewPostFirstImage, imageViewGalleryPost;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayoutParent = itemView.findViewById(R.id.relativeLayoutParent);
            imageViewPostFirstImage = itemView.findViewById(R.id.imageViewPostFirstImage);
            imageViewGalleryPost = itemView.findViewById(R.id.imageViewGalleryPost);

        }
    }

    private int calculateImageSize() {
        // Calculate the square size based on the screen width and desired grid spacing
        // You can adjust the calculations based on your layout requirements
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int spacing = (int) (3 * displayMetrics.density); // Adjust spacing as needed
        int availableWidth = screenWidth - (spacing * (3 - 1));
        int imageSize = availableWidth / 3;
        return imageSize;
    }
}
