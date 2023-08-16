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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.User;
import com.example.h2ak.view.activities.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DisplayFriendAdapter extends RecyclerView.Adapter<DisplayFriendAdapter.ViewHolder> {
    private FriendShipDataSource friendShipDataSource;
    private List<User> friends;
    private User currentUser;
    Context context;
    private FirebaseAuth firebaseAuth;
    private UserDataSource userDataSource;
    public DisplayFriendAdapter(Context context) {
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
        this.context = context;
        friends = new ArrayList<>();
        currentUser = null;
        firebaseAuth = FirebaseAuth.getInstance();
        userDataSource = UserDataSourceImpl.getInstance(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_display_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User friend = friends.get(position);

        if (friend.getImageAvatar() != null && !friend.getImageAvatar().isEmpty()) {
            Glide.with(context)
                    .load(friend.getImageAvatar())
                    .placeholder(R.drawable.baseline_avatar_place_holder)
                    .error(R.drawable.baseline_report_gmailerrorred_24)
                    .into(holder.imageViewFriendAvatar);
        } else {
            Glide.with(context)
                    .load(R.drawable.baseline_avatar_place_holder)
                    .placeholder(R.drawable.baseline_avatar_place_holder)
                    .error(R.drawable.baseline_report_gmailerrorred_24)
                    .into(holder.imageViewFriendAvatar);
        }

        holder.textViewFriendName.setText(friend.getName());

        int imageSize = calculateImageSize();
        holder.imageViewFriendAvatar.getLayoutParams().width = imageSize - (int)(0.1*imageSize);
        holder.imageViewFriendAvatar.getLayoutParams().height = imageSize - (int)(0.1*imageSize);

        holder.textViewFriendName.setWidth(imageSize - (int)(0.2*imageSize));

        holder.relativeLayoutParent.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("USER_ID", friend.getId());
            context.startActivity(intent);
        });
        int friendsSize = 0;

        if (currentUser != null) {
            friendsSize = friendShipDataSource.getMutualFriends(currentUser, friend).size();
        } else {

            friendsSize = friendShipDataSource.getMutualFriends(userDataSource.getUserById(firebaseAuth.getCurrentUser().getUid()), friend).size();
        }


        if (friendsSize > 0) {
            holder.textViewMutualFriends.setText(friendsSize + " mutal friends");
        } else {
            holder.textViewMutualFriends.setText("");
        }


    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageViewFriendAvatar;
        TextView textViewFriendName, textViewMutualFriends;
        RelativeLayout relativeLayoutParent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewFriendAvatar = itemView.findViewById(R.id.imageViewFriendAvatar);
            textViewMutualFriends = itemView.findViewById(R.id.textViewMutualFriends);
            textViewFriendName = itemView.findViewById(R.id.textViewFriendName);
            relativeLayoutParent =itemView.findViewById(R.id.relativeLayoutParent);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private int calculateImageSize() {
        // Calculate the square size based on the screen width and desired grid spacing
        // You can adjust the calculations based on your layout requirements
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int spacing = (int) (1 * displayMetrics.density); // Adjust spacing as needed
        int availableWidth = screenWidth - (spacing * (3 - 1));
        int imageSize = availableWidth / 3;
        return imageSize;
    }
}
