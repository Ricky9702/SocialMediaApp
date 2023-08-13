package com.example.h2ak.presenter;


import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.contract.UserProfileAcitivtyContract;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserProfileAcitivtyPresenter implements UserProfileAcitivtyContract.Presenter {

    private UserProfileAcitivtyContract.View view;
    private Context context;
    private UserDataSource userDataSource;
    private FriendShipDataSource friendShipDataSource;
    private InboxDataSource inboxDataSource;
    private FirebaseAuth firebaseAuth;
    private PostDataSource postDataSource;

    public UserProfileAcitivtyPresenter(UserProfileAcitivtyContract.View view, Context context) {
        this.view = view;
        this.context = context;
        postDataSource = PostDataSourceImpl.getInstance(context);
        userDataSource = UserDataSourceImpl.getInstance(context);
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
        inboxDataSource = InboxDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void getUserById(String id) {
        User currentUser = userDataSource.getUserById(firebaseAuth.getUid());
        User user = userDataSource.getUserById(id);
        FriendShip friendShip = friendShipDataSource.findLastestFriendShip(currentUser, user);
        if (friendShip != null)
            view.onStatusReceived(friendShip.getStatus());
        if (user != null) view.onUserRecieved(user);
    }

    @Override
    public void createOrUpdateFriendRequest(User user2) {
        User currentUser = userDataSource.getUserById(firebaseAuth.getUid());
        FriendShip friendShip = friendShipDataSource.findLastestFriendShip(currentUser, user2);
        view.showProgressBar(true);
        // already exists friendship
        if (friendShip != null) {
            Log.d("UserProfilePresenter", friendShip.getStatus() + "");
            Log.d("UserProfilePresenter", friendShip.getCreatedDate() + "");
            Log.d("UserProfilePresenter", friendShip.getUser1().getId() + "");
            Log.d("UserProfilePresenter", friendShip.getUser2().getId() + "");
            // create popup menu delete, if selected friends
            friendShip.setCreatedDate(new FriendShip().getCreatedDate());
            if (friendShip.getStatus().equals(FriendShip.FriendShipStatus.ACCEPTED.getStatus())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                FriendShip finalFriendShip = friendShip;
                builder.setTitle("Delete Friend")
                        .setMessage("Are you sure want to delete " + user2.getName() + " from friend list?")
                        .setNegativeButton("NO", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setPositiveButton("YES", (dialogInterface, i) -> {
                            finalFriendShip.setStatus(FriendShip.FriendShipStatus.DELETED.getStatus());
                            friendShipDataSource.updateFriendShip(finalFriendShip);
                            view.onStatusReceived(finalFriendShip.getStatus());
                            view.onSendMessage("Delete " + user2.getName() + " from friend list successfully.");
                            dialogInterface.dismiss();
                        })
                        .create().show();

            }
            //Send friend requests then Cancel requests
            else if (friendShip.getStatus().equals(FriendShip.FriendShipStatus.PENDING.getStatus())) {
                friendShip.setStatus(FriendShip.FriendShipStatus.DELETED.getStatus());
                if (friendShipDataSource.updateFriendShip(friendShip)) {
                    view.onStatusReceived(friendShip.getStatus());

                    // delete the inbox from the user recieve request
                    Inbox inbox = inboxDataSource.findInboxFriendRequest(user2.getId(), currentUser.getId());
                    if (inbox != null) {
                        Log.d("createOrUpdateFriendRequest", "createOrUpdateFriendRequest:  " + inbox.getId());
                        if (inboxDataSource.deleteInbox(inbox)) {
                            Log.d("UserProfilePresenter", "DELETED SUCCESSFULLY");
                        } else {
                            Log.d("UserProfilePresenter", "DELETED FAILED");
                        }
                    } else {
                        Log.d("UserProfilePresenter", "Inbox not found, might not be created yet");
                    }
                    view.onSendMessage("Cancel send the request successfully.");
                }
            } // Cancel request then send friend requests
            else if (friendShip.getStatus().equals(FriendShip.FriendShipStatus.DELETED.getStatus())) {
                friendShip.setStatus(FriendShip.FriendShipStatus.PENDING.getStatus());
                boolean test = friendShipDataSource.updateFriendShip(friendShip);
                Log.d("createOrUpdateFriendRequest", "test: " + test);
                if (test) {
                    view.onStatusReceived(friendShip.getStatus());
                    createNewInbox(currentUser, user2);
                } else {
                    view.onSendFriendRequestFailed("Send friend request failed!!");
                }
            }
            // there is no record friendship between these 2 users
        } else { // create a new friendship
            friendShip = new FriendShip(currentUser, user2);
            if (friendShipDataSource.createFriendShip(friendShip)) {
                view.onStatusReceived(friendShip.getStatus());
                createNewInbox(currentUser, user2);
            } else {
                view.onSendFriendRequestFailed("Send friend request failed!!");
            }
        }
        view.showProgressBar(false);
    }

    @Override
    public void getAllPostByUserId(String id, String privacy1, String privacy2) {
        List<Post> postList = new ArrayList<>();
        postList.addAll(postDataSource.getAllPostByUserIdWithPrivacy(id, privacy1, privacy2));
        Collections.sort(postList, (post1, post2) -> {
            Date date1 = TextInputLayoutUtils.parseDateFromString(post1.getCreatedDate());
            Date date2 = TextInputLayoutUtils.parseDateFromString(post2.getCreatedDate());

            // Sorting in descending order (latest items first)
            return date2.compareTo(date1);
        });
        view.onListPostRecieved(postList);
    }

    private void createNewInbox(User currentUser, User user2) {
        // create a new inbox for user 2
        Inbox inbox = new Inbox();
        // set content as a friend request from user 1
        String message = Inbox.FRIEND_REQUEST_MESSAGE.replace("{{senderName}}", currentUser.getName());

        inbox.setContent(message);
        inbox.setCreatedDate(inbox.getCreatedDate());
        inbox.setInboxType(Inbox.InboxType.FRIEND_REQUEST);
        inbox.setType(Inbox.InboxType.FRIEND_REQUEST.getType());
        inbox.setUserSentRequest(currentUser);
        inbox.setUserRecieveRequest(user2);
        inbox.setRead(false);
        inbox.setActive(true);

        if (inboxDataSource.createInbox(inbox)) {
            view.onSendMessage(String.format("Send friend request successfully to %s !!", user2.getName()));
            Log.d("UserProfilePresenter", "Create inbox sucess");
        } else
            Log.d("UserProfilePresenter", "Create inbox failed");
    }

}
