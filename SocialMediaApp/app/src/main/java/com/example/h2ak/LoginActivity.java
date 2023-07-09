package com.example.h2ak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textLayoutPassword, textLayoutEmail;
    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnGoToRegister;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        //methods
        ValidationUtils.dynamicClearText(textLayoutEmail);
        ValidationUtils.dynamicPasswordToggle(textLayoutPassword);
        ValidationUtils.validateEmail(editTextEmail);
        ValidationUtils.validatePassword(editTextPassword);
        ValidationUtils.sethanlderButton(btnLogin);
        btnLoginClick();


        btnGoToRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void btnLoginClick() {
        btnLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (email.isEmpty() && password.isEmpty())
                editTextEmail.setFocusable(true);
            else if (ValidationUtils.isEmailValid() && ValidationUtils.isPasswordValid())
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