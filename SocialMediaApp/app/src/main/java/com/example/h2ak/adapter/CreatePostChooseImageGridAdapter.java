package com.example.h2ak.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.h2ak.R;
import com.example.h2ak.contract.CreatePostChooseImageFragmentContract;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreatePostChooseImageGridAdapter extends BaseAdapter {

    private List<String> listImageUrl = new ArrayList<>();
    private CreatePostChooseImageFragmentContract.View view;
    Context context;
    private boolean allowMultipleImages = false;
    private Map<Integer, ImageSelectionInfo> selectionInfoMap = new LinkedHashMap<>();

    public CreatePostChooseImageGridAdapter(Context context, CreatePostChooseImageFragmentContract.View view) {
        this.view = view;
        this.context = context;
    }

    @Override
    public int getCount() {
        return getListImageUrl().size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View parent = LayoutInflater.from(context).inflate(R.layout.custom_loading_recent_images, viewGroup, false);

        ImageView imageViewRecentImage = parent.findViewById(R.id.imageViewRecentImage);

        TextView textViewIndicator = parent.findViewById(R.id.textViewIndicator);

        int imageSize = calculateImageSize();
        imageViewRecentImage.getLayoutParams().width = imageSize;
        imageViewRecentImage.getLayoutParams().height = imageSize;

        imageViewRecentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(context)
                .load(getListImageUrl().get(i))
                .placeholder(R.color.not_active_icon)
                .error(R.drawable.baseline_report_gmailerrorred_24)
                .into(imageViewRecentImage);


        // if the user selected multiple images
        if (isAllowMultipleImages()) {
            // change the ui with multiple images
            if (selectionInfoMap.containsKey(i)) {
                ImageSelectionInfo imageSelectionInfo = selectionInfoMap.get(i);
                imageSelectionInfo.setImageUrl(getListImageUrl().get(i));
                textViewIndicator.setText(imageSelectionInfo.id > 0 ? String.valueOf(imageSelectionInfo.id) : "");
                textViewIndicator.setBackgroundResource(R.drawable.circle_background_select_image_active);
            } else {
                textViewIndicator.setText("");
                textViewIndicator.setBackgroundResource(R.drawable.circle_background_select_image_not_active);
            }
            textViewIndicator.setVisibility(View.VISIBLE);

        } else {
            textViewIndicator.setVisibility(View.GONE);
            selectionInfoMap.clear();
        }


        parent.setOnClickListener(view1 -> {
            if (!selectionInfoMap.containsKey(i)) {
                selectionInfoMap.put(i, new ImageSelectionInfo());
                selectionInfoMap.get(i).id = selectionInfoMap.size();
                selectionInfoMap.get(i).imageUrl = listImageUrl.get(i);
            } else {
                if (allowMultipleImages) {
                    // the user double click on selected image, remove it
                    selectionInfoMap.remove(i);
                    // Update IDs for remaining items
                    int size = selectionInfoMap.size();
                    int id = 1;
                    for (int key : selectionInfoMap.keySet()) {
                        if (id > size)
                            break;
                        selectionInfoMap.get(key).id = id;
                        id++;
                    }
                }
            }
            // ask view load image
            this.view.onMapImageSelected(selectionInfoMap);
            this.notifyDataSetChanged();
        });
        return parent;
    }

    public List<String> getListImageUrl() {
        return listImageUrl;
    }

    public void setListImageUrl(List<String> listImageUrl) {
        this.listImageUrl = listImageUrl;
        this.notifyDataSetChanged();
    }

    private int calculateImageSize() {
        // Calculate the square size based on the screen width and desired grid spacing
        // You can adjust the calculations based on your layout requirements
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int spacing = (int) (1 * displayMetrics.density); // Adjust spacing as needed
        int availableWidth = screenWidth - (spacing * (4 - 1));
        int imageSize = availableWidth / 4;
        return imageSize;
    }

    public boolean isAllowMultipleImages() {
        return allowMultipleImages;
    }

    public void setAllowMultipleImages(boolean allowMultipleImages) {
        this.allowMultipleImages = allowMultipleImages;
        this.notifyDataSetChanged();
    }

    public class ImageSelectionInfo {
        private String imageUrl = "";
        private int id;

        {
            id = 1;
        }

        public ImageSelectionInfo() {
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

}
