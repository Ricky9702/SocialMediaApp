package com.example.h2ak.view.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.h2ak.R;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private boolean validateAge;
    private String date;
    private onDateRecieveListener listener;
    private TextInputEditText textInputEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get current Date
        int year, month, day;
        if (textInputEditText != null && textInputEditText.getText() != null && !textInputEditText.getText().toString().isEmpty()) {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date currentInput = null;
            try {
                currentInput = f.parse(textInputEditText.getText().toString().trim());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentInput);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        return new DatePickerDialog(requireContext(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        date = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
        if (validateAge) {
            if (TextInputLayoutUtils.isDateValid(date)) {
                listener.onDateValidation(date);
            } else  {
                listener.onDateNotValidation("The age should be between 10 and 70 !!");
            }
        }
    }

    public  interface onDateRecieveListener {
        void onDateValidation(String date);
        void onDateNotValidation(String errorMsg);
    }

    public void setValidateAge(boolean isvalidate) {
        this.validateAge = isvalidate;
    }

    public void setListener(onDateRecieveListener listener) {
        this.listener = listener;
    }

    public void setTextInputEditText(TextInputEditText textInputEditText) {
        this.textInputEditText = textInputEditText;
    }


}