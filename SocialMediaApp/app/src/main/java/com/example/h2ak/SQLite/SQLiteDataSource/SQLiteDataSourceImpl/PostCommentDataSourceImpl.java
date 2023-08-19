package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FireBaseCommentDataSource;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FireBaseCommentDataSourceImpl;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.PostReaction;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.TextInputLayoutUtils;

import org.w3c.dom.Comment;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PostCommentDataSourceImpl implements PostCommentDataSource {

    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private static PostCommentDataSourceImpl instance;
    private String currentUserId;
    private UserDataSource userDataSource;
    private PostDataSource postDataSource;
    private FireBaseCommentDataSource fireBaseCommentDataSource;

    private String TAG = "PostCommentDataSourceImpl";

    private PostCommentDataSourceImpl(Context context, String currentUserId) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        this.currentUserId = currentUserId;
        userDataSource = UserDataSourceImpl.getInstance(context);
        postDataSource = PostDataSourceImpl.getInstance(context);
        fireBaseCommentDataSource = FireBaseCommentDataSourceImpl.getInstance();
    }

    public static synchronized PostCommentDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new PostCommentDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }


    @Override
    public boolean create(PostComment comment) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (comment.getId() == null || comment.getId().isEmpty()) {
                Log.d(TAG, "create: id is null");
                return false;
            } else if (comment.getCreatedDate() == null || comment.getCreatedDate().isEmpty()) {
                Log.d(TAG, "create: createdDate is null");
                return false;
            } else if (comment.getContent() == null || comment.getContent().isEmpty()) {
                Log.d(TAG, "create: content is null");
                return false;
            } else if (comment.getUser() == null) {
                Log.d(TAG, "create: user is null");
                return false;
            } else if (comment.getPost() == null) {
                Log.d(TAG, "create: post is null");
                return false;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_ID, comment.getId());
                contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE, comment.getCreatedDate());
                contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_CONTENT, comment.getContent());
                contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_USER_ID, comment.getUser().getId());
                contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_POST_ID, comment.getPost().getId());

                Log.d(TAG, "create: "+ comment.getParent()+ MyApp.getInstance().getCurrentUser().getEmail());

                if (getById(comment.getId()) == null) {

                    if (comment.getParent() != null) {
                        contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_PARENT_ID, comment.getParent().getId());
                    } else {
                        contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_PARENT_ID, "");
                    }

                    result = db.insert(MySQLiteHelper.TABLE_POST_COMMENT, null, contentValues) > 0;

                    if (result) {
                        Log.d(TAG, "create: X2 "+ comment.getParent()+ MyApp.getInstance().getCurrentUser().getEmail());
                        fireBaseCommentDataSource.create(comment);
                    }
                }
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
    public boolean delete(PostComment comment) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (comment.getId() == null || comment.getId().isEmpty()) {
                Log.d(TAG, "delete: id is null");
                return false;
            } else if (comment.getCreatedDate() == null || comment.getCreatedDate().isEmpty()) {
                Log.d(TAG, "delete: createdDate is null");
                return false;
            } else if (comment.getContent() == null || comment.getContent().isEmpty()) {
                Log.d(TAG, "delete: content is null");
                return false;
            } else if (comment.getUser() == null) {
                Log.d(TAG, "delete: user is null");
                return false;
            } else if (comment.getPost() == null) {
                Log.d(TAG, "delete: post is null");
                return false;
            } else {
                if (getById(comment.getId()) != null) {
                    result = db.delete(MySQLiteHelper.TABLE_POST_COMMENT, MySQLiteHelper.COLUMN_POST_COMMENT_ID + " = ? ", new String[]{comment.getId()}) > 0;
                    if (result) {
                        fireBaseCommentDataSource.delete(comment);
                    }
                }
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
    public boolean update(PostComment comment) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (comment.getId() == null || comment.getId().isEmpty()) {
                Log.d(TAG, "update: id is null");
                return false;
            } else if (comment.getCreatedDate() == null || comment.getCreatedDate().isEmpty()) {
                Log.d(TAG, "update: createdDate is null");
                return false;
            } else if (comment.getContent() == null || comment.getContent().isEmpty()) {
                Log.d(TAG, "update: content is null");
                return false;
            } else if (comment.getUser() == null) {
                Log.d(TAG, "update: user is null");
                return false;
            } else if (comment.getPost() == null) {
                Log.d(TAG, "update: post is null");
                return false;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_CONTENT, comment.getContent());
                contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE, new PostComment().getCreatedDate());

                if (comment.getParent() != null) {
                    this.updateParentField(comment);
                 }

                if (getById(comment.getId()) != null && !getById(comment.getId()).equals(comment)) {
                    result = db.update(MySQLiteHelper.TABLE_POST_COMMENT,
                            contentValues,
                            MySQLiteHelper.COLUMN_POST_COMMENT_ID + " = ? ", new String[]{comment.getId()}) > 0;
                    if (result) {
                        fireBaseCommentDataSource.update(comment);
                    }
                }

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
    public boolean updateParentField(PostComment comment) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (comment.getId() == null || comment.getId().isEmpty()) {
                Log.d(TAG, "update: id is null");
                return false;
            } else if (comment.getCreatedDate() == null || comment.getCreatedDate().isEmpty()) {
                Log.d(TAG, "update: createdDate is null");
                return false;
            } else if (comment.getContent() == null || comment.getContent().isEmpty()) {
                Log.d(TAG, "update: content is null");
                return false;
            } else if (comment.getUser() == null) {
                Log.d(TAG, "update: user is null");
                return false;
            } else if (comment.getPost() == null) {
                Log.d(TAG, "update: post is null");
                return false;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_PARENT_ID, comment.getParent().getId());
                result = db.update(MySQLiteHelper.TABLE_POST_COMMENT,
                        contentValues,
                        MySQLiteHelper.COLUMN_POST_COMMENT_ID + " = ? ", new String[]{comment.getId()}) > 0;

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
    public Set<PostComment> getAllCommentByPost(Post post) {
        Set<PostComment> postCommentSet = new HashSet<>();
        if (post.getId() != null) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_COMMENT
                    + " WHERE  " + MySQLiteHelper.COLUMN_POST_COMMENT_POST_ID + " = ? "
                    + " ORDER BY " + MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE + " DESC ", new String[]{post.getId()})) {
                if (c != null) {
                    while (c.moveToNext()) {
                        PostComment postComment = getCommentByCursor(c);
                        if (postComment != null) {
                            postCommentSet.add(postComment);
                        }
                    }
                }
            }
        }
        return postCommentSet;
    }

    @Override
    public Set<PostComment> getAllCommentByParent(PostComment comment) {
        Set<PostComment> postCommentSet = new HashSet<>();
        if (comment.getId() != null) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_COMMENT
                    + " WHERE  " + MySQLiteHelper.COLUMN_POST_COMMENT_PARENT_ID + " = ? "
                    + " ORDER BY " + MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE + " DESC ", new String[]{comment.getId()})) {
                if (c != null) {
                    while (c.moveToNext()) {
                        PostComment postComment = getCommentByCursor(c);
                        if (postComment != null) {
                            postCommentSet.add(postComment);
                        }
                    }
                }
            }
        }
        return postCommentSet;
    }

    @Override
    public PostComment getById(String id) {
        PostComment comment = null;
        if (id != null && !id.isEmpty()) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_COMMENT
                    + " WHERE  " + MySQLiteHelper.COLUMN_POST_COMMENT_ID + " = ? ", new String[]{id})) {
                if (c != null && c.moveToFirst()) {
                    comment = getCommentByCursor(c);
                }
            }
        }
        return comment;
    }

    @Override
    public PostComment getNewestComment(User user) {
        PostComment comment = null;
        if (user.getId() != null && !user.getId().isEmpty()) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_COMMENT
                    + " WHERE  " + MySQLiteHelper.COLUMN_POST_COMMENT_USER_ID + " = ? "
                    + " ORDER BY  " + MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE + " DESC "
                    + " LIMIT 1 ", new String[]{user.getId()})) {
                if (c != null && c.moveToFirst()) {
                    comment = getCommentByCursor(c);
                }
            }
        }
        return comment;
    }

    public PostComment getCommentByCursor(Cursor cursor) {
        PostComment postComment = null;
        if (cursor != null) {
            String id = cursor.getString(0);
            String createdDate = cursor.getString(1);
            String content = cursor.getString(2);
            String userId = cursor.getString(3);
            String parentId = cursor.getString(4);
            String postId = cursor.getString(5);

            if (id == null || id.isEmpty()) {
                Log.d(TAG, "getCommentByCursor: id is null");
                return null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                Log.d(TAG, "getCommentByCursor: createdDate is null");
                return null;
            } else if (content == null || content.isEmpty()) {
                Log.d(TAG, "getCommentByCursor: content is null");
                return null;
            } else if (userId == null || userId.isEmpty()) {
                Log.d(TAG, "getCommentByCursor: userId is null");
                return null;
            } else if (postId == null || postId.isEmpty()) {
                Log.d(TAG, "getCommentByCursor: postId is null");
                return null;
            } else {

                User user = userDataSource.getUserById(userId);
                Post post = postDataSource.findPost(postId);

                if (user == null || post == null) {
                    Log.d(TAG, "getCommentByCursor: user or post is null");
                    return null;
                } else {
                    postComment = new PostComment(content, user, post);
                    postComment.setId(id);
                    postComment.setCreatedDate(createdDate);

                    if (parentId != null && !parentId.isEmpty()) {
                        PostComment parent = getById(parentId);
                        if (parent != null) postComment.setParent(parent);
                    }
                }

            }
        }
        return postComment;
    }
}
