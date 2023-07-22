package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.h2ak.R;
import com.example.h2ak.contract.LoginActivityContract;
import com.example.h2ak.presenter.LoginActivtyPresenter;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements LoginActivityContract.View {
    private TextView textViewResetPassword;
    private TextInputLayout textLayoutPassword, textLayoutEmail;
    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnGoToRegister;
    private ProgressBar progressBar;
    private LoginActivtyPresenter loginActivtyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified())
            startActivity(new Intent(LoginActivity.this, BaseMenuActivity.class));
    }

    private void init() {
        textViewResetPassword = findViewById(R.id.textViewResetPassword);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        progressBar = findViewById(R.id.progressBar);

        //methods
        TextInputLayoutUtils.setDynamicClearText(textLayoutEmail);
        TextInputLayoutUtils.setDynamicPasswordToggle(textLayoutPassword);

        btnGoToRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        textViewResetPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        });


        loginActivtyPresenter = new LoginActivtyPresenter(this);

        btnLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Set the FirebaseAuthListener in the presenter
            loginActivtyPresenter.setFirebaseAuthListener(isVerified -> {
                Log.d("LoginActivity", "onEmailVerified: " + isVerified);
                if (isVerified) {
                    loginActivtyPresenter.updateUserActive(email, true);
                }
            });

            // Perform the login process
            loginActivtyPresenter.login(email, password);
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginActivtyPresenter.setFirebaseAuthListener(null);
    }

    @Override
    public void showProgressbar(boolean flag) {
        if (flag)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoginSuccess() {
        startActivity(new Intent(LoginActivity.this, BaseMenuActivity.class));
        finish();
    }

    @Override
    public void onLoginFailure(String errorMessage) {
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}