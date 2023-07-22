package com.example.h2ak.presenter;


import android.content.Context;

import com.example.h2ak.MyApp;
import com.example.h2ak.contract.BaseMenuContract;
import com.example.h2ak.datasource.UserDataSource;
import com.example.h2ak.datasource.datasourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class BaseMenuPresenter implements BaseMenuContract.Presenter {

    private UserDataSource userDataSoruce;
    private BaseMenuContract.View view;
    private FirebaseAuth firebaseAuth;

    public BaseMenuPresenter(BaseMenuContract.View view) {
        this.view = view;
        userDataSoruce = new UserDataSourceImpl(MyApp.getInstance());
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void getUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            User u = userDataSoruce.getUserByEmail(firebaseUser.getEmail());
            view.changeProfileAvatar(u);
        }
    }
}
