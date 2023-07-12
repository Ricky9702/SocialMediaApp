package com.example.h2ak.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ValidationUtils {

    private  boolean emailValid;
    private  boolean passwordValid;

    private  boolean emailFocused;

    private Button handlerButton;
    private boolean isEnabled;

    private static FirebaseAuth firebaseAuth;

    public static FirebaseUser userSignedIn() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user;
    }

    public ValidationUtils() {
        emailValid = false;
        passwordValid = false;
        emailFocused = false;
    }

    public  void validateEmail(EditText editTextEmail) {
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editTextEmail.getText().toString().trim();
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Invalid email format");
                    setEmailValid(false);
                } else {
                    editTextEmail.setError(null);
                    setEmailValid(true);
                }
                enableHandlerButton(handlerButton);
            }
        });
    }

    public  void validatePassword(EditText editTextPassword) {
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = editTextPassword.getText().toString().trim();
                if (password.isEmpty() || password.length() < 6) {
                    editTextPassword.setError("Password length should be at least 6 characters");
                    setPasswordValid(false);
                } else {
                    editTextPassword.setError(null);
                    setPasswordValid(true);
                }
                enableHandlerButton(handlerButton);
            }
        });
    }

    public  void dynamicPasswordToggle(TextInputLayout textLayoutPassword) {
        textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        textLayoutPassword.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
                textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            else
                textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        });
    }

    public  void dynamicClearText(TextInputLayout textLayoutEmail) {
        textLayoutEmail.setEndIconMode(TextInputLayout.END_ICON_NONE);

        //Check if focus and update icon
        textLayoutEmail.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                emailFocused = true;
                updateClearTextIcon(textLayoutEmail);
            } else {
                emailFocused = false;
                textLayoutEmail.setEndIconMode(TextInputLayout.END_ICON_NONE);
            }
        });

        //Check if text length > 0 and update icon
        textLayoutEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateClearTextIcon(textLayoutEmail);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void updateClearTextIcon(TextInputLayout textLayoutEmail) {
        if (emailFocused && textLayoutEmail.getEditText().getText().length() > 0) {
            textLayoutEmail.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        } else {
            textLayoutEmail.setEndIconMode(TextInputLayout.END_ICON_NONE);
        }
    }

    public void enableHandlerButton(Button b) {
        isEnabled = isEmailValid() && isPasswordValid();
        b.setEnabled(isEnabled);
    }

    public boolean isEmailValid() {
        return emailValid;
    }

    public boolean isPasswordValid() {
        return passwordValid;
    }

    public void setHandlerButton(Button b) {
        handlerButton = b;
    }

    public void setEmailValid(boolean emailValid) {
        this.emailValid = emailValid;
    }

    public void setPasswordValid(boolean passwordValid) {
        this.passwordValid = passwordValid;
    }
}
