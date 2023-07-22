package com.example.h2ak.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class TextInputLayoutUtils {

    private static boolean emailFocused;

    public TextInputLayoutUtils() {
        emailFocused = false;
    }


    public static void setDynamicPasswordToggle(TextInputLayout textLayoutPassword) {
        textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        textLayoutPassword.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
                textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            else
                textLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        });
    }

    public static void setDynamicClearText(TextInputLayout textLayoutEmail) {
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

}
