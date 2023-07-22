package com.example.h2ak.presenter;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
    private FirebaseAuthListener firebaseAuthListener;
    private LoginActivityContract.View view;

    private FirebaseAuth.AuthStateListener authStateListener = mAuth -> {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            boolean isVerified = firebaseUser.isEmailVerified();
            if (firebaseAuthListener != null) {
                firebaseAuthListener.onEmailVerified(isVerified);
            }
        }

    };

    public LoginActivtyPresenter(LoginActivityContract.View view) {
        this.view = view;
        firebaseAuth = FirebaseAuth.getInstance();
        userDataSource = new UserDataSourceImpl(MyApp.getInstance());

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void login(String email, String password) {
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
                if (user != null) {
                    if (user.isActive()) {
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
                view.showProgressbar(false);
            } else {
                // Login failed, notify failure
                view.showProgressbar(false);
                view.onLoginFailure(task.getException().getMessage());
            }
        });
    }

    public void updateUserActive(String email, boolean isActive) {
        Log.d("TAG", "Updating active status for email: " + email + ", isActive: " + isActive);
        User user = userDataSource.getUserByEmail(email);
        if (user != null) {
            user.setActive(isActive);
            boolean success = userDataSource.updateActiveUser(user);
            Log.d("TAG", "Update success: " + success);
        }
    }

    public void setFirebaseAuthListener(FirebaseAuthListener listener) {
        this.firebaseAuthListener = listener;
    }

    public interface FirebaseAuthListener {
        void onEmailVerified(boolean isVerified);
    }

}
