package com.example.h2ak.contract;

import android.net.Uri;

import com.example.h2ak.model.User;

public interface EditProfileActivityContract {

    interface View{
        void loadingInformationUser(User user);
        void showMessage(String message);
        void showProgressbar(boolean flag);
        void onImageUploadSuccess(String imageUrl, String type);
        void onImageUploadFail(String errorMsg);
        void onConfirmPasswordFail(String errorMsg);
        void onConfirmPasswordSuccessful(String password);
    }

    interface Presenter{
        void getUser();
        void updateUser(User user);
        void uploadImageToFirebaseCloud(Uri imageUri, String type);
        void setUpdatedProfileListener(EditProfileActivityContract.UpdatedProfileListener listener);
        void confirmPasswordChange(String currentPassword, String newPassword, String confirmPassword);
    }

     interface UpdatedProfileListener {
        void onProfileUpdated();
    }

}
