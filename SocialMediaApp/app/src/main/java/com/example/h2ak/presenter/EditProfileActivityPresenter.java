package com.example.h2ak.presenter;

import android.net.Uri;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.contract.EditProfileActivityContract;
import com.example.h2ak.database.FirebaseHelper;
import com.example.h2ak.datasource.UserDataSource;
import com.example.h2ak.datasource.datasourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.PasswordHashing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

public class EditProfileActivityPresenter implements EditProfileActivityContract.Presenter {


    private EditProfileActivityContract.View view;
    private UserDataSource userDataSource;
    private FirebaseAuth firebaseAuth;
    private User currentUser;

    public EditProfileActivityPresenter(EditProfileActivityContract.View view) {
        this.view = view;
        userDataSource = new UserDataSourceImpl(MyApp.getInstance());
        firebaseAuth = FirebaseAuth.getInstance();
        this.currentUser = userDataSource.getUserByEmail(firebaseAuth.getCurrentUser().getEmail());
    }

    @Override
    public void getUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = userDataSource.getUserByEmail(firebaseUser.getEmail());
        view.loadingInformationUser(user);
    }

    @Override
    public void updateUser(User user) {
        view.showProgressbar(true);
        if (user.getName() != null && !user.getName().isEmpty()) {
            currentUser.setName(user.getName());
        }
        if (user.getBio() != null) {
            currentUser.setBio(user.getBio());
        }
        if (user.getGender() != null && !user.getGender().isEmpty()) {
            currentUser.setGender(user.getGender());
        }
        if (user.getBirthday() != null && !user.getBirthday().isEmpty()) {
            currentUser.setBirthday(user.getBirthday());
        }
        if (user.getImageAvatar() != null && !user.getImageAvatar().isEmpty()) {
            currentUser.setImageAvatar(user.getImageAvatar());
        }
        if (user.getImageCover() != null && !user.getImageCover().isEmpty()) {
            currentUser.setImageCover(user.getImageCover());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty() && !currentUser.getPassword().equals(user.getPassword())) {
            currentUser.setPassword(user.getPassword().trim());
            firebaseAuth.getCurrentUser().updatePassword(user.getPassword()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("AFTER SUCCESS FUL ", user.getPassword());
                }
            }).addOnFailureListener(runnable -> {
                view.showMessage(runnable.getMessage());
            });
        }

        if (userDataSource.updateUser(currentUser)) {
            view.showMessage("Edit profile successfully!!");
        } else {
            view.showMessage("There is some errors while editing");
        }
        view.showProgressbar(false);
    }

    @Override
    /**
     * Uploads the image to Firebase Cloud Storage.
     *
     * @param imageUri The URI of the image to be uploaded.
     * @param type The type of the image ("avatar" or "cover").
     */
    public void uploadImageToFirebaseCloud(Uri imageUri, String type) {

        //Validate if the user do not change image
        String currentImageUrl = null;
        String newImageUrl = imageUri.toString();

        StorageReference storageReference = null;
        switch (type) {
            case "avatar" -> {
                currentImageUrl = currentUser.getImageAvatar();
                storageReference = FirebaseHelper.getImageAvatarStorageRef();

            }
            case "cover" -> {
                currentImageUrl = currentUser.getImageCover();
                storageReference = FirebaseHelper.getImageCoverStorageRef();
            }
        }

        if (currentImageUrl != null && currentImageUrl.equals(newImageUrl)) {
            return;
        }

        FirebaseHelper.uploadImageToFirebaseCloud(imageUri, storageReference, new FirebaseHelper.OnImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String imageUrl) {
                view.onImageUploadSuccess(imageUrl, type);
            }

            @Override
            public void onImageUploadFailure(String errorMessage) {
                view.onImageUploadFail(errorMessage);
            }
        });
    }

    @Override
    public void confirmPasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        String hashedPassword = currentUser.getPassword().trim();
        if (currentPassword.isEmpty()) {
            view.onConfirmPasswordFail("Current password can be not empty!!");
        } else if (newPassword.isEmpty()) {
            view.onConfirmPasswordFail("New password can be not empty!!");
        } else if (confirmPassword.isEmpty()) {
            view.onConfirmPasswordFail("Confirm password can be not empty!!");
        } else if (newPassword.length() < 6) {
            view.onConfirmPasswordFail("Password length limit is 6!!");
        } else if (currentPassword.equals(newPassword)) {
            view.onConfirmPasswordFail("New password is the same to current password!!");
        } else if (!newPassword.equals(confirmPassword)) {
            view.onConfirmPasswordFail("Please check your confirm password!!");
        } else if (!PasswordHashing.verifyPassword(currentPassword, hashedPassword)) {
            view.onConfirmPasswordFail("Please check your current password!!");
        }  else {
            view.onConfirmPasswordSuccessful(confirmPassword);
        }
    }
}
