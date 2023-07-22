package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.h2ak.R;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
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
        //Get views
        editTextEmailResetPassword = findViewById(R.id.editTextEmailResetPassword);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        buttonResetPassword = findViewById(R.id.btnResetPassword);
        buttonResetPassword.setEnabled(false);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        //Edit toolBar
        //Edit toolBar
        Toolbar toolBar = findViewById(R.id.toolBar);
        toolBar.setTitle("Recovery password");
        toolBar.setTitleTextColor(getColor(R.color.black));
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        //Get methods
        TextInputLayoutUtils.setDynamicClearText(textLayoutEmail);

        toolBar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
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