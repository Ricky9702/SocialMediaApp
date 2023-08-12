package com.example.h2ak.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h2ak.R;
import com.example.h2ak.model.User;
import com.example.h2ak.view.activities.UserProfileActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private Context context;
    private List<User> userList = new ArrayList<>();
    private List<User> originalUserList = new ArrayList<>();

    public FriendAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_friend_layout, parent, false);
        return new FriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User friend = userList.get(position);

        Glide.with(context)
                .load(friend.getImageAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                .placeholder(R.drawable.baseline_avatar_place_holder)// Disable memory caching to reload the image
                .into(holder.imageViewFriendAvatar);

        holder.textViewFriendName.setText(friend.getName());
        if (friend.isOnline()) {
            Glide.with(context)
                    .load(R.drawable.baseline_avatar_active_24)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                    .placeholder(R.drawable.baseline_avatar_active_24)// Disable memory caching to reload the image
                    .into(holder.imageViewFriendStatus);
        } else {
            Glide.with(context)
                    .load(R.drawable.baseline_avatar_not_active_24)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                    .placeholder(R.drawable.baseline_avatar_not_active_24)// Disable memory caching to reload the image
                    .into(holder.imageViewFriendStatus);
        }

        holder.linearLayoutParent.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.linearLayoutParent);
            popupMenu.inflate(R.menu.go_to_friend_profile);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.itemGoToProfile) {
                    Log.d("linearLayoutParent", "onBindViewHolder: HERE");
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("USER_ID", friend.getId());
                    context.startActivity(intent);
                }
                return true;
            });
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        this.notifyDataSetChanged();
    }

    public void setFilterForList(Map<String, String> params) {
        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                List<User> filterList = this.userList.stream().filter(user -> user.getName().toLowerCase().contains(kw.toLowerCase().trim())).collect(Collectors.toList());
                this.setUserList(filterList);
            } else {
                this.setUserList(originalUserList);
                this.notifyDataSetChanged();
            }
        } else {
            this.setUserList(originalUserList);
            this.notifyDataSetChanged();
        }
    }

    public List<User> getOriginalUserList() {
        return originalUserList;
    }

    public void setOriginalUserList(List<User> originalUserList) {
        this.originalUserList = originalUserList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutParent;
        CircularImageView imageViewFriendAvatar, imageViewFriendStatus;
        TextView textViewFriendName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutParent = itemView.findViewById(R.id.linearLayoutParent);
            imageViewFriendAvatar = itemView.findViewById(R.id.imageViewFriendAvatar);
            imageViewFriendStatus = itemView.findViewById(R.id.imageViewFriendStatus);
            textViewFriendName = itemView.findViewById(R.id.textViewFriendName);

            imageViewFriendStatus.setClickable(false);
            imageViewFriendAvatar.setClickable(false);
            textViewFriendName.setClickable(false);
        }
    }
}
