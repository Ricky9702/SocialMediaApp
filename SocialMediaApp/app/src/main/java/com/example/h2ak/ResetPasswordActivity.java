package com.example.h2ak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.h2ak.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextEmailResetPassword;
    private TextInputLayout textLayoutEmail;
    private Button buttonResetPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolBarRecoveryPassword);
        editTextEmailResetPassword = findViewById(R.id.editTextEmailResetPassword);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        buttonResetPassword = findViewById(R.id.btnResetPassword);
        buttonResetPassword.setEnabled(false);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        ValidationUtils validationUtils = new ValidationUtils();
        validationUtils.setPasswordValid(true);
        validationUtils.dynamicClearText(textLayoutEmail);
        validationUtils.validateEmail(editTextEmailResetPassword);
        validationUtils.setHandlerButton(buttonResetPassword);

        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        buttonResetPassword.setOnClickListener(view -> {
            String email = editTextEmailResetPassword.getText().toString().trim();
            if (!email.isEmpty() && validationUtils.isEmailValid())
                resetPassword(email);
        });
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, task -> {
           if (task.isSuccessful()) {
               Toast.makeText(ResetPasswordActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
           }
           else
               Toast.makeText(ResetPasswordActivity.this, "Failed to sent email...", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(runnable -> {
           Toast.makeText(ResetPasswordActivity.this, runnable.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }
}