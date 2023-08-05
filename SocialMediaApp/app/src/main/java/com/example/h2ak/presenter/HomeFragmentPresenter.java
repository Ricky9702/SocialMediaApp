package com.example.h2ak.presenter;


import android.content.Context;

import com.example.h2ak.contract.HomeFragmentContract;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeFragmentPresenter implements HomeFragmentContract.Presenter {

    private UserDataSource userDataSoruce;
    private HomeFragmentContract.View view;
    private FirebaseAuth firebaseAuth;

    public HomeFragmentPresenter(Context context, HomeFragmentContract.View view) {
        this.view = view;
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
}
