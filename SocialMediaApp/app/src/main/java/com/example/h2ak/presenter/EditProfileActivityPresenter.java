package com.example.h2ak.presenter;

import android.net.Uri;

import com.example.h2ak.MyApp;
import com.example.h2ak.contract.EditProfileActivityContract;
import com.example.h2ak.database.FirebaseHelper;
import com.example.h2ak.datasource.UserDataSource;
import com.example.h2ak.datasource.datasourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

public class EditProfileActivityPresenter implements EditProfileActivityContract.Presenter {


    private EditProfileActivityContract.View view;
    private UserDataSource userDataSource;
    private FirebaseAuth firebaseAuth;

    public EditProfileActivityPresenter(EditProfileActivityContract.View view) {
        this.view = view;
        userDataSource = new UserDataSourceImpl(MyApp.getInstance());
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = userDataSource.getUserByEmail(firebaseUser.getEmail());
        view.loadingInfomationUser(user);
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void uploadImageToFirebaseCloud(Uri imageUri, String type) {
        StorageReference storageReference = null;
        switch (type) {
            case "avatar" -> storageReference = FirebaseHelper.getImageAvatarStorageRef();
            case "cover" -> storageReference = FirebaseHelper.getImageCoverStorageRef();
        }

        FirebaseHelper.uploadImageToFirebaseCloud(imageUri, storageReference, new FirebaseHelper.OnImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String imageUrl) {
                view.onImageUploadSuccess(imageUrl);
            }

            @Override
            public void onImageUploadFailure(String errorMessage) {
                view.onImageUploadFail(errorMessage);
            }
        });
    }
}
