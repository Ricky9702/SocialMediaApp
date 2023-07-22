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
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private TextInputEditText textInputEditText;
    private String date;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Get current Date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day  = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireContext(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        date = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month+1, year);
        textInputEditText.setText(date);
    }

    public void setTextInputEditText(TextInputEditText textInputEditText) {
        this.textInputEditText = textInputEditText;
    }

}