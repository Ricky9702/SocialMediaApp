package com.example.h2ak.presenter;


import android.content.Context;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.contract.HomeFragmentContract;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class HomeFragmentPresenter implements HomeFragmentContract.Presenter {
    private HomeFragmentContract.View view;
    private FirebaseAuth firebaseAuth;
    private UserDataSource userDataSoruce;
    private PostDataSource postDataSource;
    private FriendShipDataSource friendShipDataSource;

    public HomeFragmentPresenter(Context context, HomeFragmentContract.View view) {
        this.view = view;
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
        postDataSource = PostDataSourceImpl.getInstance(context);
        userDataSoruce = UserDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void loadCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            User u = userDataSoruce.getUserById(firebaseUser.getUid());
            view.changeProfileAvatar(u);
        }
    }

    @Override
    public void loadFriendsPost() {
        Set<User> friends = new HashSet<>();
        if (MyApp.getInstance().getCurrentUser() != null) {
            friends = friendShipDataSource.getFriendsByUser(MyApp.getInstance().getCurrentUser());
        }

        Set<Post> postSet = new HashSet<>();
        if (!friends.isEmpty()) {
            friends.forEach(user -> {
                Set<Post> friendPost = postDataSource.getAllPostByUserId(user.getId());
                if (!friendPost.isEmpty()) {
                    friendPost.forEach(post -> postSet.add(post));
                }
            });
        }
        // Shuffle post
        List<Post> postList = new ArrayList<>();
        if (!postSet.isEmpty()) {
            postList.addAll(postSet.stream()
                    .filter(post -> !post.getPrivacy().equals(Post.PostPrivacy.ONLY_ME.getPrivacy())).collect(Collectors.toList()));
            Collections.shuffle(postList);
        }
        view.onListPostReceived(postList);
    }
}
