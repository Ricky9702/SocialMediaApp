package com.example.h2ak.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.h2ak.R;

import java.util.ArrayList;
import java.util.List;

public class AdminBaseAdapter extends RecyclerView.Adapter<AdminBaseAdapter.ViewHolder> {

   Context context;

    public AdminBaseAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_admin_add_edit_delete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.linearLayoutParent.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.linearLayoutParent);
            popupMenu.inflate(R.menu.add_edit_delete_menu);
            popupMenu.setForceShowIcon(true);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.itemView) {
                    holder.btnView.performClick();
                } else if (item.getItemId() == R.id.itemEdit) {
                    holder.btnEdit.performClick();
                } else {
                    holder.btnDelete.performClick();
                }
                return true;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewItemName;
        ImageButton btnView, btnEdit, btnDelete;
        LinearLayout linearLayoutParent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            linearLayoutParent = itemView.findViewById(R.id.linearLayoutParent);

        }
    }
}
