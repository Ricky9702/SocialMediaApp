package com.example.h2ak.contract;

import com.example.h2ak.adapter.CreatePostChooseImageGridAdapter;

import java.util.List;
import java.util.Map;

public interface CreatePostChooseImageFragmentContract {

    interface View {

        void onMapImageSelected(Map<Integer, CreatePostChooseImageGridAdapter.ImageSelectionInfo> listImageUrl);


        void onRecievedRecentImages(List<String> listImageUrl);
    }

    interface Presenter {
        void loadRecentImagesFromDevice();
    }
}
