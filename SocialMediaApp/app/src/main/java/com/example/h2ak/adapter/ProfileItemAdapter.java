package com.example.h2ak.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.SearchHistoryDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SearchHistoryDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.SearchHistory;
import com.example.h2ak.model.User;
import com.example.h2ak.view.activities.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileItemAdapter extends RecyclerView.Adapter<ProfileItemAdapter.ViewHolder> {

    private List<User> userList = new ArrayList<>();
    private Context context;
    private SearchHistoryDataSource searchHistoryDataSource;
    private FirebaseAuth firebaseAuth;
    private UserDataSource userDataSource;

    public ProfileItemAdapter(Context context) {
        this.context = context;
        userDataSource = UserDataSourceImpl.getInstance(context);
        searchHistoryDataSource = SearchHistoryDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_search_profile_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        String name = user.getName();
        String createdDate = user.getCreatedDate();
        String avatar = user.getImageAvatar();


        holder.textViewProfileName.setText(name);
        holder.textViewProfileCreatedDate.setText(createdDate);

        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(context)
                    .load(avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                    .placeholder(R.drawable.baseline_avatar_place_holder)// Disable memory caching to reload the image
                    .into(holder.imageViewProfileAvatar);
        } else {
            holder.imageViewProfileAvatar.setImageResource(R.drawable.baseline_avatar_place_holder);
        }

        // let the parent handle the click event
        holder.textViewProfileName.setClickable(false);
        holder.textViewProfileCreatedDate.setClickable(false);
        holder.imageViewProfileAvatar.setClickable(false);

        holder.linearLayoutParent.setOnClickListener(view -> {

            // Check if the user is already in search history
            User currentUser = userDataSource.getUserById(firebaseAuth.getCurrentUser().getUid());
            SearchHistory searchHistory = searchHistoryDataSource.findSearchHistory(currentUser, user);
            if (searchHistory == null) {
                searchHistory = new SearchHistory(currentUser, user);
                if(searchHistoryDataSource.createSearchHistory(searchHistory, false)) {
                    Log.d("searchHistoryDataSource create", "success");
                } else {
                    Log.d("searchHistoryDataSource create", "failed");
                }
            } else {
                Log.d("searchHistory", "exists");
            }

            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("USER_ID", user.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (userList != null)
            return userList.size();
        return 0;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayoutParent;
        CircularImageView imageViewProfileAvatar;
        TextView textViewProfileName, textViewProfileCreatedDate;
        ImageView imageViewGoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutParent = itemView.findViewById(R.id.linearLayoutParent);
            imageViewProfileAvatar = itemView.findViewById(R.id.imageViewProfileAvatar);
            textViewProfileName = itemView.findViewById(R.id.textViewProfileName);
            textViewProfileCreatedDate = itemView.findViewById(R.id.textViewProfileCreatedDate);
            imageViewGoto = itemView.findViewById(R.id.imageViewGoto);
        }
    }

}
