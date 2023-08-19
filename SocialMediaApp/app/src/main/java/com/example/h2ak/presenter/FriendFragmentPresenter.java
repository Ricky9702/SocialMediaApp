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
import java.util.stream.Collectors;

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
        User currentUser = userDataSource.getUserById(MyApp.getInstance().getCurrentUser().getId());
        Log.d("currentUser: ", currentUser.getEmail());
        view.onFriendListRecieved(new ArrayList<>(friendShipDataSource.getFriendsByUser(currentUser)));
    }
}
