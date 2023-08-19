package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebasePostDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebasePostDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PostDataSourceImpl implements PostDataSource {

    private static PostDataSourceImpl instance;
    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private String currentUserId;
    private UserDataSource userDataSource;
    private FirebasePostDataSource firebasePostDataSource;

    private PostDataSourceImpl(Context context, String currentUserId) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        this.currentUserId = currentUserId;
        userDataSource = UserDataSourceImpl.getInstance(context);
        firebasePostDataSource = FirebasePostDataSourceImpl.getInstance();
    }

    public static synchronized PostDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new PostDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }


    @Override
    public boolean createPost(Post post) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (post.getId() == null || post.getId().isEmpty()) {
                Log.d("createPost: ", "id is null");
                return false;
            } else if (post.getContent() == null) {
                Log.d("createPost: ", "content is null");
                return false;
            } else if (post.getCreatedDate() == null || post.getCreatedDate().isEmpty()) {
                Log.d("createPost: ", "createdDate is null");
                return false;
            } else if (post.getUser() == null) {
                Log.d("createPost: ", "user is null");
                return false;
            } else if (post.getPrivacy() == null || post.getPrivacy().isEmpty()) {
                Log.d("createPost: ", "privacy is null");
                return false;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_POST_ID, post.getId());
                contentValues.put(MySQLiteHelper.COLUMN_POST_CONTENT, post.getContent());
                contentValues.put(MySQLiteHelper.COLUMN_POST_CREATED_DATE, post.getCreatedDate());
                contentValues.put(MySQLiteHelper.COLUMN_POST_USER_ID, post.getUser().getId());
                contentValues.put(MySQLiteHelper.COLUMN_POST_PRIVACY, post.getPrivacy());

                if (findPost(post.getId()) == null) {
                    result = db.insert(MySQLiteHelper.TABLE_POST, null, contentValues) > 0;
                    if (result) {
                        if (firebasePostDataSource.create(post)) {
                            Log.d("createPostFirebase: ", "success");
                        } else {
                            Log.d("createPostFirebase: ", "failed");
                        }
                    }
                } else {
                    Log.d("createPost: ", "already exists");
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
            ;
        }
        return result;
    }

    @Override
    public boolean deletePost(Post post) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (post.getId() == null || post.getId().isEmpty()) {
                Log.d("deletePost: ", "id is null");
                return false;
            } else if (post.getContent() == null) {
                Log.d("deletePost: ", "content is null");
                return false;
            } else if (post.getCreatedDate() == null || post.getCreatedDate().isEmpty()) {
                Log.d("deletePost: ", "createdDate is null");
                return false;
            } else if (post.getUser() == null) {
                Log.d("deletePost: ", "user is null");
                return false;
            } else if (post.getPrivacy() == null || post.getPrivacy().isEmpty()) {
                Log.d("deletePost: ", "privacy is null");
                return false;
            } else {
                if (findPost(post.getId()) != null) {
                    result = db.delete(MySQLiteHelper.TABLE_POST, MySQLiteHelper.COLUMN_POST_ID + " = ? " , new String[]{post.getId()}) > 0;
                    if (result) {
                        if (firebasePostDataSource.delete(post)) {
                            Log.d("deletePostFirebase: ", "success");
                        } else {
                            Log.d("deletePostFirebase: ", "failed");
                        }
                    }
                }
            }
            // delete post, so we need to delete its like, comment, image;

            db.setTransactionSuccessful();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public boolean updatePost(Post post) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (post.getId() == null || post.getId().isEmpty()) {
                Log.d("updatePost: ", "id is null");
                return false;
            } else if (post.getContent() == null) {
                Log.d("updatePost: ", "content is null");
                return false;
            } else if (post.getCreatedDate() == null || post.getCreatedDate().isEmpty()) {
                Log.d("updatePost: ", "createdDate is null");
                return false;
            } else if (post.getUser() == null) {
                Log.d("updatePost: ", "user is null");
                return false;
            } else if (post.getPrivacy() == null || post.getPrivacy().isEmpty()) {
                Log.d("updatePost: ", "privacy is null");
                return false;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_POST_CONTENT, post.getContent());
                contentValues.put(MySQLiteHelper.COLUMN_POST_PRIVACY, post.getPrivacy());
                contentValues.put(MySQLiteHelper.COLUMN_POST_CREATED_DATE, new Post().getCreatedDate());
                if (findPost(post.getId()) != null && !findPost(post.getId()).equals(post)) {
                    result = db.update(MySQLiteHelper.TABLE_POST, contentValues, MySQLiteHelper.COLUMN_POST_ID + " = ? ", new String[]{post.getId()}) > 0;
                    if (result) {
                        if (firebasePostDataSource.update(post)) {
                            Log.d("updatePostFirebase: ", "successs");
                        } else {
                            Log.d("updatePostFirebase: ", "failed");
                        }
                    }
                }
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
    public Post findPost(String id) {
        Post post = null;
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST
                + " WHERE " + MySQLiteHelper.COLUMN_POST_ID + " = ? ", new String[]{id})) {
            if (c != null && c.moveToFirst()) {
                post = getPostByCursor(c);
            }
        }
        return post;
    }

    @Override
    public Set<Post> getAllPost() {
        Set<Post> postSet = new HashSet<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST
                + " ORDER BY  " + MySQLiteHelper.COLUMN_POST_CREATED_DATE + " DESC ",
               null)) {
            if (c != null) {
                while (c.moveToNext()) {
                    Post post = getPostByCursor(c);
                    if (post != null) {
                        postSet.add(post);
                    }
                }
            }
        }
        return postSet;
    }



    Post getPostByCursor(Cursor c) {
        Post post = null;

        int idColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_ID);
        int contentColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_CONTENT);
        int createdDateColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_CREATED_DATE);
        int privacyColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_PRIVACY);
        int userIdColumnIndex = c.getColumnIndex(MySQLiteHelper.COLUMN_POST_USER_ID);

        String userId = c.getString(userIdColumnIndex);
        User user = null;

        if (userId != null) {
            user = userDataSource.getUserById(userId);
        }

        if (user != null) {
            post = new Post(c.getString(contentColumnIndex), user, Post.PostPrivacy.valueOf(c.getString(privacyColumnIndex)));
            post.setId(c.getString(idColumnIndex));
            post.setCreatedDate(c.getString(createdDateColumnIndex));
        }

        return post;

    }


    @Override
    public Set<Post> getAllPostByUserId(String id) {
        Set<Post> postSet = new HashSet<>();
        if (id != null && !id.isEmpty()) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST
                    + " WHERE " + MySQLiteHelper.COLUMN_POST_USER_ID + " = ? ", new String[]{id})) {
                if (c != null) {
                    while (c.moveToNext()) {
                        Post post = getPostByCursor(c);
                        if (post != null) {
                            postSet.add(post);
                        }
                    }
                }
            }
        }
        return postSet;
    }

    @Override
    public Set<Post> getAllPostByUserIdWithPrivacy(String id, String privacy1, String privacy2) {
        Set<Post> postSet = new HashSet<>();
        postSet.addAll(getAllPostByUserId(id).stream().filter(post -> post.getPrivacy().equals(privacy1)).collect(Collectors.toList()));
        if (privacy2 != null && !privacy2.isEmpty()) {
            postSet.addAll(getAllPostByUserId(id).stream().filter(post -> post.getPrivacy().equals(privacy2)).collect(Collectors.toList()));
        }
        return postSet;
    }

    @Override
    public Set<Post> getRandomPost() {
        Set<Post> postSet = new HashSet<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST
                + " WHERE " + MySQLiteHelper.COLUMN_POST_PRIVACY + " = ? "
                + " ORDER BY RANDOM() ", new String[]{Post.PostPrivacy.PUBLIC.getPrivacy()})) {
            if (c != null) {
                while (c.moveToNext()) {
                    Post post = getPostByCursor(c);
                    if (post != null) {
                        postSet.add(post);
                    }
                }
            }
        }
        return postSet;
    }
}
