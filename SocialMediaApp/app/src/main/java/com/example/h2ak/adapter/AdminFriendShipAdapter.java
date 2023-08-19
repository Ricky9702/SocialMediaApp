package com.example.h2ak.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.PasswordHashing;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdminFriendShipAdapter extends AdminBaseAdapter {
    private List<User> userList;
    private List<FriendShip> friendShipList;
    private FriendShipDataSource friendShipDataSource;
    private UserDataSource userDataSource;

    public AdminFriendShipAdapter(Context context) {
        super(context);
        friendShipList = new ArrayList<>();
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
        userDataSource = UserDataSourceImpl.getInstance(context);
        userList = userDataSource.getAllUsers(null);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendShip friendShip = friendShipList.get(position);

        holder.imageViewIcon.setImageResource(R.drawable.baseline_friendship);
        holder.textViewItemName.setText(friendShip.getUser1().getEmail()+"\n"+friendShip.getUser2().getEmail());

        super.onBindViewHolder(holder, position);

        LinearLayout linearLayout = new LinearLayout(context);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View viewToolbar = LayoutInflater.from(context).inflate(R.layout.app_bar_layout, null, false);

        Toolbar toolbar = viewToolbar.findViewById(R.id.toolBar);

        toolbar.setTitle("View friendship");
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));

        TextInputLayout friendshipId = createTextInputLayout(context, "id", friendShip.getId());
        TextInputLayout user1Id = createTextInputLayout(context, "user1", friendShip.getUser1().getId());
        TextInputLayout user2Id = createTextInputLayout(context, "user2", friendShip.getUser2().getId());
        TextInputLayout status = createTextInputLayout(context, "status", friendShip.getStatus());
        TextInputLayout createdDate = createTextInputLayout(context, "createdDate", friendShip.getCreatedDate());

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(viewToolbar);
        linearLayout.addView(friendshipId);
        linearLayout.addView(user1Id);
        linearLayout.addView(user2Id);
        linearLayout.addView(status);
        linearLayout.addView(createdDate);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(scrollView);

        AlertDialog dialog = builder.create();

        Rect displayRectangle = new Rect();

        Window window = dialog.getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialog.getWindow().setLayout((int) (displayRectangle.width()), (int) (displayRectangle.height()));

        toolbar.setNavigationOnClickListener(view1 -> {
            dialog.dismiss();
        });

        dialog.setOnDismissListener(dialogInterface -> {
            friendshipId.getEditText().setText(friendShip.getId());
            user1Id.getEditText().setText(friendShip.getUser1().getId());
            user2Id.getEditText().setText(friendShip.getUser2().getId());
            status.getEditText().setText(friendShip.getStatus());
            createdDate.getEditText().setText(friendShip.getCreatedDate());
        });

        holder.btnView.setOnClickListener(view -> {
            friendshipId.getEditText().setFocusable(false);
            user1Id.getEditText().setFocusable(false);
            user2Id.getEditText().setFocusable(false);
            status.getEditText().setFocusable(false);
            createdDate.getEditText().setFocusable(false);


            toolbar.setTitle("View friendship");
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
            if (toolbar.getMenu() != null) {
                if (toolbar.getMenu().findItem(R.id.itemCheck) != null) {
                    toolbar.getMenu().findItem(R.id.itemCheck).setVisible(false);
                }
            }
            dialog.show();
        });

        holder.btnEdit.setOnClickListener(view -> {
            friendshipId.getEditText().setFocusable(false);
            user1Id.getEditText().setFocusable(true);
            user2Id.getEditText().setFocusable(true);
            status.getEditText().setFocusable(true);
            createdDate.getEditText().setFocusable(true);

            toolbar.setTitle("Edit friendship");
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

            if (toolbar.getMenu() != null) {
                if (toolbar.getMenu().findItem(R.id.itemCheck) == null) {
                    toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);
                }
                toolbar.getMenu().findItem(R.id.itemCheck).setVisible(true);
            }

            toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));

            dialog.show();

            CharSequence[] statusItems = new String[]{"PENDING", "ACCEPTED", "DELETED"};
            status.getEditText().setFocusable(false);
            status.getEditText().setOnClickListener(v -> {
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setTitle("Choose status")
                        .setItems(statusItems, (dialogInterface, i) -> {
                            status.getEditText().setText(statusItems[i]);
                        })
                        .create().show();
            });

            CharSequence[] items = userList.stream()
                    .flatMap(user -> Stream.of(user.getEmail())).collect(Collectors.toList())
                    .toArray(new CharSequence[userList.size()]);

            user1Id.getEditText().setFocusable(false);
            user1Id.getEditText().setOnClickListener(v -> {
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setTitle("Choose user")
                        .setItems(items, (dialogInterface, i) -> {
                            user1Id.getEditText().setText(userList.get(i).getId());
                        })
                        .create().show();
            });

            user2Id.getEditText().setFocusable(false);
            user2Id.getEditText().setOnClickListener(v -> {
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setTitle("Choose user")
                        .setItems((CharSequence[]) items, (dialogInterface, i) -> {
                            user2Id.getEditText().setText(userList.get(i).getId());
                        })
                        .create().show();
            });


            toolbar.setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                builder1.setTitle("Update friendship")
                        .setMessage("Are you sure want to apply changes to this friendship?")
                        .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            String id1 = user1Id.getEditText().getText().toString().trim();
                            String id2 = user2Id.getEditText().getText().toString().trim();
                            if (id1.isEmpty()) {
                                Toast.makeText(view.getContext(), "User id 1 is null", Toast.LENGTH_SHORT).show();
                            } else if (id2.isEmpty()) {
                                Toast.makeText(view.getContext(), "User id 2 is null", Toast.LENGTH_SHORT).show();
                            } else {
                                User user1 = userDataSource.getUserById(id1);
                                User user2 = userDataSource.getUserById(id2);
                                if (user1 == null) {
                                    Toast.makeText(view.getContext(), "User 1 is null", Toast.LENGTH_SHORT).show();
                                } else if (user2 == null) {
                                    Toast.makeText(view.getContext(), "User 2 is null", Toast.LENGTH_SHORT).show();
                                } else if (user1.getId().equals(user2.getId())) {
                                    Toast.makeText(view.getContext(), "This person can not be friend him/she self", Toast.LENGTH_SHORT).show();
                                } else if (FriendShip.FriendShipStatus.valueOf(friendShip.getStatus()).getStatus() == null) {
                                    Toast.makeText(context, "Status are PENDING, ACCEPTED, DELETED", Toast.LENGTH_SHORT).show();
                                } else {
                                    friendShip.setStatus(status.getEditText().getText().toString());
                                    friendShip.setCreatedDate(createdDate.getEditText().getText().toString());
                                    friendShip.setUser1(user1);
                                    friendShip.setUser2(user2);
                                    if (friendShipDataSource.updateFriendShip(friendShip)) {
                                        dialog.dismiss();
                                        Toast.makeText(view.getContext(), "Update successfully!", Toast.LENGTH_SHORT).show();
                                        notifyItemChanged(position);
                                    } else {
                                        Toast.makeText(view.getContext(), "Update failed!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            }
                        }).create().show();
                return true;
            });
        });

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
            builder1.setTitle("Delete friendship")
                    .setMessage("Are you sure you want to delete this friendship?")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        if (friendShipDataSource.delete(friendShip)) {
                            Toast.makeText(view.getContext(), "Delete successfully!", Toast.LENGTH_SHORT).show();
                            this.friendShipList.remove(friendShip);
                            this.notifyItemRemoved(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(view.getContext(), "Delete failed!", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        });
    }

    public List<FriendShip> getFriendShipList() {
        return friendShipList;
    }

    @Override
    public int getItemCount() {
        return this.friendShipList.size();
    }

    public void setFriendShipList(List<FriendShip> friendShipList) {
        Log.d("TAG", "setFriendShipList: "+ friendShipList.size());
        this.friendShipList = friendShipList;
        notifyDataSetChanged();
    }

    public TextInputLayout createTextInputLayout(Context context, String hint, String textPlaceHolder) {
        TextInputLayout textInputLayout = new TextInputLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        textInputLayout.setLayoutParams(layoutParams);
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED);
        TextInputEditText textInputEditText = new TextInputEditText(context);
        textInputEditText.setText(textPlaceHolder);
        textInputEditText.setHint(hint);
        textInputEditText.setSingleLine(true);
        textInputEditText.setEllipsize(TextUtils.TruncateAt.END);
        textInputEditText.setBackgroundResource(android.R.color.transparent);
        textInputLayout.addView(textInputEditText);
        return textInputLayout;
    }

    public void createFriendShip() {
        FriendShip friendShip = new FriendShip();
        LinearLayout linearLayout = new LinearLayout(context);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View viewToolbar = LayoutInflater.from(context).inflate(R.layout.app_bar_layout, null, false);

        Toolbar toolbar = viewToolbar.findViewById(R.id.toolBar);

        toolbar.setTitle("Create friendship");
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));

        TextInputLayout friendshipId = createTextInputLayout(context, "id", friendShip.getId());
        TextInputLayout user1Id = createTextInputLayout(context, "user1","");
        TextInputLayout user2Id = createTextInputLayout(context, "user2", "");
        TextInputLayout status = createTextInputLayout(context, "status", friendShip.getStatus());
        TextInputLayout createdDate = createTextInputLayout(context, "createdDate", friendShip.getCreatedDate());

        CharSequence[] items = userList.stream()
                .flatMap(user -> Stream.of(user.getEmail())).collect(Collectors.toList())
                        .toArray(new CharSequence[userList.size()]);

        user1Id.getEditText().setFocusable(false);
        user1Id.getEditText().setOnClickListener(view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle("Choose user")
                    .setItems(items, (dialogInterface, i) -> {
                        user1Id.getEditText().setText(userList.get(i).getId());
                    })
                    .create().show();
        });

        user2Id.getEditText().setFocusable(false);
        user2Id.getEditText().setOnClickListener(view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle("Choose user")
                    .setItems((CharSequence[]) items, (dialogInterface, i) -> {
                        user2Id.getEditText().setText(userList.get(i).getId());
                    })
                    .create().show();
        });

        CharSequence[] statusItems = new String[]{"PENDING", "ACCEPTED", "DELETED"};
        status.getEditText().setFocusable(false);
        status.getEditText().setOnClickListener(view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle("Choose status")
                    .setItems(statusItems, (dialogInterface, i) -> {
                        status.getEditText().setText(statusItems[i]);
                    })
                    .create().show();
        });

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(viewToolbar);
        linearLayout.addView(friendshipId);
        linearLayout.addView(user1Id);
        linearLayout.addView(user2Id);
        linearLayout.addView(status);
        linearLayout.addView(createdDate);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(scrollView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Rect displayRectangle = new Rect();

        Window window = dialog.getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialog.getWindow().setLayout((int) (displayRectangle.width()), (int) (displayRectangle.height()));

        toolbar.setNavigationOnClickListener(view1 -> {
            dialog.dismiss();
        });

        toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);

        toolbar.setOnMenuItemClickListener(item -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Create user")
                    .setMessage("Are you sure want to create this user?")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        String id1 = user1Id.getEditText().getText().toString().trim();
                        String id2 = user2Id.getEditText().getText().toString().trim();
                        if (id1.isEmpty()) {
                            Toast.makeText(context, "User id 1 is null", Toast.LENGTH_SHORT).show();
                        } else if (id2.isEmpty()) {
                            Toast.makeText(context, "User id 2 is null", Toast.LENGTH_SHORT).show();
                        } else {
                            User user1 = userDataSource.getUserById(id1);
                            User user2 = userDataSource.getUserById(id2);
                            if (user1 == null) {
                                Toast.makeText(context, "User 1 is null", Toast.LENGTH_SHORT).show();
                            } else if (user2 == null) {
                                Toast.makeText(context, "User 2 is null", Toast.LENGTH_SHORT).show();
                            } else if (user1.getId().equals(user2.getId())) {
                                Toast.makeText(context, "This person can not be friend him/she self", Toast.LENGTH_SHORT).show();
                            } else if (FriendShip.FriendShipStatus.valueOf(friendShip.getStatus()).getStatus() == null) {
                                Toast.makeText(context, "Status are PENDING, ACCEPTED, DELETED", Toast.LENGTH_SHORT).show();
                            } else {
                                friendShip.setStatus(status.getEditText().getText().toString());
                                friendShip.setCreatedDate(createdDate.getEditText().getText().toString());
                                friendShip.setUser1(user1);
                                friendShip.setUser2(user2);
                                if (friendShipDataSource.createFriendShip(friendShip)) {
                                    dialog.dismiss();
                                    Toast.makeText(context, "Create successfully!", Toast.LENGTH_SHORT).show();
                                    friendShipList.add(0, friendShip);
                                    notifyItemInserted(0);
                                } else {
                                    Toast.makeText(context, "Create failed!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        }
                    }).create().show();
            return true;
        });
    }
}
