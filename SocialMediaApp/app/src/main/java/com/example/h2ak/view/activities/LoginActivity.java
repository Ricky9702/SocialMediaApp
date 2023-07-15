package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.h2ak.R;
import com.example.h2ak.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView textViewResetPassword;
    private TextInputLayout textLayoutPassword, textLayoutEmail;
    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnGoToRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        FirebaseUser user = mAuth.getCurrentUser();

//        if (user != null)
//            startActivity(new Intent(LoginActivity.this, BaseMenuActivity.class));

    }

    private void init() {
        textViewResetPassword = findViewById(R.id.textViewResetPassword);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setEnabled(false);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        //methods
        ValidationUtils validationUtils = new ValidationUtils();
        validationUtils.dynamicClearText(textLayoutEmail);
        validationUtils.dynamicPasswordToggle(textLayoutPassword);
        validationUtils.validateEmail(editTextEmail);
        validationUtils.validatePassword(editTextPassword);
        validationUtils.setHandlerButton(btnLogin);
        btnLoginClick(validationUtils);


        btnGoToRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        textViewResetPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        });

    }

    private void btnLoginClick(ValidationUtils validationUtils) {
        btnLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (validationUtils.isEmailValid() && validationUtils.isPasswordValid())
                loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(LoginActivity.this, BaseMenuActivity.class));
                        finish();
                    } else {
                        //Sign in fails
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }).addOnFailureListener(runnable -> {
                    Toast.makeText(LoginActivity.this, runnable.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

}