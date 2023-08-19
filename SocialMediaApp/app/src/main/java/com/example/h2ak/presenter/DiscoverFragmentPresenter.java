package com.example.h2ak.presenter;

import android.content.Context;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.contract.DiscoverFragmentContract;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscoverFragmentPresenter implements DiscoverFragmentContract.Presenter {
    private DiscoverFragmentContract.View view;
    private Context context;
    private PostDataSource postDataSource;
    private UserDataSource userDataSource;
    private FriendShipDataSource friendShipDataSource;

    public DiscoverFragmentPresenter(Context context, DiscoverFragmentContract.View view) {
        this.context = context;
        userDataSource = UserDataSourceImpl.getInstance(context);
        postDataSource = PostDataSourceImpl.getInstance(context);
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
        this.view = view;
    }

    @Override
    public void getRandomPostList() {

        List<Post> postList = new ArrayList<>();
        if (!postDataSource.getRandomPost().isEmpty()) {
            postList.addAll(postDataSource.getRandomPost());
        }
        view.onRandomPostListReceived(postList);
    }

    @Override
    public void getRandomUser() {
        Set<User> suggestUser = new HashSet<>();
        Log.d("TAG", "getRandomUser: userDataSource" + userDataSource.getAllUsers(null).size());
        Set<User> friends = friendShipDataSource.getFriendsByUser(MyApp.getInstance().getCurrentUser());

        if (friends.isEmpty()) {
            // friend is empty, so suggest random user
            suggestUser.addAll(userDataSource.getAllUsers(null).stream().filter(user -> user.isActive() && !user.getId().equals(MyApp.getInstance().getCurrentUserId())).collect(Collectors.toList()));

        } else {
            // suggest mutual friend
            suggestUser.removeAll(friendShipDataSource.getFriendsByUser(userDataSource.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid())));

            friends.forEach(friend -> {
                Set<User> mutual = friendShipDataSource.getMutualFriends(friend, MyApp.getInstance().getCurrentUser());
                suggestUser.addAll(mutual);
            });
        }

          view.onSuggestUserListReceived(suggestUser.stream().collect(Collectors.toList()));
    }
}
