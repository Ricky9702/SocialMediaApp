package com.example.h2ak.contract;

import android.net.Uri;

import com.example.h2ak.model.User;

public interface EditProfileActivityContract {

    interface View{
        void loadingInfomationUser(User user);
        void showProgressbar();
        void onImageUploadSuccess(String imageUrl);
        void onImageUploadFail(String errorMsg);
    }

    interface Presenter{
        void getUser();
        void updateUser(User user);
        void uploadImageToFirebaseCloud(Uri imageUri, String type);
    }

}
