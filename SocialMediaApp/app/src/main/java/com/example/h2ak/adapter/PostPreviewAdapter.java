package com.example.h2ak.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.h2ak.R;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.relex.circleindicator.CircleIndicator2;

public class PostPreviewAdapter extends RecyclerView.Adapter<PostPreviewAdapter.ViewHolder> {
    private Context context;
    private List<PostImages> postImages = new ArrayList<>();
    private Post post;
    private PagerSnapHelper snapHelper;

    public PostPreviewAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_post_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.LinearLayoutPostStats.setVisibility(View.GONE);
        holder.linearLayoutLikeCommentPost.setVisibility(View.GONE);
        holder.btnPostAction.setVisibility(View.GONE);

        User user = post.getUser();
        holder.textViewPostUserName.setText(user.getName());

        holder.textViewPostCreatedDate.setText(TextInputLayoutUtils.covertTimeToText(post.getCreatedDate()));

        holder.textViewPostContent.setText(post.getContent());

        if (post.getPrivacy().equals(Post.PostPrivacy.PUBLIC.getPrivacy())) {
            holder.imageViewPostPrivacy.setImageResource(R.drawable.baseline_public_24);
        } else if (post.getPrivacy().equals(Post.PostPrivacy.FRIENDS.getPrivacy())) {
            holder.imageViewPostPrivacy.setImageResource(R.drawable.baseline_friend_privacy_24);
        } else {
            holder.imageViewPostPrivacy.setImageResource(R.drawable.baseline_lock_24);
        }

        if (user.getImageAvatar().isEmpty()) {
            Glide.with(context)
                    .load(R.drawable.baseline_avatar_place_holder)
                    .placeholder(R.drawable.baseline_avatar_place_holder)
                    .error(R.drawable.baseline_report_gmailerrorred_24)
                    .into(holder.imageViewPostUserAvatar);
        } else {
            Glide.with(context)
                    .load(user.getImageAvatar())
                    .placeholder(R.drawable.baseline_avatar_place_holder)
                    .error(R.drawable.baseline_report_gmailerrorred_24)
                    .into(holder.imageViewPostUserAvatar);
        }

        // set image for post
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(context);
        List<String> selectedImage = postImages.stream().flatMap(postImages1 -> Stream.of(postImages1.getImageUrl())).collect(Collectors.toList());
        imageSliderAdapter.setListSelectedImageUrl(selectedImage);
        holder.recyclerViewImageSlider.setAdapter(imageSliderAdapter);
        holder.recyclerViewImageSlider.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        if (getSnapHelper() == null) {
            setSnapHelper(new PagerSnapHelper());
            if (holder.recyclerViewImageSlider.getOnFlingListener() == null) {
                snapHelper.attachToRecyclerView(holder.recyclerViewImageSlider);
            }
            holder.indicator2.attachToRecyclerView(holder.recyclerViewImageSlider, getSnapHelper());
        }


        if (this.postImages.size() > 1) {
            holder.indicator2.setVisibility(View.VISIBLE);
        } else { holder.indicator2.setVisibility(View.GONE);}
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public List<PostImages> getPostImages() {
        return postImages;
    }

    public void setPostImages(List<PostImages> postImages) {
        Collections.sort(postImages, (postImages1, postImages2) -> {
            String[] parts1 = postImages1.getCreatedDate().split("_");
            String[] parts2 = postImages2.getCreatedDate().split("_");

            Date date1 = parseDateFromString(parts1[0]); // Extract date part
            Date date2 = parseDateFromString(parts2[0]); // Extract date part

            int dateComparison = date1.compareTo(date2);
            if (dateComparison == 0) {
                long millis1 = Long.parseLong(parts1[1]); // Extract millis part
                long millis2 = Long.parseLong(parts2[1]); // Extract millis part
                return Long.compare(millis1, millis2);
            }
            return dateComparison;
        });

        this.postImages = postImages;
    }


    private Date parseDateFromString(String dateString) {
        try {
            return TextInputLayoutUtils.simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Return a default date in case of parsing error
        }
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post, boolean allowNotifyDataSetChange) {
        this.post = post;
        if (allowNotifyDataSetChange)
            this.notifyDataSetChanged();
    }

    public PagerSnapHelper getSnapHelper() {
        return snapHelper;
    }

    public void setSnapHelper(PagerSnapHelper snapHelper) {
        this.snapHelper = snapHelper;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout LinearLayoutPostStats, linearLayoutLikeCommentPost;
        CircularImageView imageViewPostUserAvatar;
        TextView textViewPostUserName, textViewPostCreatedDate, textViewPostContent,
                textViewPostStatsLike, textViewPostStatsComment;
        ImageView imageViewPostPrivacy;
        ImageButton btnPostAction;
        RecyclerView recyclerViewImageSlider;
        ImageButton btnLikePost, btnCommentPost;
        CircleIndicator2 indicator2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            LinearLayoutPostStats = itemView.findViewById(R.id.LinearLayoutPostStats);
            linearLayoutLikeCommentPost = itemView.findViewById(R.id.linearLayoutLikeCommentPost);
            imageViewPostUserAvatar = itemView.findViewById(R.id.imageViewPostUserAvatar);
            imageViewPostPrivacy = itemView.findViewById(R.id.imageViewPostPrivacy);
            textViewPostUserName = itemView.findViewById(R.id.textViewPostUserName);
            textViewPostCreatedDate = itemView.findViewById(R.id.textViewPostCreatedDate);
            textViewPostContent = itemView.findViewById(R.id.textViewPostContent);
            textViewPostStatsLike = itemView.findViewById(R.id.textViewPostStatsLike);
            textViewPostStatsComment = itemView.findViewById(R.id.textViewPostStatsComment);
            btnPostAction = itemView.findViewById(R.id.btnPostAction);
            recyclerViewImageSlider = itemView.findViewById(R.id.recyclerViewImageSlider);
            btnLikePost = itemView.findViewById(R.id.btnLikePost);
            btnCommentPost = itemView.findViewById(R.id.btnCommentPost);
            indicator2 = itemView.findViewById(R.id.indicator);
//            textViewPostCreatedDate.setVisibility(View.GONE);
        }
    }
}
