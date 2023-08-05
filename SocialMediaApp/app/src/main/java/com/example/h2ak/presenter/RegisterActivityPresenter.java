package com.example.h2ak.presenter;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseUserDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseUserDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.contract.RegisterActivityContract;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.PasswordHashing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivityPresenter implements RegisterActivityContract.Presenter {

    private RegisterActivityContract.View view;
    private FirebaseAuth firebaseAuth;
    private UserDataSource userDataSource;
    private FirebaseUserDataSource firebaseUserDataSource;


    public RegisterActivityPresenter(Context context, RegisterActivityContract.View view) {
        this.view = view;
        userDataSource = UserDataSourceImpl.getInstance(context);
        firebaseUserDataSource = FirebaseUserDataSourceImpl.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public User createUser(String name, String birthday, String gender, String email, String password, String confirmPassword) {
        User user = null;
        if (name.isEmpty() || name == null) {
            view.showError("Name can not be empty!!");
        } else if (birthday.isEmpty() || birthday == null) {
            view.showError("Birthday can not be empty!!");
        } else if (gender.isEmpty() || gender == null) {
            view.showError("Gender can not be empty!!");
        } else if (email.isEmpty() || email == null) {
            view.showError("Email can not be empty!!");
        } else if (password.isEmpty() || password == null) {
            view.showError("Password can not be empty!!");
        } else if (confirmPassword.isEmpty() || confirmPassword == null) {
            view.showError("Please confirm your password!!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showError("Please check your email pattern, ex: abc@mail.com");
        } else if (!password.equals(confirmPassword)) {
            view.showError("Passwords do not match!!");
        } else if (password.length() < 6){
            view.showError("Password length should be greater than 6");
        } else {
            user = new User();
            user.setName(name);
            user.setGender(gender);
            user.setBirthday(birthday);
            user.setEmail(email);
            user.setPassword(password);
            user.setBio("");
            user.setImageAvatar("");
            user.setImageCover("");
            user.setActive(false);
            user.setUserRole(User.UserRole.ROLE_USER);
            user.setOnline(false);
        }
        return user;
    }

    @Override
    public void register(User user) {
        view.showProgressBar(true);
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                //Add user
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                user.setPassword(PasswordHashing.hashPassword(user.getPassword()));
                user.setId(currentUser.getUid());
                addUnverifiedUser(user);
                firebaseUserDataSource.createUser(user);

                //Send email
                currentUser.sendEmailVerification().addOnSuccessListener(task1 -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyApp.getInstance(), "Verification email sent.", Toast.LENGTH_LONG).show();
                        view.onRegisterSuccess();
                    }
                }).addOnFailureListener(runnable -> {
                    Toast.makeText(MyApp.getInstance(), "Failed to send verification email.", Toast.LENGTH_LONG).show();
                });
            }
            view.showProgressBar(false);
        }).addOnFailureListener(task -> {
            Toast.makeText(MyApp.getInstance(), task.getMessage(), Toast.LENGTH_SHORT).show();
            view.showProgressBar(false);
        });
    }

    @Override
    public void addUnverifiedUser(User user) {
        userDataSource.createUser(user);
    }
}
