package com.example.h2ak.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int STORAGE_REQUEST_CODE = 2;
    public static final int IMAGE_PICK_GALLERY_CODE = 3;
    public static final int IMAGE_PICK_CAMERA_CODE = 4;
    public static String[] cameraPermissions;
    public static String[] storagePermissions;

    static {

        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public static boolean checkStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    public static void requestStoragePermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, storagePermissions, STORAGE_REQUEST_CODE);
    }

    public static boolean checkCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    public static void requestCameraPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, cameraPermissions, CAMERA_REQUEST_CODE);
    }

}