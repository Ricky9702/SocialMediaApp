package com.example.h2ak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout textLayoutPassword, textLayoutEmail;
    private EditText editTextEmail, editTextPassword;
    private Button btnRegister;
    private Toolbar toolbar;

    private TextView textViewGoToLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init()
    {
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);
        toolbar = findViewById(R.id.toolBarRegister);
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        ValidationUtils.sethanlderButton(btnRegister);

        // methods
        ValidationUtils.dynamicClearText(textLayoutEmail);
        ValidationUtils.dynamicPasswordToggle(textLayoutPassword);
        ValidationUtils.validateEmail(editTextEmail);
        ValidationUtils.validatePassword(editTextPassword);
        btnRegisterClick();

        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

        textViewGoToLogin.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void btnRegisterClick() {
            btnRegister.setOnClickListener(view -> {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if (ValidationUtils.isEmailValid() && ValidationUtils.isPasswordValid())
                    register(email, password);
            });
    }
    private void register(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        // Registration failed
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }).addOnFailureListener(runnable -> {
                    Toast.makeText(RegisterActivity.this, runnable.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
        });
    }

}