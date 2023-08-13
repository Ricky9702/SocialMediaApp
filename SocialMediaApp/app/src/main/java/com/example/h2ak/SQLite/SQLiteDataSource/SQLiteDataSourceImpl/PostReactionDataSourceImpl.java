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
import com.example.h2ak.SQLite.SQLiteDataSource.PostReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.model.PostReaction;
import com.example.h2ak.model.User;

import java.util.HashSet;
import java.util.Set;

public class PostReactionDataSourceImpl implements PostReactionDataSource {
    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private Context context;
    private static PostReactionDataSourceImpl instance;
    private String currentUserId;
    private UserDataSource userDataSource;
    private PostDataSource postDataSource;

    private PostReactionDataSourceImpl(Context context, String currentUserId) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        userDataSource = UserDataSourceImpl.getInstance(context);
        postDataSource = PostDataSourceImpl.getInstance(context);
        this.currentUserId = currentUserId;
    }

    public static synchronized PostReactionDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new PostReactionDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    @Override
    public boolean create(PostReaction postReaction) {
        db.beginTransaction();
        boolean result = false;
        try {

            if (postReaction.getId() == null || postReaction.getId().isEmpty()) {
                Log.d("PostReactionDataSourceImpl", "create: id is null");
                return  false;
            } else if (postReaction.getPost() == null) {
                Log.d("PostReactionDataSourceImpl", "create: post is null");
                return false;
            } else if (postReaction.getCreatedDate() == null || postReaction.getCreatedDate().isEmpty()) {
                Log.d("PostReactionDataSourceImpl", "create: createdDate is null");
                return false;
            } else if (postReaction.getUser() == null) {
                Log.d("PostReactionDataSourceImpl", "create: User is null");
                return false;
            } else if (postReaction.getType() == null || postReaction.getType().isEmpty()) {
                Log.d("PostReactionDataSourceImpl", "create: type is null");
                return false;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_POST_REACTION_ID, postReaction.getId());
                contentValues.put(MySQLiteHelper.COLUMN_POST_REACTION_TYPE, postReaction.getType());
                contentValues.put(MySQLiteHelper.COLUMN_POST_REACTION_CREATED_DATE, postReaction.getCreatedDate());
                contentValues.put(MySQLiteHelper.COLUMN_POST_REACTION_USER_ID, postReaction.getUser().getId());
                contentValues.put(MySQLiteHelper.COLUMN_POST_REACTION_POST_ID, postReaction.getPost().getId());

                result  = db.insert(MySQLiteHelper.TABLE_POST_REACTION, null, contentValues) > 0;
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
    public boolean delete(PostReaction postReaction) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (postReaction.getId() == null || postReaction.getId().isEmpty()) {
                Log.d("PostReactionDataSourceImpl", "delete: id is null");
                return  false;
            } else if (postReaction.getPost() == null) {
                Log.d("PostReactionDataSourceImpl", "delete: post is null");
                return false;
            } else if (postReaction.getCreatedDate() == null || postReaction.getCreatedDate().isEmpty()) {
                Log.d("PostReactionDataSourceImpl", "delete: createdDate is null");
                return false;
            } else if (postReaction.getUser() == null) {
                Log.d("PostReactionDataSourceImpl", "delete: User is null");
                return false;
            } else if (postReaction.getType() == null || postReaction.getType().isEmpty()) {
                Log.d("PostReactionDataSourceImpl", "delete: type is null");
                return false;
            } else {
                Log.d("PostReactionDataSourceImpl", "delete: " + postReaction.getId());
                result  = db.delete(MySQLiteHelper.TABLE_POST_REACTION,
                        MySQLiteHelper.COLUMN_POST_REACTION_ID + " = ? ",
                        new String[]{postReaction.getId()}) > 0;
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
    public Set<PostReaction> getAllReactionByPost(Post post) {
        Set<PostReaction> postReactionSet = new HashSet<>();
        if (post.getId() != null) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_REACTION
                    + " WHERE  " +  MySQLiteHelper.COLUMN_POST_REACTION_POST_ID + " = ? "
                    + " ORDER BY " + MySQLiteHelper.COLUMN_POST_REACTION_CREATED_DATE + " DESC ", new String[]{post.getId()})) {
                if (c != null) {
                    while (c.moveToNext()) {
                        PostReaction postReaction = getPostReactionByCursor(c);
                        if (postReaction != null) {
                            postReactionSet.add(postReaction);
                        }
                    }
                }
            }
        }
        return postReactionSet;
    }

    public PostReaction getPostReactionByCursor(Cursor c) {

        if (c != null) {

            String id = c.getString(0);
            String type = c.getString(1);
            String createdDate = c.getString(2);
            String userId = c.getString(3);
            String postId = c.getString(4);

            if (id == null || id.isEmpty()) {
                return null;
            } else if (type == null || type.isEmpty()) {
                return  null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                return  null;
            } else if (userId == null || userId.isEmpty()) {
                return  null;
            } else if (postId == null || postId.isEmpty()) {
                return null;
            } else {
                User user = userDataSource.getUserById(userId);
                Post post = postDataSource.findPost(postId);

                if (user == null || post == null) {
                    return null;
                } else {
                    PostReaction postReaction = new PostReaction(PostReaction.PostReactionType.valueOf(type), user, post);
                    postReaction.setId(id);
                    postReaction.setCreatedDate(createdDate);
                    return postReaction;
                }
            }
        }

        return null;
    }



    @Override
    public PostReaction find(Post post, String currentUserId) {
        PostReaction postReaction = null;
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_REACTION
                + " WHERE " + MySQLiteHelper.COLUMN_POST_REACTION_POST_ID + " = ? "
                + " AND " + MySQLiteHelper.COLUMN_POST_REACTION_USER_ID + " = ?"
                + " ORDER BY " + MySQLiteHelper.COLUMN_POST_REACTION_CREATED_DATE + " ASC "
                + " LIMIT 1 ", new String[] {post.getId(), currentUserId})) {
            if (c != null && c.moveToFirst()) {
                postReaction = getPostReactionByCursor(c);
            }
        }
        return postReaction;
    }
}
