package com.example.h2ak.presenter;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.contract.LoginActivityContract;
import com.example.h2ak.datasource.UserDataSource;
import com.example.h2ak.datasource.datasourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivtyPresenter implements LoginActivityContract.Presenter {

    private FirebaseAuth firebaseAuth;
    private UserDataSource userDataSource;
    private LoginActivityContract.View view;


    public LoginActivtyPresenter(LoginActivityContract.View view) {
        this.view = view;
        firebaseAuth = FirebaseAuth.getInstance();
        userDataSource = new UserDataSourceImpl(MyApp.getInstance());

    }


    @Override
    public void login(String email, String password, LoginActivityContract.EmailVerifyingListener listener) {
        view.showProgressbar(true);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // Input validation failed, notify failure
            view.onLoginFailure("Email and password cannot be empty");
            view.showProgressbar(false);
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                User user = userDataSource.getUserByEmail(firebaseUser.getEmail());
                listener.onEmailVerified(firebaseUser.isEmailVerified());
                if (firebaseUser != null && user != null) {
                    if (firebaseUser.isEmailVerified()) {
                        // User is active, notify success
                        view.onLoginSuccess();
                    } else {
                        // User is not active, notify failure
                        view.onLoginFailure("Please verify your email");
                    }
                } else {
                    // User not found in local database, notify failure
                    view.onLoginFailure("User not found");
                }
            } else {
                // Login failed, notify failure
                view.onLoginFailure(task.getException().getMessage());
            }
            view.showProgressbar(false);
        });
    }




    public void updateUserWithEmailVerified(boolean isActive) {
        User user = userDataSource.getUserByEmail(firebaseAuth.getCurrentUser().getEmail());
        if (user != null && !user.isActive()) {
            user.setActive(isActive);
            boolean success = userDataSource.updateUser(user);
            Log.d("TAG", "Update success: " + success);
        }
    }


}
