package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebasePostCommentReactionDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebasePostCommentReactionDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.PostCommentReaction;
import com.example.h2ak.model.User;

import java.util.HashSet;
import java.util.Set;

public class PostCommentReactionDataSourceImpl implements PostCommentReactionDataSource {
    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private Context context;
    private static PostCommentReactionDataSourceImpl instance;
    private String currentUserId;
    private UserDataSource userDataSource;
    private PostCommentDataSource postCommentDataSource;
    private FirebasePostCommentReactionDataSource firebasePostCommentReactionDataSource;
    private String TAG = "PostCommentReactionDataSourceImpl";

    private PostCommentReactionDataSourceImpl(Context context, String currentUserId) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        userDataSource = UserDataSourceImpl.getInstance(context);
        postCommentDataSource = PostCommentDataSourceImpl.getInstance(context);
        firebasePostCommentReactionDataSource = FirebasePostCommentReactionDataSourceImpl.getInstance();
        this.currentUserId = currentUserId;
    }

    public static synchronized PostCommentReactionDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new PostCommentReactionDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    @Override
    public boolean create(PostCommentReaction commentReaction) {
        db.beginTransaction();
        boolean result = false;
        try {

            if (commentReaction.getId() == null || commentReaction.getId().isEmpty()) {
                Log.d(TAG, "create: id is null");
                return  false;
            } else if (commentReaction.getPostComment() == null) {
                Log.d(TAG, "create: post is null");
                return false;
            } else if (commentReaction.getCreatedDate() == null || commentReaction.getCreatedDate().isEmpty()) {
                Log.d(TAG, "create: createdDate is null");
                return false;
            } else if (commentReaction.getUser() == null) {
                Log.d(TAG, "create: User is null");
                return false;
            } else if (commentReaction.getType() == null || commentReaction.getType().isEmpty()) {
                Log.d(TAG, "create: type is null");
                return false;
            } else {

                if (find(commentReaction.getPostComment(), commentReaction.getUser().getId()) == null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_ID, commentReaction.getId());
                    contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_TYPE, commentReaction.getType());
                    contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE, commentReaction.getCreatedDate());
                    contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_USER_ID, commentReaction.getUser().getId());
                    contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID, commentReaction.getPostComment().getId());
                    result  = db.insert(MySQLiteHelper.TABLE_POST_COMMENT_REACTION, null, contentValues) > 0;

                    if (result) {
                        firebasePostCommentReactionDataSource.create(commentReaction);
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
    public boolean delete(PostCommentReaction commentReaction) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (commentReaction.getId() == null || commentReaction.getId().isEmpty()) {
                Log.d(TAG, "delete: id is null");
                return  false;
            } else if (commentReaction.getPostComment() == null) {
                Log.d(TAG, "delete: post is null");
                return false;
            } else if (commentReaction.getCreatedDate() == null || commentReaction.getCreatedDate().isEmpty()) {
                Log.d(TAG, "delete: createdDate is null");
                return false;
            } else if (commentReaction.getUser() == null) {
                Log.d(TAG, "delete: User is null");
                return false;
            } else if (commentReaction.getType() == null || commentReaction.getType().isEmpty()) {
                Log.d(TAG, "delete: type is null");
                return false;
            } else {
                Log.d(TAG, "delete: " + commentReaction.getId());

                if (find(commentReaction.getPostComment(), commentReaction.getUser().getId()) != null) {
                    result  = db.delete(MySQLiteHelper.TABLE_POST_COMMENT_REACTION,
                            MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_ID + " = ? ",
                            new String[]{commentReaction.getId()}) > 0;
                    if (result) {
                        firebasePostCommentReactionDataSource.delete(commentReaction);
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
    public boolean update(PostCommentReaction commentReaction) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (commentReaction.getId() == null || commentReaction.getId().isEmpty()) {
                Log.d(TAG, "update: id is null");
                return  false;
            } else if (commentReaction.getPostComment() == null) {
                Log.d(TAG, "update: post is null");
                return false;
            } else if (commentReaction.getCreatedDate() == null || commentReaction.getCreatedDate().isEmpty()) {
                Log.d(TAG, "update: createdDate is null");
                return false;
            } else if (commentReaction.getUser() == null) {
                Log.d(TAG, "update: User is null");
                return false;
            } else if (commentReaction.getType() == null || commentReaction.getType().isEmpty()) {
                Log.d(TAG, "update: type is null");
                return false;
            } else {
                Log.d(TAG, "update: " + commentReaction.getId());
                PostCommentReaction reaction = find(commentReaction.getPostComment(), commentReaction.getUser().getId());

                if (reaction != null && !reaction.equals(commentReaction)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_TYPE, commentReaction.getType());
                    contentValues.put(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE, commentReaction.getCreatedDate());

                    result  = db.update(MySQLiteHelper.TABLE_POST_COMMENT_REACTION, contentValues ,
                            MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_ID + " = ? ",
                            new String[]{commentReaction.getId()}) > 0;
                    if (result) {
                        firebasePostCommentReactionDataSource.update(commentReaction);
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
    public Set<PostCommentReaction> getAllReactionByComment(PostComment comment) {
        Set<PostCommentReaction> postReactionSet = new HashSet<>();
        if (comment.getId() != null) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_COMMENT_REACTION
                    + " WHERE  " +  MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID + " = ? "
                    + " ORDER BY " + MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE + " DESC ", new String[]{comment.getId()})) {
                if (c != null) {
                    while (c.moveToNext()) {
                        PostCommentReaction commentReaction = getPostReactionByCursor(c);
                        if (commentReaction != null) {
                            postReactionSet.add(commentReaction);
                        }
                    }
                }
            }
        }
        return postReactionSet;
    }

    @Override
    public Set<PostCommentReaction> getAllReactionByUser(User user) {
        Set<PostCommentReaction> postReactionSet = new HashSet<>();
        if (user.getId() != null) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_COMMENT_REACTION
                    + " WHERE  " +  MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_USER_ID + " = ? "
                    + " ORDER BY " + MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE + " DESC ", new String[]{user.getId()})) {
                if (c != null) {
                    while (c.moveToNext()) {
                        PostCommentReaction commentReaction = getPostReactionByCursor(c);
                        if (commentReaction != null) {
                            postReactionSet.add(commentReaction);
                        }
                    }
                }
            }
        }
        return postReactionSet;
    }

    @Override
    public PostCommentReaction find(PostComment comment, String currentUserId) {
        PostCommentReaction postReactionSet = null;
        if (comment.getId() != null) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_POST_COMMENT_REACTION
                    + " WHERE  " +  MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID + " = ? "
                    + " AND " + MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_USER_ID + " = ? "
                    + " ORDER BY " + MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE + " DESC ", new String[]{comment.getId(), currentUserId})) {
                if (c != null && c.moveToFirst()) {
                    postReactionSet = getPostReactionByCursor(c);
                }
            }
        }
        return postReactionSet;
    }

    public PostCommentReaction getPostReactionByCursor(Cursor c) {
        PostCommentReaction commentReaction = null;
        if (c != null) {
            String id = c.getString(0);
            String type = c.getString(1);
            String createdDate = c.getString(2);
            String userId = c.getString(3);
            String commentId = c.getString(4);

            if (id == null || id.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: id is null");
                return null;
            } else if (type == null || type.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: type is null");
                return  null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: createdDate is null");
                return  null;
            } else if (userId == null || userId.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: user_id is null");
                return  null;
            } else if (commentId == null || commentId.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: comment_id is null");
                return null;
            } else {
                User user = userDataSource.getUserById(userId);
                PostComment comment = postCommentDataSource.getById(commentId);

                if (user == null) {
                    Log.d(TAG, "getPostReactionByCursor: user is null");
                    return null;
                }else if (comment == null) {
                    Log.d(TAG, "getPostReactionByCursor: comment is null");
                    return null;
                } else {
                    commentReaction = new PostCommentReaction(PostCommentReaction.CommentReactionType.valueOf(type), user,
                            comment);
                    commentReaction.setId(id);
                    commentReaction.setCreatedDate(createdDate);
                }
            }
        }
        return commentReaction;
    }
    
}
