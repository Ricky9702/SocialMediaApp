package com.example.h2ak.Firebase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.model.User;
import com.example.h2ak.view.activities.BaseMenuActivity;
import com.example.h2ak.view.activities.LoginActivity;
import com.example.h2ak.view.activities.UserProfileActivity;
import com.example.h2ak.view.fragments.FriendFragment;
import com.example.h2ak.view.fragments.HomeFragment;
import com.example.h2ak.view.fragments.InboxFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDataSync {
    // User
    private DatabaseReference userRef;
    private UserDataSource userDataSource;

    // FriendShip
    private DatabaseReference friendShipRef;
    private FriendShipDataSource friendShipDataSource;

    // Inbox
    private DatabaseReference inBoxRef;
    private InboxDataSource inboxDataSource;

    private Context context;
    private static FirebaseDataSync instance;
    private String currentUserId;

    public FirebaseDataSync(Context context, String currentUserId) {
        // User
        userRef = FirebaseHelper.getDatabaseReferenceByPath("User");
        userDataSource = UserDataSourceImpl.getInstance(context);

        friendShipRef = FirebaseHelper.getDatabaseReferenceByPath("FriendShip");
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);

        inBoxRef = FirebaseHelper.getDatabaseReferenceByPath("Inbox");
        inboxDataSource = InboxDataSourceImpl.getInstance(context);
        this.currentUserId = currentUserId;
    }

    public static FirebaseDataSync getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseDataSync(context, MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    public void syncUser(OnDataChangeListener listener) {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SyncUser: ", "There is some change here!!");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {
                        String id = snapshot.getKey();
                        User existingUser = userDataSource.getUserById(id);

                        User user = new User();
                        user.setId(snapshot.child(MySQLiteHelper.COLUMN_USER_ID).getValue(String.class));
                        user.setActive(Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_USER_IS_ACTIVE).getValue(Boolean.class)));
                        user.setOnline(Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_USER_IS_ONLINE).getValue(Boolean.class)));
                        user.setRole(snapshot.child(MySQLiteHelper.COLUMN_USER_USER_ROLE).getValue(String.class));
                        user.setBirthday(snapshot.child(MySQLiteHelper.COLUMN_USER_BIRTHDAY).getValue(String.class));
                        user.setBio(snapshot.child(MySQLiteHelper.COLUMN_USER_BIO).getValue(String.class));
                        user.setPassword(snapshot.child(MySQLiteHelper.COLUMN_USER_PASSWORD).getValue(String.class));
                        user.setEmail(snapshot.child(MySQLiteHelper.COLUMN_USER_EMAIL).getValue(String.class));
                        user.setGender(snapshot.child(MySQLiteHelper.COLUMN_USER_GENDER).getValue(String.class));
                        user.setName(snapshot.child(MySQLiteHelper.COLUMN_USER_NAME).getValue(String.class));
                        user.setCreatedDate(snapshot.child(MySQLiteHelper.COLUMN_USER_CREATED_DATE).getValue(String.class));
                        user.setImageCover(snapshot.child(MySQLiteHelper.COLUMN_USER_IMAGE_COVER).getValue(String.class));
                        user.setImageAvatar(snapshot.child(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR).getValue(String.class));

                        if (existingUser == null) {
                            userDataSource.createUser(user);
                            listener.onDataChange();
                        } else {
                            userDataSource.UpdateUserChangeOnFirebase(user);
                            listener.onDataChange();
                        }
                    }
                }

//                if (MyApp.getInstance().getCurrentActivity() instanceof BaseMenuActivity) {
//                    Fragment currentFragment = ((BaseMenuActivity) MyApp.getInstance().getCurrentActivity()).getSupportFragmentManager().findFragmentById(R.id.frameLayout);
//                    if (currentFragment instanceof FriendFragment) {
//                        ((FriendFragment) currentFragment).getPresenter().getFriendList();
//                    } else if (currentFragment instanceof HomeFragment) {
//                        ((HomeFragment) currentFragment).getPresenter().loadCurrentUser();
//                    }
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void syncFriendShip(OnDataChangeListener listener) {
        friendShipRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SyncFriendShip: ", "There is some change here!!");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String userId1 = snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1).getValue(String.class);
                    String userId2 = snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2).getValue(String.class);

                    if (userId1 != null && userId2 != null) {
                        User user1 = userDataSource.getUserById(userId1);
                        User user2 = userDataSource.getUserById(userId2);
                        if (user1 != null && user2 != null) {
                            FriendShip friendShip = new FriendShip();

                            friendShip.setId(snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_ID).getValue(String.class));
                            friendShip.setCreatedDate(snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE).getValue(String.class));
                            friendShip.setStatus(snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS).getValue(String.class));
                            friendShip.setUser1(user1);
                            friendShip.setUser2(user2);

                            FriendShip found = friendShipDataSource.findFriendShipFirebase(friendShip);

                            if (found == null) {
                                Log.d("SyncFriendShip: ", "There is some change here!! X2");
                                friendShipDataSource.createFriendShipOnFirebaseChange(friendShip);
                                listener.onDataChange();
                            }
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void syncInbox(OnDataChangeListener listener) {
        inBoxRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SyncInbox: ", "There is some change here!!");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId1 = snapshot.child(MySQLiteHelper.COLUMN_INBOX_USER_1).getValue(String.class);
                    String userId2 = snapshot.child(MySQLiteHelper.COLUMN_INBOX_USER_2).getValue(String.class);

                    if (userId1 != null && userId2 != null) {
                        User user1 = userDataSource.getUserById(userId1);
                        User user2 = userDataSource.getUserById(userId2);

                        if (user1 != null && user2 != null) {

                            Inbox inbox = new Inbox();
                            inbox.setId(snapshot.child(MySQLiteHelper.COLUMN_INBOX_ID).getValue(String.class));
                            inbox.setContent(snapshot.child(MySQLiteHelper.COLUMN_INBOX_CONTENT).getValue(String.class));
                            inbox.setCreatedDate(snapshot.child(MySQLiteHelper.COLUMN_INBOX_CREATED_DATE).getValue(String.class));
                            inbox.setType(snapshot.child(MySQLiteHelper.COLUMN_INBOX_TYPE).getValue(String.class));
                            inbox.setRead(Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_INBOX_IS_READ).getValue(Boolean.class)));
                            inbox.setActive(Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE).getValue(Boolean.class)));
                            inbox.setUserRecieveRequest(user1);
                            inbox.setUserSentRequest(user2);

                            if (inboxDataSource.findInboxOnFirebaseChange(inbox) != null) {
                                // update
                                inboxDataSource.updateInboxOnFirebaseChange(inbox);
                            } else {
                                // create
                                inboxDataSource.createInboxOnFirebaseChange(inbox);
                                listener.onDataChange();
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        inBoxRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    inboxDataSource.deleteInboxOnFirebaseChange(snapshot.getKey());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnDataChangeListener{
        void onDataChange();
    }

}
