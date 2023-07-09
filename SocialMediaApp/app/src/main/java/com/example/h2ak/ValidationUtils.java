package com.example.h2ak;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class ValidateUtils {

    private static boolean emailValid = false;
    private static boolean passwordValid = false;

    private static boolean emailFocused = false;


    public static void validateEmail(EditText editTextEmail, Button b) {
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
                if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Invalid email format");
                    emailValid = false;
                } else {
                    editTextEmail.setError(null);
                    emailValid = true;
                }
                enableRegisterButton(b);
            }
        });
    }

    public static void validatePassword(EditText editTextPassword, Button b) {
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
                if (password.length() < 9) {
                    editTextPassword.setError("Password length should be at least 9 characters");
                    passwordValid = false;
                } else {
                    editTextPassword.setError(null);
                    passwordValid = true;
                }
                enableRegisterButton(b);
            }
        });
    }

    public static void dynamicPasswordToggle(TextInputLayout textLayoutPassword) {
        textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        textLayoutPassword.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
                textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            else
                textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        });
    }

    public static void dynamicClearText(TextInputLayout textLayoutEmail) {
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

    public static void updateClearTextIcon(TextInputLayout textLayoutEmail) {
        if (emailFocused && textLayoutEmail.getEditText().getText().length() > 0) {
            textLayoutEmail.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        } else {
            textLayoutEmail.setEndIconMode(TextInputLayout.END_ICON_NONE);
        }
    }

    public static void enableRegisterButton(Button b) {
        if (emailValid && passwordValid) {
            b.setEnabled(true);
        } else {
            b.setEnabled(false);
        }
    }

    public static boolean isEmailValid() {
        return emailValid;
    }

    public static boolean isPasswordValid() {
        return passwordValid;
    }

}
