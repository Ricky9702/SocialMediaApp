package com.example.h2ak.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerGenderAdapter extends ArrayAdapter {
    private int count;
    public SpinnerGenderAdapter(@NonNull Context context, @NonNull Object[] objects) {
        super(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, objects);
        count = objects.length;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
