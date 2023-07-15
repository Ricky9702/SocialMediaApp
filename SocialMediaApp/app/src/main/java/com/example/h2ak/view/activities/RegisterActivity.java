package com.example.h2ak.view.activities;

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

import com.example.h2ak.R;
import com.example.h2ak.database.FirebaseDatabaseHelper;
import com.example.h2ak.pojo.User;
import com.example.h2ak.utils.ValidationUtils;
import com.google.android.material.appbar.AppBarLayout;
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
        // Get views
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        // Edit appBar
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("Create account");
        toolbar.setTitleTextColor(getColor(R.color.black));
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);


        // Get methods
        ValidationUtils validationUtils = new ValidationUtils();
        validationUtils.dynamicClearText(textLayoutEmail);
        validationUtils.dynamicPasswordToggle(textLayoutPassword);
        validationUtils.validateEmail(editTextEmail);
        validationUtils.validatePassword(editTextPassword);
        validationUtils.setHandlerButton(btnRegister);
        btnRegisterClick(validationUtils);

        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        textViewGoToLogin.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
    }

    private void btnRegisterClick(ValidationUtils validationUtils) {
            btnRegister.setOnClickListener(view -> {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if (validationUtils.isEmailValid() && validationUtils.isPasswordValid())
                    register(email, password);
            });
    }
    private void register(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // Registration success
                        FirebaseUser user = mAuth.getCurrentUser();

                        String userEmail = user.getEmail();
                        String userUid = user.getUid();

                        User createdUser = new User(userUid, userEmail, "", "", "");
                        FirebaseDatabaseHelper.getFirebaseDatabaseUser().child(userUid).setValue(createdUser);


                        // Send email verification
                        sendEmailVerification(user);

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

    private void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(runnable -> {
                Toast.makeText(RegisterActivity.this, runnable.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

}