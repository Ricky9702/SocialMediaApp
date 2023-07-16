package com.example.h2ak.view.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.h2ak.R;
import com.example.h2ak.controllers.UserController;
import com.example.h2ak.utils.PermissionUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.security.Permission;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {
    private UserController userController;
    private ActivityResultLauncher<Uri> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;


    private boolean isChanged = false;

    {
        userController = new UserController();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();


    }

    private void init() {

        //Get view
        TextView textViewEditProfilePicture = findViewById(R.id.textViewEditProfilePicture);
        TextView textViewEditCoverPhoto = findViewById(R.id.textViewEditCoverPhoto);
        TextView textViewEditProfileInfo = findViewById(R.id.textViewEditProfileInfo);
        TextInputLayout textLayoutName = findViewById(R.id.textLayoutName);
        TextInputLayout textLayoutBio = findViewById(R.id.textLayoutBio);
        TextInputLayout textLayoutEmai = findViewById(R.id.textLayoutEmail);
        TextInputLayout textLayoutPhone = findViewById(R.id.textLayoutPhone);
        CircularImageView imageViewProfileAvatar = findViewById(R.id.imageViewProfileAvatar);
        ImageView imageViewCoverPhoto = findViewById(R.id.imageViewCoverPhoto);


        //Set EditText focusable true

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is being changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                setIsChanged(true);
            }
        };
        textLayoutName.getEditText().addTextChangedListener(textWatcher);
        textLayoutBio.getEditText().addTextChangedListener(textWatcher);
        textLayoutEmai.getEditText().addTextChangedListener(textWatcher);
        textLayoutPhone.getEditText().addTextChangedListener(textWatcher);

        textViewEditProfileInfo.setOnClickListener(view -> {
            textLayoutName.getEditText().setFocusable(true);
            textLayoutName.getEditText().setFocusableInTouchMode(true);
            textLayoutName.getEditText().requestFocus();

            textLayoutBio.getEditText().setFocusable(true);
            textLayoutBio.getEditText().setFocusableInTouchMode(true);

            textLayoutEmai.getEditText().setFocusable(true);
            textLayoutEmai.getEditText().setFocusableInTouchMode(true);

            textLayoutPhone.getEditText().setFocusable(true);
            textLayoutPhone.getEditText().setFocusableInTouchMode(true);
        });


        //Edit toolBar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("Edit profile");
        toolbar.setTitleTextColor(getColor(R.color.black));
        toolbar.setNavigationIcon(getDrawable(R.drawable.baseline_close_24));
        toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);

        toolbar.setOnMenuItemClickListener(view -> {
            onBackPressed();
            finish();
            return true;
        });

        toolbar.setNavigationOnClickListener(item -> {
            if (!isChanged) {
                onBackPressed();
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Unsaved changes");
                builder.setMessage("You have unsaved changes. Are you sure you want to cancel?");

                builder.setPositiveButton("YES", (dialogInterface, i) -> {
                    onBackPressed();
                    finish();
                });

                builder.setNegativeButton("NO", (dialogInterface, i) -> {
                    // Close the dialog without performing any action
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
           if (result) {
               requestCameraPermission();
               requestStoragePermission();
           }
        });

        galleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Handle the gallery activity result here
                        if (result.getData() != null) {
                            // Retrieve the selected image from the result.getData() if necessary
                            // For example, you can get the image URI using result.getData().getData()
                        }
                    }
                }
        );

        textViewEditProfilePicture.setOnClickListener(view -> {
            selectFromGallery();
        });


        userController.getUser(user -> {
            if (user != null) {
                //get data
                String id = user.getId();
                String email = user.getEmail();
                String name = user.getName();
                Date createdDate = user.getCreatedDate();
                String avatar = user.getAvatar();
                String phone = user.getPhone();
                //set data
                textLayoutName.getEditText().setText(name);
                textLayoutEmai.getEditText().setText(email);
                textLayoutPhone.getEditText().setText(phone);

                if (avatar != null && !avatar.isEmpty()) {
                    textViewEditProfilePicture.setText("Edit");
                    Picasso.get().load(avatar).into(imageViewProfileAvatar);

                } else {
                    textViewEditProfilePicture.setText("Add");
                    Drawable placeholderDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_person_24);
                    Picasso.get().load(R.drawable.baseline_person_24).placeholder(placeholderDrawable).into(imageViewProfileAvatar);
                }
                setIsChanged(false);
            }
        });

    }

    private void requestStoragePermission() {
        PermissionUtils.requestStoragePermission(this);
    }

    private void requestCameraPermission() {
        PermissionUtils.requestCameraPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PermissionUtils.CAMERA_REQUEST_CODE -> {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    selectFromCamera();
                } else {
                    //Permission denied
                    Toast.makeText(this, "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
                }
            }

            case PermissionUtils.STORAGE_REQUEST_CODE -> {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    selectFromGallery();
                } else {
                    //Permission denied
                    Toast.makeText(this, "Please enable storage permissions", Toast.LENGTH_SHORT).show();
                }
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void selectFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryActivityResultLauncher.launch(galleryIntent);
    }

    private void selectFromCamera() {
        //provide metadata for image
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //Insert image to media store
        Uri imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        //Create intent, using camera app
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        cameraActivityResultLauncher.launch(imageUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (resultCode == PermissionUtils.IMAGE_PICK_CAMERA_CODE) {

            }

            if (resultCode == PermissionUtils.IMAGE_PICK_GALLERY_CODE) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void setIsChanged(boolean changed) {
        this.isChanged = changed;
    }

}