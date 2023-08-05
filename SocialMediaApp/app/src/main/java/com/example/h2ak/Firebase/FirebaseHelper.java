package com.example.h2ak.Firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.utils.ImageSizeValidationUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import id.zelory.compressor.Compressor;

public class FirebaseHelper {

    private static final FirebaseStorage firebaseStorage;
    private static final StorageReference storageReference;
    private static final StorageReference imageStorageReference;
    private static final FirebaseDatabase firebaseDatabase;

    static {

        //Database
        firebaseDatabase = FirebaseDatabase.getInstance("https://socialmediaapp-32653-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Storage
        firebaseStorage = FirebaseStorage.getInstance("gs://socialmediaapp-32653.appspot.com");
        storageReference = firebaseStorage.getReference();
        imageStorageReference = storageReference.child("images");
    }

    public static DatabaseReference getDatabaseReferenceByPath(String path) {
        return firebaseDatabase.getReference(path);
    }

    public static StorageReference getImageAvatarStorageRef() {
        return imageStorageReference.child("avatar");
    }

    public static StorageReference getImageCoverStorageRef() {
        return imageStorageReference.child("cover");
    }

    public static void uploadImageToFirebaseCloud(Context context, Uri imageUri, StorageReference storageReference, OnImageUploadListener listener) {
        if (imageUri != null && storageReference != null) {
            Log.d("TEST IMAGE URI PART2", imageUri.toString());
            StorageReference fileRef = storageReference.child(imageUri.getLastPathSegment());
            //Compress the image
            ImageSizeValidationUtils.compressImage(context, imageUri, new ImageSizeValidationUtils.OnImageCompressionListener() {
                @Override
                public void onCompressionComplete(File compressedFile) {
//                    Create path in firebase cloud
                    //Starting upload
                    Log.d("Test compressed file", compressedFile.toString());
                    UploadTask uploadTask = fileRef.putFile(Uri.fromFile(compressedFile));

                    // upload sucess
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // download sucess
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d("TEST URI", uri.toString());
                            if (listener != null) {
                                listener.onImageUploadSuccess(uri.toString());
                            }
                        }).addOnFailureListener(runnable -> {
                            // download fail
                            if (listener != null)
                                listener.onImageUploadFailure(runnable.getMessage());
                        });

                    }).addOnFailureListener(runnable -> {
                        // upload fail
                        if (listener != null)
                            listener.onImageUploadSuccess(runnable.getMessage());
                    });
                }

                @Override
                public void onCompressionFailed(String errorMessage) {
                    listener.onImageUploadFailure(errorMessage);
                }
            });
        }
    }
    public interface OnImageUploadListener {
        void onImageUploadSuccess(String imageUrl);

        void onImageUploadFailure(String errorMessage);
    }
}