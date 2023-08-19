package com.example.h2ak.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.contract.EditProfileActivityContract;
import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.PasswordHashing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

public class EditProfileActivityPresenter implements EditProfileActivityContract.Presenter{


    private EditProfileActivityContract.View view;
    private UserDataSource userDataSource;
    private FirebaseAuth firebaseAuth;
    private User currentUser;
    private Context context;
    private EditProfileActivityContract.UpdatedProfileListener listener;
    private OnUpdatePasswordListener onUpdatePasswordListener;

    public EditProfileActivityPresenter(EditProfileActivityContract.View view, Context context) {
        this.context = context;
        this.view = view;
        userDataSource = UserDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void setUpdatedProfileListener(EditProfileActivityContract.UpdatedProfileListener listener) {
        this.listener = listener;
    }


    @Override
    public void getUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = userDataSource.getUserById(firebaseUser.getUid());
        this.currentUser = user;
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
            firebaseAuth.getCurrentUser().updatePassword(user.getPassword()).addOnSuccessListener(task -> {
                onUpdatePasswordListener.updateStatus(true);

            }).addOnFailureListener(runnable -> {
                Log.d("Update firebase password", runnable.getMessage());
                view.showMessage(runnable.getMessage());
            });
        }

        this.setOnUpdatePasswordListener(new OnUpdatePasswordListener() {
            @Override
            public void updateStatus(boolean flag) {
                if (flag) {
                    currentUser.setPassword(PasswordHashing.hashPassword(user.getPassword()));
                    if (userDataSource.updateCurrentUser(currentUser)) {
                        Log.d("Listener is null?", listener == null ? "NULL" : "NOT NULL");
                        if (listener != null)
                            listener.onProfileUpdated();
                    }
                }
            }
        });

        if (userDataSource.updateCurrentUser(currentUser)) {
            Log.d("Listener is null?", listener == null ? "NULL" : "NOT NULL");
            if (listener != null)
                listener.onProfileUpdated();
        }

        view.showProgressbar(false);
    }

    public OnUpdatePasswordListener getOnUpdatePasswordListener() {
        return onUpdatePasswordListener;
    }

    public void setOnUpdatePasswordListener(OnUpdatePasswordListener onUpdatePasswordListener) {
        this.onUpdatePasswordListener = onUpdatePasswordListener;
    }

    public interface OnUpdatePasswordListener {
        void updateStatus(boolean flag);
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

        StorageReference storageReference = null;
        switch (type) {
            case "avatar" -> {
                storageReference = FirebaseHelper.getImageAvatarStorageRef();
            }
            case "cover" -> {
                storageReference = FirebaseHelper.getImageCoverStorageRef();
            }
        }

        FirebaseHelper.uploadImageToFirebaseCloud(context, imageUri, storageReference, new FirebaseHelper.OnImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String imageUrl) {
                Log.d("TEST IMAGE URL", imageUrl);
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
