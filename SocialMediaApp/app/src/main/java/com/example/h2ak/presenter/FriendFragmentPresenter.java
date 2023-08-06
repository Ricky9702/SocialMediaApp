package com.example.h2ak.presenter;

import android.content.Context;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.contract.FriendFragmentContract;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FriendFragmentPresenter implements FriendFragmentContract.Presenter {

    private Context context;
    private FriendFragmentContract.View view;
    private FriendShipDataSource friendShipDataSource;
    private UserDataSource userDataSource;
    private FirebaseAuth firebaseAuth;

    public FriendFragmentPresenter(FriendFragmentContract.View view, Context context) {
        this.context = context;
        this.view = view;
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
        userDataSource = UserDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getFriendList() {
        User currentUser = userDataSource.getUserById(firebaseAuth.getCurrentUser().getUid());
        Log.d("currentUser: ", currentUser.getEmail());
        Set<FriendShip> friendShipSet = friendShipDataSource.getAllFriendShipByUser(currentUser);
        Log.d("friendShipSet", friendShipSet.size()+"");
        List<User> userList = new ArrayList<>();
        if (!friendShipSet.isEmpty() && friendShipSet != null) {
            friendShipSet.forEach(friendShip -> {
                // choose which user is friend and display on ui
                if (friendShip.getUser1().getId().equals(currentUser.getId())) {
                    userList.add(friendShip.getUser2());
                } else {
                    userList.add(friendShip.getUser1());
                }
            });
            view.onFriendListRecieved(userList);
        } else {
            Log.d("FriendFragmentPresenter", "getFriendList: size = 0");
        }
    }
}
