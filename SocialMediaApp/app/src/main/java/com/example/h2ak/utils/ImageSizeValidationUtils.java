package com.example.h2ak.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import id.zelory.compressor.Compressor;

public class ImageSizeValidationUtils {
    private static final int COVER_MIN_WIDTH = 400;
    private static final int COVER_MIN_HEIGHT = 200;

    private static final int AVATAR_MIN_WIDTH = 100;
    private static final int AVATAR_MIN_HEIGHT = 100;

    public enum ImageType {

        AVATAR(AVATAR_MIN_WIDTH, AVATAR_MIN_HEIGHT),
        COVER(COVER_MIN_WIDTH, COVER_MIN_HEIGHT);

        private int minWidth;
        private int minHeight;
        ImageType(int minWidth, int  minHeight) {
            this.minWidth = minWidth;
            this.minHeight = minHeight;
        }

        public int getMinWidth() {
            return minWidth;
        }

        public int minHeight() {
            return minHeight;
        }
    }

    public static boolean checkImageSize(Context context, Uri uri, ImageType imageType) {
        if (imageType != null) {
            int width = 0, height = 0;
            ContentResolver contentResolver = context.getContentResolver();
            String[] projection = {MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                cursor.close();
            }

            return width >= imageType.getMinWidth() && height >= imageType.minHeight();
        }
        return false;
    }

    public static void compressImage(Context context, Uri imageUri, OnImageCompressionListener listener) {
        try {
            // Open an input stream from the Uri
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

            // Create a temporary File to hold the image content
            String uniqueFileName = System.currentTimeMillis() + ".JPEG";
            File tempFile = new File(context.getCacheDir(), uniqueFileName);

            // Copy the content of the InputStream to the temporary File
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // Create another temporary File to hold the compressed image
            File compressedFile = new Compressor(context)
                    .setMaxWidth(720) // Set desired width and height
                    .setMaxHeight(720)
                    .setQuality(60) // Set image quality
                    .setCompressFormat(Bitmap.CompressFormat.JPEG) // Set compress format
                    .compressToFile(tempFile);

            // Delete the temporary File that held the image content
            tempFile.delete();

            // Notify the listener about the compression success
            listener.onCompressionComplete(compressedFile);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onCompressionFailed(e.getMessage());
        }
    }




    public interface OnImageCompressionListener {
        void onCompressionComplete(File compressedFile);
        void onCompressionFailed(String errorMessage);
    }

}
