package com.example.h2ak.view.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.h2ak.R;
import com.example.h2ak.adapter.SpinnerGenderAdapter;
import com.example.h2ak.contract.EditProfileActivityContract;
import com.example.h2ak.database.FirebaseHelper;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.BaseMenuPresenter;
import com.example.h2ak.presenter.EditProfileActivityPresenter;
import com.example.h2ak.utils.CameraUtils;
import com.example.h2ak.utils.GalleryUtils;
import com.example.h2ak.utils.PermissionUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class EditProfileActivity extends AppCompatActivity implements EditProfileActivityContract.View {
    TextView textViewEditProfilePicture, textViewEditCoverPhoto, textViewEditProfileInfo, textViewEditPersonalPrivacy;
    TextInputEditText editTextName, editTextBio, editTextGender, editTextBirthday, editTextPassword;
    CircularImageView imageViewProfileAvatar;
    ImageView imageViewCoverPhoto;

    private ActivityResultLauncher<Uri> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    private CameraUtils cameraUtils;
    private GalleryUtils galleryUtils;
    private boolean isChangingProfilePicture = false;
    private boolean isChanged = false;
    private EditProfileActivityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();

        cameraUtils = new CameraUtils(this);
        galleryUtils = new GalleryUtils();
        presenter = new EditProfileActivityPresenter(this);
        presenter.getUser();
    }

    private void init() {

        //Get view
        textViewEditProfilePicture = findViewById(R.id.textViewEditProfilePicture);
        textViewEditCoverPhoto = findViewById(R.id.textViewEditCoverPhoto);
        textViewEditProfileInfo = findViewById(R.id.textViewEditProfileInfo);
        editTextName = findViewById(R.id.editTextName);
        editTextBio = findViewById(R.id.editTextBio);
        editTextGender = findViewById(R.id.editTextGender);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        editTextPassword = findViewById(R.id.editTextPassword);
        imageViewProfileAvatar = findViewById(R.id.imageViewProfileAvatar);
        imageViewCoverPhoto = findViewById(R.id.imageViewCoverPhoto);


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

        textViewEditProfileInfo.setOnClickListener(view -> {
            editTextName.setEnabled(true);
            editTextBio.setEnabled(true);
            editTextGender.setEnabled(true);
            editTextBirthday.setEnabled(true);
        });

        //Edit toolBar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("Edit profile");
        toolbar.setTitleTextColor(getColor(R.color.black));
        toolbar.setNavigationIcon(getDrawable(R.drawable.baseline_close_24));
        toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);

        toolbar.setOnMenuItemClickListener(view -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            return true;
        });

        toolbar.setNavigationOnClickListener(item -> {
            if (!isChanged) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            } else {
                showUnsavedChangeDialog();
            }
        });


        textViewEditProfilePicture.setOnClickListener(view -> {
            isChangingProfilePicture = true;
            showImagePickDialog();
        });

        textViewEditCoverPhoto.setOnClickListener(view -> {
            isChangingProfilePicture = false;
            showImagePickDialog();
        });

        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {

            if (result) {
                if (cameraUtils.getCapturedImageUri() != null) {
                    Uri imageUri = cameraUtils.getCapturedImageUri();
                    if (isChangingProfilePicture)
                        Picasso.get().load(imageUri).into(imageViewProfileAvatar);
                    else
                        Picasso.get().load(imageUri).into(imageViewCoverPhoto);

                }
            }
        });

        galleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        if (isChangingProfilePicture)
                            Picasso.get().load(imageUri).into(imageViewProfileAvatar);
                        else
                            Picasso.get().load(imageUri).into(imageViewCoverPhoto);
                    }
                }
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case PermissionUtils.CAMERA_REQUEST_CODE -> {
                if (PermissionUtils.checkCameraPermission(this)) {
                    cameraUtils.openCamera(cameraActivityResultLauncher);
                } else {
                    //Permission denied
                    Toast.makeText(this, "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
                }
            }

            case PermissionUtils.STORAGE_REQUEST_CODE -> {
                if (PermissionUtils.checkStoragePermission(this)) {
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
        // Build dialog for choosing camera or gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose your image");
        builder.setItems(new String[]{"Camera", "Gallery"}, (dialogInterface, i) -> {
            if (i == 0) {
                PermissionUtils.requestCameraPermission(this);
            } else
                PermissionUtils.requestStoragePermission(this);
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showEditFieldDialog(String title, View view, String fieldValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        if (view == null) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(55, 0, 55, 0);

            EditText editText = new EditText(this);
            editText.setText(fieldValue);

            layout.addView(editText, params);
            view = layout;
        }
        builder.setView(view);

        builder.setPositiveButton("CHANGE", (dialogInterface, i) -> {
            switch (fieldValue) {
                case "name" -> { editTextName.setText();}
                case "bio" -> {}
                case "gender" -> {}
                case "birthday" -> {}
                case "password" -> {}
                default -> {}
            }
        });

        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            // Close the dialog without performing any action
            dialogInterface.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }




    public void setIsChanged(boolean changed) {
        this.isChanged = changed;
    }

    @Override
    public void loadingInfomationUser(User user) {
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
                    Picasso.get().load(avatar).into(imageViewProfileAvatar);

                } else {
                    textViewEditProfilePicture.setText("Add");
                    imageViewProfileAvatar.setImageResource(R.drawable.baseline_avatar_place_holder);
                }

                if (cover != null && !cover.isEmpty()) {
                    textViewEditCoverPhoto.setText("Edit");
                    Picasso.get().load(cover).into(imageViewCoverPhoto);

                } else {
                    textViewEditCoverPhoto.setText("Add");
                    imageViewCoverPhoto.setImageResource(R.color.not_active_icon);
                }

                setIsChanged(false);

                //Create dialogue for each field
                editTextName.setOnClickListener(view -> {
                    showEditFieldDialog("Edit Name", null, name);
                });

                editTextBio.setOnClickListener(view -> {
                    showEditFieldDialog("Edit Bio", null, bio);
                });

                editTextGender.setOnClickListener(view -> {

                    //Create linearLayout
                    LinearLayout linearLayout = new LinearLayout(this);

                    //Set params
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(55, 0, 55, 0);

                    //Create adpater spinner
                    String[] options = new String[]{"Male", "Female", "Other"};
                    SpinnerGenderAdapter adapter = new SpinnerGenderAdapter(this,
                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, options);
                    adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

                    //Create spinner
                    Spinner spinner = new Spinner(this);
                    spinner.setAdapter(adapter);
                    int selectedIndex = Arrays.asList(options).indexOf(gender);
                    //Set the selection default as gender of user
                    spinner.setSelection(selectedIndex);
                    linearLayout.addView(spinner, params);
                    showEditFieldDialog("Edit gender", linearLayout, gender);
                });

            }
    }

    @Override
    public void showProgressbar() {

    }

    @Override
    public void onImageUploadSuccess(String imageUrl) {

    }

    @Override
    public void onImageUploadFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}