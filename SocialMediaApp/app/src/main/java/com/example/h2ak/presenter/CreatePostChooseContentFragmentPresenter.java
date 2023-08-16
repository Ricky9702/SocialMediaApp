package com.example.h2ak.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxPostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxPostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostImagesDataSourceImpl;
import com.example.h2ak.contract.CreatePostChooseContentFragmentContract;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.model.InboxPost;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.model.User;

import java.io.File;
import java.util.List;
import java.util.Set;

public class CreatePostChooseContentFragmentPresenter implements CreatePostChooseContentFragmentContract.Presenter{

    private PostDataSource postDataSource;
    private PostImagesDataSource postImagesDataSource;
    private CreatePostChooseContentFragmentContract.View view;
    private Context context;
    private InboxPostDataSource inboxPostDataSource;
    private InboxDataSource inboxDataSource;
    private FriendShipDataSource friendShipDataSource;

    public CreatePostChooseContentFragmentPresenter(Context context, CreatePostChooseContentFragmentContract.View view) {
        this.context = context;
        this.view = view;
        postDataSource = PostDataSourceImpl.getInstance(context);
        postImagesDataSource = PostImagesDataSourceImpl.getInstance(context);
        inboxPostDataSource = InboxPostDataSourceImpl.getInstance(context);
        inboxDataSource = InboxDataSourceImpl.getInstance(context);
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
    }

    @Override
    public void createPost(Post post, List<PostImages> postImagesList) {
        view.showProgressbar(true);
        if(postDataSource.createPost(post)) {
            postImagesList.forEach(postImages -> {
                FirebaseHelper.uploadImageToFirebaseCloud(context, Uri.fromFile(new File(postImages.getImageUrl()))
                        , FirebaseHelper.getImagePostStorageRef(), new FirebaseHelper.OnImageUploadListener() {
                            @Override
                            public void onImageUploadSuccess(String imageUrl) {
                                postImages.setImageUrl(imageUrl);
                                postImagesDataSource.createPostImages(postImages);
                            }

                            @Override
                            public void onImageUploadFailure(String errorMessage) {

                            }
                        });
            });
            view.showProgressbar(false);
            view.showMessage("Create post successfully!!");

            // send notifications to friends of the post owner about a new post has just created.

            Set<User> friends = friendShipDataSource.getFriendsByUser(post.getUser());
            if (!friends.isEmpty()) {
                String message = Inbox.CREATE_NEW_POST.replace("{{senderName}}", post.getUser().getName());

                friends.forEach(friend -> {
                    if (friend != null) {
                        Inbox inbox = new Inbox(message, Inbox.InboxType.POST_MESSAGE, friend, post.getUser());
                        if (inboxDataSource.createInbox(inbox))  {
                            InboxPost inboxPost = new InboxPost();
                            inboxPost.setId(inbox.getId());
                            inboxPost.setPost(post);
                            inboxPostDataSource.create(inboxPost);

                            Log.d("Create inbox for friends", "createInbox: success");
                        }else Log.d("Create inbox for friends", "createInbox: failed");
                    }
                });

            }
        } else {
            view.showProgressbar(false);
            view.showMessage("Failed to create post!!");
        }
    }
}
