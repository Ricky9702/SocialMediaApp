package com.example.h2ak.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.SearchHistoryDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SearchHistoryDataSource;
import com.example.h2ak.contract.SearchFragmentContract;
import com.example.h2ak.model.SearchHistory;
import com.example.h2ak.model.User;
import com.example.h2ak.view.activities.UserProfileActivity;
import com.example.h2ak.view.fragments.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private List<User> userList = new ArrayList<>();
    private Context context;
    private SearchHistoryDataSource searchHistoryDataSource;
    private SearchFragmentContract.View view;

    public SearchHistoryAdapter(Context context, SearchFragmentContract.View view) {
        this.context = context;
        searchHistoryDataSource = SearchHistoryDataSourceImpl.getInstance(context);
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_search_history_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("SearchHistoryAdapter", "onBindViewHolder: " + position +"");
        User searchingUser = userList.get(position);

        // set avatar
        Glide.with(context)
                .load(searchingUser.getImageAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                .placeholder(R.drawable.baseline_avatar_place_holder)// Disable memory caching to reload the image
                .into(holder.imageViewSearchingUserAvatar);
        // set name
        holder.textViewSearchingUserName.setText(searchingUser.getName());

        // set view profile on click
        holder.linearLayoutParent.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("USER_ID", searchingUser.getId());
            context.startActivity(intent);
        });


        // set on click on delete
        holder.btnDelete.setOnClickListener(view -> {

            // delete in ui
            int actualPosition = holder.getAdapterPosition();
            userList.remove(actualPosition);
            notifyItemRemoved(actualPosition);
            notifyItemRangeChanged(actualPosition, userList.size());

            if (userList.isEmpty()) {
                this.view.onDeleteAllSearchHistory();
            }


            // delete in database
            SearchHistory searchHistory = searchHistoryDataSource.findSearchHistory(MyApp.getInstance().getCurrentUser(), searchingUser);
            if (searchHistoryDataSource.deleteSearchHistory(searchHistory, false)) {
                Log.d("SearchHistoryAdapter", "onBindViewHolder: Delete success" );
            } else {
                Log.d("SearchHistoryAdapter", "onBindViewHolder: Delete failed" );
            }

        });

    }

    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
        }
        else return 0;
    }


    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutParent;
        CircularImageView imageViewSearchingUserAvatar;
        TextView textViewSearchingUserName;
        ImageButton btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutParent = itemView.findViewById(R.id.linearLayoutParent);
            imageViewSearchingUserAvatar = itemView.findViewById(R.id.imageViewSearchingUserAvatar);
            textViewSearchingUserName = itemView.findViewById(R.id.textViewSearchingUserName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
