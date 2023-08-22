package com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseInboxDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseInboxDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.DatabaseManager;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InboxDataSourceImpl implements InboxDataSource {
    private SQLiteDatabase db;
    private DatabaseManager databaseManager;
    private UserDataSource userDataSource;
    private FirebaseInboxDataSource firebaseInboxDataSource;
    private static InboxDataSourceImpl instance;
    private String currentUserId;

    private InboxDataSourceImpl(Context context, String currentUserId) {
        firebaseInboxDataSource = FirebaseInboxDataSourceImpl.getInstance();
        userDataSource = UserDataSourceImpl.getInstance(context);
        databaseManager = DatabaseManager.getInstance(context);
        db = databaseManager.getDatabase();
        this.currentUserId = currentUserId;
    }

    public static synchronized InboxDataSourceImpl getInstance(Context context) {
        if (instance == null) {
            instance = new InboxDataSourceImpl(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    @Override
    public boolean createInbox(Inbox inbox) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (inbox.getId() == null || inbox.getId().isEmpty()) {
                Log.d("createInboxOnFirebaseChange", "is is null");
                return false;
            } else if (inbox.getContent() == null || inbox.getContent().isEmpty()) {
                Log.d("createInboxOnFirebaseChange", "content is null");
                return false;
            } else if (inbox.getCreatedDate() == null || inbox.getCreatedDate().isEmpty()) {
                Log.d("createInboxOnFirebaseChange", "createdDate is null");
                return false;
            } else if (inbox.getType() == null || inbox.getType().isEmpty()) {
                Log.d("createInboxOnFirebaseChange", "type is null");
                return false;
            } else if (inbox.getUserRecieveRequest() == null) {
                Log.d("createInboxOnFirebaseChange", "user1 is null");
                return false;
            } else if (inbox.getUserSentRequest() == null) {
                Log.d("createInboxOnFirebaseChange", "user2 is null");
                return false;
            } else {
                Log.d("createInbox : ", "LOCAL");
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_ID, inbox.getId());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_CREATED_DATE, inbox.getCreatedDate());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_CONTENT, inbox.getContent());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_TYPE, inbox.getType());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_IS_READ, inbox.isRead() ? 1 : 0);
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE, inbox.isActive() ? 1 : 0);
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_USER_1, inbox.getUserRecieveRequest().getId());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_USER_2, inbox.getUserSentRequest().getId());
                if (findInbox(inbox) == null) {
                    result = db.insert(MySQLiteHelper.TABLE_INBOX, null, contentValues) > 0;
                    if (result) {
                        firebaseInboxDataSource.createInbox(inbox);
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
    public List<Inbox> getInboxByUser(User user) {
        Set<Inbox> inboxSet = new HashSet<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_INBOX
                + " WHERE " + MySQLiteHelper.COLUMN_INBOX_USER_1 + "  = ? "
                + " OR " + MySQLiteHelper.COLUMN_INBOX_USER_2 + " = ? ", new String[]{user.getId(), user.getId()})) {
            if (c != null) {
                while (c.moveToNext()) {
                    Inbox inbox = getInboxByCursor(c);
                    if (inbox != null) {
                        inboxSet.add(inbox);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return inboxSet.stream().collect(Collectors.toList());
    }

    public boolean createInboxOnFirebaseChange(Inbox inbox) {
        db.beginTransaction();
        boolean result = false;
        try {
            if (inbox.getId() == null || inbox.getId().isEmpty()) {
                Log.d("createInboxOnFirebaseChange", "is is null");
                return false;
            } else if (inbox.getContent() == null || inbox.getContent().isEmpty()) {
                Log.d("createInboxOnFirebaseChange", "content is null");
                return false;
            } else if (inbox.getCreatedDate() == null || inbox.getCreatedDate().isEmpty()) {
                Log.d("createInboxOnFirebaseChange", "createdDate is null");
                return false;
            } else if (inbox.getType() == null || inbox.getType().isEmpty()) {
                Log.d("createInboxOnFirebaseChange", "type is null");
                return false;
            } else if (inbox.getUserRecieveRequest() == null) {
                Log.d("createInboxOnFirebaseChange", "user1 is null");
                return false;
            } else if (inbox.getUserSentRequest() == null) {
                Log.d("createInboxOnFirebaseChange", "user2 is null");
                return false;
            } else {
                Log.d("createInbox : ", "LOCAL FIREBASE");
                ContentValues contentValues = new ContentValues();
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_ID, inbox.getId());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_CREATED_DATE, inbox.getCreatedDate());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_CONTENT, inbox.getContent());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_TYPE, inbox.getType());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_IS_READ, inbox.isRead() ? 1 : 0);
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE, inbox.isActive() ? 1 : 0);
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_USER_1, inbox.getUserRecieveRequest().getId());
                contentValues.put(MySQLiteHelper.COLUMN_INBOX_USER_2, inbox.getUserSentRequest().getId());
                result = db.insert(MySQLiteHelper.TABLE_INBOX, null, contentValues) > 0;
                Log.d("createInbox : ", "LOCAL FIREBASE");
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.d("createInboxOnFirebaseChange", ex.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public boolean deleteInboxOnFirebaseChange(String id) {
        db.beginTransaction();
        boolean result = false;
        try {
            Log.d("DeleteInbox : ", "LOCAL FIREBASE");
            result = db.delete(MySQLiteHelper.TABLE_INBOX,
                    MySQLiteHelper.COLUMN_INBOX_ID + " = ? ", new String[]{id}) > 0;
            db.setTransactionSuccessful();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public List<Inbox> getAllInboxesByUserId(String id) {
        Set<Inbox> inboxSet = new HashSet<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_INBOX
                + " WHERE " + MySQLiteHelper.COLUMN_INBOX_USER_1 + "  = ? ", new String[]{id})) {
            if (c != null) {
                while (c.moveToNext()) {
                    Inbox inbox = getInboxByCursor(c);
                    if (inbox != null) {
                        inboxSet.add(inbox);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return inboxSet.stream().collect(Collectors.toList());
    }

    @Override
    public Inbox findInboxFriendRequest(String userId1, String userId2) {
        Inbox inbox = null;
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_INBOX
                + " WHERE " + MySQLiteHelper.COLUMN_INBOX_USER_1 + " = ? AND " + MySQLiteHelper.COLUMN_INBOX_USER_2 + " = ?  OR "
                + MySQLiteHelper.COLUMN_INBOX_USER_1 + " = ? AND " + MySQLiteHelper.COLUMN_INBOX_USER_2 + " = ? "
                + " ORDER BY " + MySQLiteHelper.COLUMN_INBOX_CREATED_DATE + " DESC "
                + " LIMIT 1", new String[]{userId1, userId2, userId2, userId1})) {

            if (c.moveToFirst()) {
                inbox = this.getInboxByCursor(c);
            }
        }
        return inbox;
    }

    @Override
    public Inbox findInbox(Inbox inbox) {
        Inbox inbox1 = null;
        try (Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_INBOX
                + " WHERE " + MySQLiteHelper.COLUMN_INBOX_ID + " = ? ", new String[]{inbox.getId()})) {
            if (c != null && c.moveToFirst()) {
                if (getInboxByCursor(c) != null) {
                    inbox1 = getInboxByCursor(c);
                }
            }
        }
        return inbox1;
    }

    @Override
    public boolean deleteInbox(Inbox inbox) {
        db.beginTransaction();
        boolean result = false;
        try {
            Log.d("DeleteInbox : ", "LOCAL");
            if (findInbox(inbox) != null) {
                result = db.delete(MySQLiteHelper.TABLE_INBOX,
                        MySQLiteHelper.COLUMN_INBOX_ID + " = ? ", new String[]{inbox.getId()}) > 0;
                if (result) {
                    firebaseInboxDataSource.deleteInbox(inbox);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception exception) {
            Log.d("deleteInbox", exception.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public boolean updateInbox(Inbox inbox) {
        db.beginTransaction();
        boolean result = false;
        try {
            Log.d("UpdateInbox : ", "LOCAL");
            ContentValues contentValues = new ContentValues();
            contentValues.put(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE, inbox.isActive());
            contentValues.put(MySQLiteHelper.COLUMN_INBOX_IS_READ, inbox.isRead());
            if (findInbox(inbox) != null && !findInbox(inbox).equals(inbox)) {
                result = db.update(MySQLiteHelper.TABLE_INBOX, contentValues,
                        MySQLiteHelper.COLUMN_INBOX_ID + " = ? ", new String[]{inbox.getId()}) > 0;
                if (result) {
                    firebaseInboxDataSource.updateInbox(inbox);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.d("updateInbox", ex.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public boolean updateInboxOnFirebaseChange(Inbox inbox) {
        db.beginTransaction();
        boolean result = false;
        try {
            Log.d("UpdateInbox : ", "LOCAL FIREBASE");
            ContentValues contentValues = new ContentValues();
            contentValues.put(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE, inbox.isActive());
            contentValues.put(MySQLiteHelper.COLUMN_INBOX_IS_READ, inbox.isRead());
            result = db.update(MySQLiteHelper.TABLE_INBOX, contentValues,
                    MySQLiteHelper.COLUMN_INBOX_ID + " = ? ", new String[]{inbox.getId()}) > 0;
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.d("updateInbox", ex.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    Inbox getInboxByCursor(Cursor c) {
        // get index of each columns
        int id = c.getColumnIndex(MySQLiteHelper.COLUMN_INBOX_ID);
        int createdDate = c.getColumnIndex(MySQLiteHelper.COLUMN_INBOX_CREATED_DATE);
        int content = c.getColumnIndex(MySQLiteHelper.COLUMN_INBOX_CONTENT);
        int type = c.getColumnIndex(MySQLiteHelper.COLUMN_INBOX_TYPE);
        int isRead = c.getColumnIndex(MySQLiteHelper.COLUMN_INBOX_IS_READ);
        int isActive = c.getColumnIndex(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE);
        int user1 = c.getColumnIndex(MySQLiteHelper.COLUMN_INBOX_USER_1);
        int user2 = c.getColumnIndex(MySQLiteHelper.COLUMN_INBOX_USER_2);

        // set value to new inbox
        Inbox inbox = new Inbox();
        inbox.setId(c.getString(id));
        inbox.setCreatedDate(c.getString(createdDate));
        inbox.setContent(c.getString(content));
        inbox.setType(c.getString(type));
        inbox.setRead(c.getInt(isRead) == 1);
        inbox.setActive(c.getInt(isActive) == 1);
        inbox.setUserRecieveRequest(userDataSource.getUserById(c.getString(user1)));
        inbox.setUserSentRequest(userDataSource.getUserById(c.getString(user2)));
        inbox.setInboxType(Inbox.InboxType.valueOf(inbox.getType()));

        return inbox;
    }
}
