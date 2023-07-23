package com.example.h2ak.presenter;

import android.util.Patterns;
import android.widget.Toast;

import com.example.h2ak.MyApp;
import com.example.h2ak.contract.RegisterActivityContract;
import com.example.h2ak.datasource.UserDataSource;
import com.example.h2ak.datasource.datasourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.PasswordHashing;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivityPresenter implements RegisterActivityContract.Presenter {

    private RegisterActivityContract.View view;

    private FirebaseAuth firebaseAuth;
    private UserDataSource userDataSource;


    public RegisterActivityPresenter(RegisterActivityContract.View view) {
        this.view = view;
        firebaseAuth = FirebaseAuth.getInstance();
        userDataSource = new UserDataSourceImpl(MyApp.getInstance());
        FirebaseUser user = firebaseAuth.getCurrentUser();
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
        } else {
                    user = new User();
                    user.setName(name);
                    user.setGender(gender);
                    user.setBirthday(birthday);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setActive(false);
        }
        return user;
    }

    @Override
    public void register(User user) {
        view.showProgressBar(true);
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                addNonverifiedUser(user);
                firebaseUser.sendEmailVerification().addOnSuccessListener(task1 -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyApp.getInstance(), "Verification email sent.", Toast.LENGTH_LONG).show();
                        view.onRegisterSuccess();
                    }
                }).addOnFailureListener(runnable -> {Toast.makeText(MyApp.getInstance(), "Failed to send verification email.", Toast.LENGTH_LONG).show();});
            }
            view.showProgressBar(false);
        }).addOnFailureListener(task -> {
            Toast.makeText(MyApp.getInstance(), "Email is already exists", Toast.LENGTH_SHORT).show();
            view.showProgressBar(false);
        });
    }

    @Override
    public void addNonverifiedUser(User user) {
        userDataSource.addUser(user);
    }
}
