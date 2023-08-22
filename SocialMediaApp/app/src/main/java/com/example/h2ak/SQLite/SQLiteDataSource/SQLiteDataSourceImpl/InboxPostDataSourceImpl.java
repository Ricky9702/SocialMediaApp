package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseInboxDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseInboxPostDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseInboxDataSource;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseInboxPostDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxPostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.model.InboxPost;
import com.example.h2ak.model.Post;

public class InboxPostDataSourceImpl implements InboxPostDataSource {
    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private static InboxPostDataSourceImpl instance;
    private String currentUserId;
    private PostDataSource postDataSource;
    private FirebaseInboxPostDataSource firebaseInboxPostDataSource;

    private InboxPostDataSourceImpl(Context context, String currentUserId) {
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        this.currentUserId = currentUserId;
        postDataSource = PostDataSourceImpl.getInstance(context);
        firebaseInboxPostDataSource = FirebaseInboxPostDataSourceImpl.getInstance();
    }

    public static synchronized InboxPostDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new InboxPostDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    @Override
    public boolean create(InboxPost inboxPost) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (inboxPost.getId() == null || inboxPost.getId().isEmpty()) {
                Log.d("createInboxPost", "is is null");
                return false;
            } else if (inboxPost.getPost() == null) {
                Log.d("createInboxPost", "post is null");
                return false;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_POST_ID, inboxPost.getId());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_POST_POST_ID, inboxPost.getPost().getId());
                if (find(inboxPost.getId()) == null) {
                    result = db.insert(MySQLiteHelper.TABLE_INBOX_POST, null, contentValues) > 0;
                    if (result) {
                        firebaseInboxPostDataSource.createInboxPost(inboxPost);
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.d("createInbox : ", ex.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public boolean delete(String id) {
        boolean result = false;
        {
            db.beginTransaction();
            try {
                result = db.delete(MySQLiteHelper.TABLE_INBOX_POST, MySQLiteHelper.COLUMN_INBOX_POST_ID + " = ? ",
                        new String[]{id}) > 0;
                if (result) {
                    firebaseInboxPostDataSource.delete(id);
                }
                db.setTransactionSuccessful();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
        return result;
    }

    @Override
    public InboxPost find(String id) {
        if (id != null && !id.isEmpty()) {
            try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_INBOX_POST
                    + " WHERE " + MySQLiteHelper.COLUMN_INBOX_POST_ID + " = ? "
                    + " LIMIT 1 ", new String[]{id})) {
                if (c != null && c.moveToFirst()) {
                    String inboxId = c.getString(0);
                    String postId = c.getString(1);

                    if (postId == null || postId.isEmpty()) {
                        return null;
                    } else {
                        Post post = postDataSource.findPost(postId);

                        if (post == null) {
                            return null;
                        }

                        InboxPost inboxPost = new InboxPost();
                        inboxPost.setId(inboxId);
                        inboxPost.setPost(post);

                        return inboxPost;

                    }
                }
            }
        }
        return null;
    }
}
