package com.example.h2ak.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.h2ak.R;
import com.example.h2ak.adapter.SpinnerGenderAdapter;
import com.example.h2ak.contract.RegisterActivityContract;
import com.example.h2ak.model.User;
import com.example.h2ak.presenter.RegisterActivityPresenter;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.example.h2ak.view.fragments.DatePickerFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity implements RegisterActivityContract.View {
    TextInputLayout textLayoutPassword, textLayoutConfirmPassword, textLayoutEmail, textLayoutName;
    TextInputEditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextGender, editTextBirthday;
    Button btnRegister;
    Toolbar toolbar;
    TextView textViewGoToLogin;
    ProgressBar progressBar;
    Spinner spinnerGender;
    private RegisterActivityPresenter registerActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        // Get views
        textLayoutName = findViewById(R.id.textLayoutName);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);
        textLayoutConfirmPassword = findViewById(R.id.textLayoutConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextGender = findViewById(R.id.editTextGender);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        btnRegister = findViewById(R.id.btnRegister);
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);
        progressBar = findViewById(R.id.progressBar);
        spinnerGender = findViewById(R.id.spinnerGender);

        // Edit appBar
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("Create account");
        toolbar.setTitleTextColor(getColor(R.color.black));
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        //set event click on edit text birthday
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTextInputEditText(editTextBirthday);
        editTextBirthday.setOnClickListener(view -> {
            datePickerFragment.show(getSupportFragmentManager(), "datePicker");
        });


        //set event click on edit text gender
        editTextGender.setOnClickListener(view -> {
            spinnerGender.performClick();
        });

        //Fill data for spinner
        spinnerGender = findViewById(R.id.spinnerGender);

        SpinnerGenderAdapter adapter = new SpinnerGenderAdapter(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                new String[]{"Male", "Female", "Other", ""});
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.setCount(adapter.getCount() - 1);

        spinnerGender.setAdapter(adapter);
        spinnerGender.setSelection(adapter.getCount());

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String gender = (String) adapterView.getItemAtPosition(i);
                editTextGender.setText(gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                editTextGender.setText("");
            }
        });


//         Get methods
        TextInputLayoutUtils.setDynamicClearText(textLayoutName);
        TextInputLayoutUtils.setDynamicClearText(textLayoutEmail);
        TextInputLayoutUtils.setDynamicPasswordToggle(textLayoutPassword);
        TextInputLayoutUtils.setDynamicPasswordToggle(textLayoutConfirmPassword);

        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        textViewGoToLogin.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        registerActivityPresenter = new RegisterActivityPresenter(this);

        btnRegister.setOnClickListener(view -> {
            String name = editTextName.getText().toString().trim();
            String birthday = editTextBirthday.getText().toString().trim();
            String gender = editTextGender.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            User createdUser = registerActivityPresenter.createUser(name, birthday, gender, email, password, confirmPassword);
            if (createdUser != null)
                registerActivityPresenter.register(createdUser);
        });

    }

    @Override
    public void showProgressBar(boolean flag) {
        if (flag)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisterSuccess() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
