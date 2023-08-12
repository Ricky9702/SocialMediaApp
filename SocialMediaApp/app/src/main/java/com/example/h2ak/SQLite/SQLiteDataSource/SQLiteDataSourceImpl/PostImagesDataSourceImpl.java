package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;

import java.util.HashSet;
import java.util.Set;

public class PostImagesDataSourceImpl implements PostImagesDataSource {

    private static PostImagesDataSourceImpl instance;
    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private String currentUserId;
    private PostDataSource postDataSource;

    private PostImagesDataSourceImpl(Context context, String currentUserId) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        this.currentUserId = currentUserId;
        postDataSource = PostDataSourceImpl.getInstance(context);
    }

    public static synchronized PostImagesDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new PostImagesDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }
    @Override
    public boolean createPostImages(PostImages postImages) {
        db.beginTransaction();
        boolean result = false;
        try {

            if (postImages.getId() == null || postImages.getId().isEmpty()) {
                Log.d("createPostImages", "is null");
                return false;
            } else if (postImages.getImageUrl() == null || postImages.getImageUrl().isEmpty()) {
                Log.d("createPostImages", "imageUrl null");
                return false;
            } else if (postImages.getPost() == null) {
                Log.d("createPostImages", "post  null");
                return false;
            } else if (postImages.getCreatedDate() == null || postImages.getCreatedDate().isEmpty()) {
                Log.d("createPostImages", "createdDate  null");
                return false;
            } else {

                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_POST_IMAGES_ID, postImages.getId());
                contentValues.put(MySQLiteHelper.COLUMN_POST_IMAGES_IMAGE_URL, postImages.getImageUrl());
                contentValues.put(MySQLiteHelper.COLUMN_POST_IMAGES_POST_ID, postImages.getPost().getId());
                contentValues.put(MySQLiteHelper.COLUMN_POST_CREATED_DATE, postImages.getCreatedDate());

                result = db.insert(MySQLiteHelper.TABLE_POST_IMAGES, null, contentValues) > 0;
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public boolean deletePostImages(PostImages postImages) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (postImages.getId() == null || postImages.getId().isEmpty()) {
                Log.d("createPostImages", "is null");
                return false;
            } else if (postImages.getImageUrl() == null || postImages.getImageUrl().isEmpty()) {
                Log.d("createPostImages", "imageUrl null");
                return false;
            } else if (postImages.getPost() == null) {
                Log.d("createPostImages", "post  null");
                return false;
            } else if (postImages.getCreatedDate() == null || postImages.getCreatedDate().isEmpty()) {
                Log.d("createPostImages", "createdDate  null");
                return false;
            } else {
                result = db.delete(MySQLiteHelper.TABLE_POST_IMAGES, MySQLiteHelper.COLUMN_POST_IMAGES_ID + " = ? ",
                        new String[]{postImages.getId()}) > 0;
            }

            db.setTransactionSuccessful();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public boolean updatePostImages(PostImages postImages) {
        return false;
    }

    @Override
    public Set<PostImages> getAllPostImagesByPost(Post post) {
        Set<PostImages> postImagesSet = new HashSet<>();

        try(Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_IMAGES
                + " WHERE " + MySQLiteHelper.COLUMN_POST_IMAGES_POST_ID + " = ? "
                + " ORDER BY " + MySQLiteHelper.COLUMN_POST_IMAGES_CREATED_DATE + " ASC ", new String[]{post.getId()})) {
            if (c != null) {
                while (c.moveToNext()) {
                    PostImages postImages = this.getPostImagesByCursor(c);
                    if (postImages != null) {
                        postImagesSet.add(postImages);
                    }
                }
            }
        }
        return postImagesSet;
    }

    @Override
    public PostImages getFirstPostImages(Post post) {
        PostImages postImages = null;
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_IMAGES
                + " WHERE " + MySQLiteHelper.COLUMN_POST_IMAGES_POST_ID + " = ? "
                + " ORDER BY " + MySQLiteHelper.COLUMN_POST_IMAGES_CREATED_DATE + " ASC "
                + " LIMIT 1 ", new String[] {post.getId()})) {
            if (c != null && c.moveToFirst()) {
                postImages = getPostImagesByCursor(c);
            }
        }
        return postImages;
    }

    PostImages getPostImagesByCursor(Cursor c) {
        PostImages postImages = null;

        int idColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_IMAGES_ID);
        int imageUrlColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_IMAGES_IMAGE_URL);
        int postIdColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_IMAGES_POST_ID);
        int createdDateColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_IMAGES_CREATED_DATE);

        String postId = c.getString(postIdColumnIndex);
        Post post = postDataSource.findPost(postId);
        if (post != null) {
            postImages = new PostImages(c.getString(imageUrlColumnIndex), post);
            postImages.setId(c.getString(idColumnIndex));
            postImages.setCreatedDate(c.getString(createdDateColumnIndex));
        }

        return postImages;
    }

}
