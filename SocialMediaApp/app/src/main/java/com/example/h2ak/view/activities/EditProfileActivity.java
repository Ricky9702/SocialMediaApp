package com.example.h2ak.view.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h2ak.R;
import com.example.h2ak.adapter.SpinnerGenderAdapter;
import com.example.h2ak.contract.EditProfileActivityContract;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.EditProfileActivityPresenter;
import com.example.h2ak.utils.CameraUtils;
import com.example.h2ak.utils.GalleryUtils;
import com.example.h2ak.utils.ImageSizeValidationUtils;
import com.example.h2ak.utils.PasswordHashing;
import com.example.h2ak.utils.PermissionUtils;
import com.example.h2ak.view.fragments.DatePickerFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class EditProfileActivity extends AppCompatActivity
        implements EditProfileActivityContract.View {

    TextView textViewEditProfilePicture, textViewEditCoverPhoto;
    TextInputEditText editTextName, editTextBio, editTextGender, editTextBirthday, editTextPassword;
    CircularImageView imageViewProfileAvatar;
    ImageView imageViewCoverPhoto;
    ProgressBar progressBar;
    Toolbar toolBar;

    private ActivityResultLauncher<Uri> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    private CameraUtils cameraUtils;
    private GalleryUtils galleryUtils;
    private boolean isChangingProfileAvatar = false;
    private boolean isChanged = false;
    private EditProfileActivityContract.Presenter presenter;
    private Uri avatarUri, coverUri;
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        cameraUtils = new CameraUtils(this);
        galleryUtils = new GalleryUtils();
        presenter = new EditProfileActivityPresenter(this, this);
        presenter.getUser();

        presenter.setUpdatedProfileListener(() -> {
            Intent resultIntent = new Intent();
            if (avatarUri != null)
                resultIntent.putExtra("UPDATED_HAS_AVATAR", avatarUri.toString());
            if (coverUri != null) resultIntent.putExtra("UPDATED_HAS_COVER", coverUri.toString());
            resultIntent.putExtra("UPDATED_DONE", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        toolBar.setOnMenuItemClickListener(item -> {
            if (isChanged) {
                if (avatarUri != null) {
                    presenter.uploadImageToFirebaseCloud(avatarUri, "avatar");
                }

                if (coverUri != null) {
                    presenter.uploadImageToFirebaseCloud(coverUri, "cover");
                }

                User user = new User();

                user.setName(editTextName.getText().toString().trim());
                user.setBio(editTextBio.getText().toString().trim());
                user.setGender(editTextGender.getText().toString().trim());
                user.setBirthday(editTextBirthday.getText().toString().trim());
                user.setPassword(password);

                presenter.updateUser(user);

                Toast.makeText(this, "Update profile successfully!!", Toast.LENGTH_SHORT).show();
            }
            onBackPressed();
            finish();
            return true;
        });

    }

    private void init() {

        //Get view
        textViewEditProfilePicture = findViewById(R.id.textViewEditProfilePicture);
        textViewEditCoverPhoto = findViewById(R.id.textViewEditCoverPhoto);
        editTextName = findViewById(R.id.editTextName);
        editTextBio = findViewById(R.id.editTextBio);
        editTextGender = findViewById(R.id.editTextGender);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        editTextPassword = findViewById(R.id.editTextPassword);
        imageViewProfileAvatar = findViewById(R.id.imageViewProfileAvatar);
        imageViewCoverPhoto = findViewById(R.id.imageViewCoverPhoto);
        progressBar = findViewById(R.id.progressBar);
        toolBar = findViewById(R.id.toolBar);


        //Set EditText focusable true

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setIsChanged(true);
            }
        };

        editTextName.addTextChangedListener(textWatcher);
        editTextBio.addTextChangedListener(textWatcher);
        editTextGender.addTextChangedListener(textWatcher);
        editTextBirthday.addTextChangedListener(textWatcher);


        //Edit toolBar
        toolBar.setTitle("Edit profile");
        toolBar.setTitleTextColor(getColor(R.color.black));
        toolBar.setNavigationIcon(getDrawable(R.drawable.baseline_close_24));
        toolBar.inflateMenu(R.menu.edit_profile_menu_app_bar);

        toolBar.setNavigationOnClickListener(item -> {
            if (!isChanged) {
                onBackPressed();
                finish();
            } else {
                showUnsavedChangeDialog();
            }
        });


        textViewEditProfilePicture.setOnClickListener(view -> {
            isChangingProfileAvatar = true;
            showImagePickDialog();
        });

        textViewEditCoverPhoto.setOnClickListener(view -> {
            isChangingProfileAvatar = false;
            showImagePickDialog();
        });

        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {

            if (result) {
                if (cameraUtils.getCapturedImageUri() != null) {
                    isChanged = true;
                    Uri imageUri = cameraUtils.getCapturedImageUri();
                    if (imageUri != null) handleImageResult(imageUri);
                }
            }
        });

        galleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    isChanged = true;
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) handleImageResult(imageUri);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case PermissionUtils.CAMERA_REQUEST_CODE -> {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    cameraUtils.openCamera(cameraActivityResultLauncher);
                } else {
                    //Permission denied
                    Toast.makeText(this, "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
                }
            }

            case PermissionUtils.STORAGE_REQUEST_CODE -> {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    galleryUtils.openGallery(galleryActivityResultLauncher);
                } else {
                    //Permission denied
                    Toast.makeText(this, "Please enable storage permissions", Toast.LENGTH_SHORT).show();
                }
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void showUnsavedChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unsaved changes");
        builder.setMessage("You have unsaved changes. Are you sure you want to cancel?");

        builder.setPositiveButton("YES", (dialogInterface, i) -> {
            onBackPressed();
            finish();
        });

        builder.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showImagePickDialog() {
        // Check if camera and storage permissions are granted
        boolean hasCameraPermission = PermissionUtils.checkCameraPermission(this);
        boolean hasStoragePermission = PermissionUtils.checkStoragePermission(this);

        // Build dialog for choosing camera or gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your image");

        // Set the dialog items based on permissions
        String[] dialogItems;
        if (hasCameraPermission && hasStoragePermission) {
            dialogItems = new String[]{"Camera", "Gallery"};
        } else if (hasCameraPermission) {
            dialogItems = new String[]{"Camera"};
        } else if (hasStoragePermission) {
            dialogItems = new String[]{"Gallery"};
        } else {
            // Both permissions are missing
            PermissionUtils.requestCameraPermission(this);
            PermissionUtils.requestStoragePermission(this);
            return;
        }

        builder.setItems(dialogItems, (dialogInterface, i) -> {
            String selectedItem = dialogItems[i];
            if (selectedItem.equals("Camera")) {
                if (hasCameraPermission) {
                    cameraUtils.openCamera(cameraActivityResultLauncher);
                } else {
                    PermissionUtils.requestCameraPermission(this);
                }
            } else if (selectedItem.equals("Gallery")) {
                if (hasStoragePermission) {
                    galleryUtils.openGallery(galleryActivityResultLauncher);
                } else {
                    PermissionUtils.requestStoragePermission(this);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showEditFieldDialog(String title, View view, OnEditChangeListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(55, 0, 55, 0);

        layout.addView(view, params);

        builder.setView(layout);

        builder.setPositiveButton("CHANGE", (dialogInterface, i) -> {

            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                listener.onTextChanged(editText.getText().toString().trim());
            } else if (view instanceof Spinner) {
                Spinner spinner = (Spinner) view;
                listener.onTextChanged(spinner.getSelectedItem().toString());
            }

        });

        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            // Close the dialog without performing any action
            dialogInterface.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    interface OnEditChangeListener {
        void onTextChanged(String text);

    }


    private void handleImageResult(Uri imageUri) {
        Log.d("TEST IMAGE", imageUri + "");
        if (isChangingProfileAvatar) {
            if (ImageSizeValidationUtils.checkImageSize(this, imageUri, ImageSizeValidationUtils.ImageType.AVATAR)) {
                avatarUri = imageUri;
                Glide.with(this)
                        .load((avatarUri))
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable caching to reload the image
                        .skipMemoryCache(true)
                        .placeholder(R.color.not_active_icon)// Disable memory caching to reload the image
                        .into(imageViewProfileAvatar);
            } else {
                Toast.makeText(this, "Image too small, please choose a larger image!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (ImageSizeValidationUtils.checkImageSize(this, imageUri, ImageSizeValidationUtils.ImageType.COVER)) {
                coverUri = imageUri;
                Glide.with(this)
                        .load((coverUri))
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable caching to reload the image
                        .skipMemoryCache(true)
                        .placeholder(R.color.not_active_icon)// Disable memory caching to reload the image
                        .into(imageViewCoverPhoto);
            } else {
                Toast.makeText(this, "Image too small, please choose a larger image!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setIsChanged(boolean changed) {
        this.isChanged = changed;
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean flag) {
        if (flag)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onImageUploadSuccess(String imageUrl, String type) {
        User user = new User();
        if (type.equals("avatar")) {
            user.setImageAvatar(imageUrl);
        } else {
            user.setImageCover(imageUrl);
        }
        presenter.updateUser(user);
    }

    @Override
    public void onImageUploadFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfirmPasswordFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfirmPasswordSuccessful(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
            editTextPassword.setText(PasswordHashing.hashPassword(password));
        }
    }

    @Override
    public void loadingInformationUser(User user) {
        if (user != null) {

            //get data
            String name = user.getName();
            String bio = user.getBio();
            String gender = user.getGender();
            String birthday = user.getBirthday();
            String avatar = user.getImageAvatar();
            String cover = user.getImageCover();


            //set data
            editTextName.setText(name);
            editTextGender.setText(gender);
            editTextBirthday.setText(birthday);
            editTextBio.setText(bio == null || bio.isEmpty() ? " " : bio);
            editTextPassword.setText(user.getPassword());

            if (avatar != null && !avatar.isEmpty()) {
                textViewEditProfilePicture.setText("Edit");
                Glide.with(this)
                        .load(avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.not_active_icon)// Disable memory caching to reload the image
                        .into(imageViewProfileAvatar);

            } else {
                textViewEditProfilePicture.setText("Add");
                imageViewProfileAvatar.setImageResource(R.drawable.baseline_avatar_place_holder);
            }

            if (cover != null && !cover.isEmpty()) {
                textViewEditCoverPhoto.setText("Edit");
                Glide.with(this)
                        .load(cover)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.not_active_icon)// Disable memory caching to reload the image
                        .into(imageViewCoverPhoto);

            } else {
                textViewEditCoverPhoto.setText("Add");
                imageViewCoverPhoto.setImageResource(R.color.not_active_icon);
            }

            //Create dialogue for each field
            editTextName.setOnClickListener(view -> {
                EditText editText = new EditText(this);
                editText.setText(editTextName.getText().toString().trim());
                showEditFieldDialog("Edit Name", editText, text -> {
                    if (text != null && !text.isEmpty()) {
                        editTextName.setText(text);
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Name can be not empty!!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            editTextBio.setOnClickListener(view -> {
                EditText editText = new EditText(this);
                editText.setText(editTextBio.getText().toString().trim());
                showEditFieldDialog("Edit Bio", editText, text -> {
                    if (text != null && !text.isEmpty()) {
                        editTextBio.setText(text);
                    } else {
                        editTextBio.setText("   ");
                    }
                });
            });

            editTextGender.setOnClickListener(view -> {
                //Create adpater spinner
                String[] options = new String[]{"Male", "Female", "Other"};
                SpinnerGenderAdapter adapter = new SpinnerGenderAdapter(this, options);

                //Create spinner
                Spinner spinner = new Spinner(this);
                spinner.setAdapter(adapter);
                int selectedIndex = Arrays.asList(options).indexOf(editTextGender.getText().toString().trim());

                //Set the selection default as gender of user
                spinner.setSelection(selectedIndex);

                showEditFieldDialog("Edit gender", spinner, text -> editTextGender.setText(text));
            });

            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setTextInputEditText(editTextBirthday);
            editTextBirthday.setOnClickListener(view -> {
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            });
            datePickerFragment.setValidateAge(true);
            datePickerFragment.setListener(new DatePickerFragment.onDateRecieveListener() {
                @Override
                public void onDateValidation(String date) {
                    editTextBirthday.setText(date);
                }

                @Override
                public void onDateNotValidation(String errorMsg) {
                    Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });

            editTextPassword.setOnClickListener(view -> {

                View customLayout = getLayoutInflater().inflate(R.layout.custom_layout_edit_password, null);

                EditText editTextCurrentPassword = customLayout.findViewById(R.id.editTextCurrentPassword);
                EditText editTextNewPassword = customLayout.findViewById(R.id.editTextNewPassword);
                EditText editTextConfirmPassword = customLayout.findViewById(R.id.editTextConfirmPassword);


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Edit password");
                builder.setView(customLayout);

                builder.setPositiveButton("CHANGE", (dialogInterface, i) -> {
                    String currentPassword = editTextCurrentPassword.getText().toString().trim();
                    String newPassword = editTextNewPassword.getText().toString().trim();
                    String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                    presenter.confirmPasswordChange(currentPassword, newPassword, confirmPassword);
                });

                builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

                builder.create().show();

            });

            // init is changed when on load info
            setIsChanged(false);
        }
    }
}