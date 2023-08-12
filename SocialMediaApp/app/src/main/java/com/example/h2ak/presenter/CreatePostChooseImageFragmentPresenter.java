package com.example.h2ak.presenter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.h2ak.contract.CreatePostChooseImageFragmentContract;

import java.util.ArrayList;
import java.util.List;

public class CreatePostChooseImageFragmentPresenter implements CreatePostChooseImageFragmentContract.Presenter {

    private CreatePostChooseImageFragmentContract.View view;
    Context context;

    public CreatePostChooseImageFragmentPresenter(CreatePostChooseImageFragmentContract.View view, Context context) {
        this.context = context;
        this.view = view;
    }


    @Override
    public void loadRecentImagesFromDevice() {
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder);

        List<String> imagePaths = new ArrayList<>();

        if (cursor != null) {
            int columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                long imageId = cursor.getLong(columnIndexId);
                String imagePath = cursor.getString(columnIndexData);
                imagePaths.add(imagePath);
            }
            view.onRecievedRecentImages(imagePaths);
            cursor.close();
        }
    }
}

