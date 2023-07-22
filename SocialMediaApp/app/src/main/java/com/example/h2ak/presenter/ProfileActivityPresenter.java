package com.example.h2ak.presenter;

import com.example.h2ak.MyApp;
import com.example.h2ak.contract.ProfileActivityContract;
import com.example.h2ak.datasource.UserDataSource;
import com.example.h2ak.datasource.datasourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivityPresenter implements ProfileActivityContract.Presenter {

    private ProfileActivityContract.View view;
    private UserDataSource userDataSource;
    private FirebaseAuth firebaseAuth;

    public ProfileActivityPresenter(ProfileActivityContract.View view) {
        this.view = view;
        userDataSource = new UserDataSourceImpl(MyApp.getInstance());
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = userDataSource.getUserByEmail(firebaseUser.getEmail());
        view.loadUserInformation(user);
    }
}
