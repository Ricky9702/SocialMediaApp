package com.example.h2ak.presenter;

import android.content.Context;

import com.example.h2ak.MyApp;
import com.example.h2ak.contract.ProfileActivityContract;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivityPresenter implements ProfileActivityContract.Presenter {

    private ProfileActivityContract.View view;
    private UserDataSource userDataSource;
    private FirebaseAuth firebaseAuth;

    public ProfileActivityPresenter(Context context, ProfileActivityContract.View view) {
        this.view = view;
        userDataSource = UserDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void getUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            User user = userDataSource.getUserById(firebaseUser.getUid());
            view.loadUserInformation(user);
        }
    }
}
