package com.example.h2ak.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.h2ak.R;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ViewHolder> {

    private List<String> listSelectedImageUrl = new ArrayList<>();
    Context context;

    public ImageSliderAdapter (Context content) {
        this.context = content;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_image_slider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String selectedImageUrl = listSelectedImageUrl.get(position);
        if (this.listSelectedImageUrl.size() > 1) {
            holder.textViewImageSliderCount.setVisibility(View.VISIBLE);
            holder.textViewImageSliderCount.setText(String.format("%d/%d", position+1, this.listSelectedImageUrl.size()));

        } else {
            holder.textViewImageSliderCount.setVisibility(View.GONE);
        }



        holder.imageViewSlider.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .load(selectedImageUrl)
                .placeholder(R.color.not_active_icon)
                .error(R.drawable.baseline_report_gmailerrorred_24)
                .into(holder.imageViewSlider);

    }

    @Override
    public int getItemCount() {
        return listSelectedImageUrl.size();
    }

    public List<String> getListSelectedImageUrl() {
        return listSelectedImageUrl;
    }

    public void setListSelectedImageUrl(List<String> listSelectedImageUrl) {
        if (listSelectedImageUrl == null) {
            this.listSelectedImageUrl.clear();
        } else {
            Log.d("setListSelectedImageUrl", listSelectedImageUrl.size()+"");
            this.listSelectedImageUrl = listSelectedImageUrl;
        }
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayoutSlider;
        TextView textViewImageSliderCount;
        ImageView imageViewSlider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayoutSlider = itemView.findViewById(R.id.relativeLayoutSlider);
            textViewImageSliderCount = itemView.findViewById(R.id.textViewImageSliderCount);
            imageViewSlider = itemView.findViewById(R.id.imageViewSlider);

        }
    }
}
