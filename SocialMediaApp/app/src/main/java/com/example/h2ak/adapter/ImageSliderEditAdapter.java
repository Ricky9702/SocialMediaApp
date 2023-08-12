package com.example.h2ak.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.h2ak.R;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.utils.TextInputLayoutUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ImageSliderEditAdapter extends RecyclerView.Adapter<ImageSliderEditAdapter.ViewHolder> {
    private List<PostImages> insertList;
    private List<PostImages> postImagesList;
    private List<PostImages> deletedList;
    private Context context;

    public ImageSliderEditAdapter(Context context) {
        deletedList = new ArrayList<>();
        postImagesList = new ArrayList<>();
        insertList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_edit_post_images, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PostImages postImages = postImagesList.get(position);

        Glide.with(context)
                .load(postImages.getImageUrl())
                .placeholder(R.color.not_active_icon)
                .error(R.drawable.baseline_report_gmailerrorred_24)
                .into(holder.imageViewSlider);

        holder.textViewDeleteImage.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete image of post")
                    .setMessage("Are you sure want to delete this image?")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {

                        int actualPosition = holder.getAdapterPosition();

                        deletedList.add(postImagesList.get(actualPosition));

                        postImagesList.remove(actualPosition);

                        notifyItemRemoved(actualPosition);

                        notifyItemRangeChanged(actualPosition, postImagesList.size());
                    })
                    .create().show();
        });

    }

    @Override
    public int getItemCount() {
        return postImagesList.size();
    }

    public List<PostImages> getPostImagesList() {
        return postImagesList;
    }

    public void setPostImagesList(List<PostImages> postImagesList) {
        Log.d("TAG", "postImagesList: " + postImagesList.size());
        if (postImagesList != null && !postImagesList.isEmpty()) {
            this.postImagesList = postImagesList;
        } else {
            this.postImagesList.clear();
        }
        this.notifyDataSetChanged();
    }

    public void insertDeletedItems() {
        postImagesList.addAll(deletedList);

        Collections.sort(postImagesList, (postImages1, postImages2) -> {
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

        if (!insertList.isEmpty()) postImagesList.removeAll(insertList);
    }

    private Date parseDateFromString(String dateString) {
        try {
            return TextInputLayoutUtils.simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Return a default date in case of parsing error
        }
    }

    public void addPostImages(PostImages postImages) {
        this.insertList.add(postImages);
        postImagesList.add(postImages);
        this.notifyItemInserted(postImagesList.size());
    }

    public List<PostImages> getInsertList() {
        return insertList;
    }

    public void setInsertList(List<PostImages> insertList) {
        this.insertList = insertList;
    }

    public List<PostImages> getDeletedList() {
        return deletedList;
    }

    public void setDeletedList(List<PostImages> deletedList) {
        this.deletedList = deletedList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDeleteImage;
        ImageView imageViewSlider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDeleteImage = itemView.findViewById(R.id.textViewDeleteImage);
            imageViewSlider = itemView.findViewById(R.id.imageViewSlider);
        }
    }
}
