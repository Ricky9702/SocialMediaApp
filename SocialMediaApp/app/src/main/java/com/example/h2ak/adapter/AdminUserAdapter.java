package com.example.h2ak.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseUserDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseUserDataSource;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxPostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxPostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentReactionDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostImagesDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostReactionDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.SearchHistoryDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SearchHistoryDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.PasswordHashing;
import com.example.h2ak.view.activities.AdminActivity;
import com.example.h2ak.view.activities.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends AdminBaseAdapter {
    private List<User> userList;
    private UserDataSource userDataSource;
    private FirebaseUserDataSource firebaseUserDataSource;

    public AdminUserAdapter(Context context) {
        super(context);
        userList = new ArrayList<>();
        userDataSource = UserDataSourceImpl.getInstance(context);
        firebaseUserDataSource = FirebaseUserDataSourceImpl.getInstance();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.imageViewIcon.setImageResource(R.drawable.baseline_avatar_place_holder);
        holder.textViewItemName.setText(user.getEmail());

        super.onBindViewHolder(holder, position);

        LinearLayout linearLayout = new LinearLayout(context);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View viewToolbar = LayoutInflater.from(context).inflate(R.layout.app_bar_layout, null, false);

        Toolbar toolbar = viewToolbar.findViewById(R.id.toolBar);

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));


        TextInputLayout userId = this.createTextInputLayout(context, "id", user.getId());
        TextInputLayout name = this.createTextInputLayout(context, "name", user.getName());
        TextInputLayout gender = this.createTextInputLayout(context, "gender", user.getGender());
        TextInputLayout birthday = this.createTextInputLayout(context, "birthday", user.getBirthday());
        TextInputLayout email = this.createTextInputLayout(context, "email", user.getEmail());
        TextInputLayout imageAvatar = this.createTextInputLayout(context, "imageAvatar", user.getImageAvatar());
        TextInputLayout imageCover = this.createTextInputLayout(context, "imageCover", user.getImageCover());
        TextInputLayout bio = this.createTextInputLayout(context, "bio", user.getBio());
        TextInputLayout createdDate = this.createTextInputLayout(context, "createdDate", user.getCreatedDate());
        TextInputLayout password = this.createTextInputLayout(context, "password", user.getPassword());
        TextInputLayout userRole = this.createTextInputLayout(context, "role", user.getRole());
        TextInputLayout isActive = this.createTextInputLayout(context, "isActive", user.isActive() + "");
        TextInputLayout isOnline = this.createTextInputLayout(context, "isOnline", user.isOnline() + "");

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(viewToolbar);
        linearLayout.addView(userId);
        linearLayout.addView(name);
        linearLayout.addView(bio);
        linearLayout.addView(gender);
        linearLayout.addView(birthday);
        linearLayout.addView(email);
        linearLayout.addView(password);
        linearLayout.addView(userRole);
        linearLayout.addView(isActive);
        linearLayout.addView(isOnline);
        linearLayout.addView(imageAvatar);
        linearLayout.addView(imageCover);
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
            name.getEditText().setText(user.getName());
            bio.getEditText().setText(user.getBio());
            gender.getEditText().setText(user.getGender());
            birthday.getEditText().setText(user.getBirthday());
            password.getEditText().setText(user.getPassword());
            email.getEditText().setText(user.getEmail());
            imageAvatar.getEditText().setText(user.getImageAvatar());
            imageCover.getEditText().setText(user.getImageCover());
            createdDate.getEditText().setText(user.getCreatedDate());
            isActive.getEditText().setText(user.isActive()+"");
            isOnline.getEditText().setText(user.isOnline()+"");
            userRole.getEditText().setText(user.getRole());
        });

        holder.btnView.setOnClickListener(view -> {
            userId.getEditText().setFocusable(false);
            name.getEditText().setFocusable(false);
            gender.getEditText().setFocusable(false);
            birthday.getEditText().setFocusable(false);
            email.getEditText().setFocusable(false);
            imageAvatar.getEditText().setFocusable(false);
            imageCover.getEditText().setFocusable(false);
            bio.getEditText().setFocusable(false);
            createdDate.getEditText().setFocusable(false);
            password.getEditText().setFocusable(false);
            userRole.getEditText().setFocusable(false);
            isActive.getEditText().setFocusable(false);
            isOnline.getEditText().setFocusable(false);

            toolbar.setTitle("View user");
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
            if (toolbar.getMenu() != null) {
                if (toolbar.getMenu().findItem(R.id.itemCheck) != null) {
                    toolbar.getMenu().findItem(R.id.itemCheck).setVisible(false);
                }
            }
            dialog.show();
        });

        holder.btnEdit.setOnClickListener(view -> {
            userId.getEditText().setFocusableInTouchMode(true);
            name.getEditText().setFocusableInTouchMode(true);
            gender.getEditText().setFocusableInTouchMode(true);
            birthday.getEditText().setFocusableInTouchMode(true);
            email.getEditText().setFocusable(false);
            imageAvatar.getEditText().setFocusableInTouchMode(true);
            imageCover.getEditText().setFocusableInTouchMode(true);
            bio.getEditText().setFocusableInTouchMode(true);
            createdDate.getEditText().setFocusableInTouchMode(true);
            password.getEditText().setFocusable(false);
            isActive.getEditText().setFocusableInTouchMode(true);
            isOnline.getEditText().setFocusableInTouchMode(true);

            CharSequence[] statusItems = new String[]{"ADMIN", "USER"};
            userRole.getEditText().setFocusable(false);
            userRole.getEditText().setOnClickListener(v -> {
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setTitle("Choose status")
                        .setItems(statusItems, (dialogInterface, i) -> {
                            userRole.getEditText().setText(statusItems[i]);
                        })
                        .create().show();
            });

            toolbar.setTitle("Edit user");
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

            if (toolbar.getMenu() != null) {
                if (toolbar.getMenu().findItem(R.id.itemCheck) == null) {
                    toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);
                }
                toolbar.getMenu().findItem(R.id.itemCheck).setVisible(true);
            }

            toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));

            dialog.show();


            toolbar.setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                builder1.setTitle("Update user")
                        .setMessage("Are you sure want to apply changes to this user?")
                        .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            user.setName(name.getEditText().getText().toString());
                            user.setGender(gender.getEditText().getText().toString());
                            user.setBirthday(birthday.getEditText().getText().toString());
                            user.setCreatedDate(createdDate.getEditText().getText().toString());
                            user.setBio(bio.getEditText().getText().toString());
                            user.setEmail(email.getEditText().getText().toString());
                            user.setPassword(password.getEditText().getText().toString());
                            user.setImageAvatar(imageAvatar.getEditText().getText().toString());
                            user.setImageCover(imageCover.getEditText().getText().toString());
                            user.setOnline(isOnline.getEditText().getText().toString().equals("true"));
                            user.setActive(isActive.getEditText().getText().toString().equals("true"));
                            user.setRole(userRole.getEditText().getText().toString().trim());
                            if (userDataSource.updateUserChangeOnFirebase(user)) {
                                dialog.dismiss();
                                Toast.makeText(view.getContext(), "Update successfully!", Toast.LENGTH_SHORT).show();
                                firebaseUserDataSource.updateUser(user);
                                notifyItemChanged(position);
                            } else {
                                Toast.makeText(view.getContext(), "Update failed!", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();
                return true;
            });

        });

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
            builder1.setTitle("Delete user")
                    .setMessage("Are you sure you want to delete this user?\n All posts, friend list, will be deleted which lead to delete everything")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {

                        // delete all search history
                        SearchHistoryDataSource searchHistoryDataSource = SearchHistoryDataSourceImpl.getInstance(context);
                        searchHistoryDataSource.getAllSearchHistoryByUser(user).forEach(searchHistory -> {
                            if (searchHistory != null) searchHistoryDataSource.deleteSearchHistory(searchHistory, false);
                        });

                        // delete all friend ship belong to this user
                        FriendShipDataSource friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
                        friendShipDataSource.getAllFriendShipByUser(user).forEach(friendShip -> {
                            if (friendShip != null) friendShipDataSource.delete(friendShip);
                        });


                        PostDataSource postDataSource = PostDataSourceImpl.getInstance(context);
                        PostImagesDataSource postImagesDataSource = PostImagesDataSourceImpl.getInstance(context);
                        PostReactionDataSource postReactionDataSource = PostReactionDataSourceImpl.getInstance(context);
                        PostCommentDataSource postCommentDataSource = PostCommentDataSourceImpl.getInstance(context);
                        PostCommentReactionDataSource postCommentReactionDataSource = PostCommentReactionDataSourceImpl.getInstance(context);

                        // delete all comment by user
                        postCommentDataSource.getAllCommentByUser(user).forEach(comment -> {
                            // delete all comment, reaction inside comment
                            if (comment != null) {
                                postCommentDataSource.getAllCommentByParent(comment).forEach(child -> {
                                    if (child != null) postCommentDataSource.delete(child);
                                });

                                postCommentReactionDataSource.getAllReactionByComment(comment).forEach(postCommentReaction -> {
                                    if (postCommentReaction != null) postCommentReactionDataSource.delete(postCommentReaction);
                                });

                                postCommentDataSource.delete(comment);
                            }
                        });

                        // delete all post
                        postDataSource.getAllPostByUserId(user.getId()).forEach(post -> {
                            if (post != null) {
                                // delete all comment, comment reaction, post reaction inside post
                                postCommentDataSource.getAllCommentByPost(post).forEach(comment -> {
                                    if (comment != null) {
                                        postCommentReactionDataSource.getAllReactionByComment(comment).forEach(postCommentReaction -> {
                                            if (postCommentReaction != null) postCommentReactionDataSource.delete(postCommentReaction);
                                        });
                                        postCommentDataSource.delete(comment);
                                    }
                                });

                                // delete all reaction to post
                                postReactionDataSource.getAllReactionByPost(post).forEach(postReaction -> {
                                    if (postReaction != null) postReactionDataSource.delete(postReaction);
                                });

                                // delete all images to post
                                postImagesDataSource.getAllPostImagesByPost(post).forEach(postImages -> {
                                    if (postImages != null) postImagesDataSource.deletePostImages(postImages);
                                });

                                postDataSource.deletePost(post);
                            }
                        });

                        // delete all reaction by user
                        postReactionDataSource.getAllReactionByUser(user).forEach(postReaction -> {
                            if (postReaction != null) postReactionDataSource.delete(postReaction);
                        });


                        // delete all comment reaction by user
                        postCommentReactionDataSource.getAllReactionByUser(user).forEach(postCommentReaction -> {
                            if (postCommentReaction != null) postCommentReactionDataSource.delete(postCommentReaction);
                        });

                        // delete all inbox
                        InboxPostDataSource inboxPostDataSource = InboxPostDataSourceImpl.getInstance(context);
                        InboxDataSource inboxDataSource = InboxDataSourceImpl.getInstance(context);
                        inboxDataSource.getInboxByUser(user).forEach(inbox -> {
                            if (inbox != null) {
                                inboxPostDataSource.delete(inbox.getId());
                                inboxDataSource.deleteInbox(inbox);
                            }
                        });


                        if (userDataSource.deleteUser(user)) {
                            Toast.makeText(view.getContext(), "Delete successfully!", Toast.LENGTH_SHORT).show();
                            this.userList.remove(user);
                            this.notifyItemRemoved(holder.getAdapterPosition());
                            if (!(MyApp.getInstance().getCurrentActivity() instanceof AdminActivity)) {
                                FirebaseAuth.getInstance().signOut();
                                MyApp.getInstance().getCurrentActivity().startActivity(new Intent(MyApp.getInstance().getCurrentActivity(), LoginActivity.class));
                                MyApp.getInstance().getCurrentActivity().finish();
                            }
                        } else {
                            Toast.makeText(view.getContext(), "Delete failed!", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        });


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


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        this.notifyDataSetChanged();
    }

    public void createUser() {
        User user = new User();

        LinearLayout linearLayout = new LinearLayout(context);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View viewToolbar = LayoutInflater.from(context).inflate(R.layout.app_bar_layout, null, false);

        Toolbar toolbar = viewToolbar.findViewById(R.id.toolBar);

        toolbar.setTitle("Create user");
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));


        TextInputLayout userId = this.createTextInputLayout(context, "id", user.getId());
        TextInputLayout name = this.createTextInputLayout(context, "name", user.getName());
        TextInputLayout gender = this.createTextInputLayout(context, "gender", user.getGender());
        TextInputLayout birthday = this.createTextInputLayout(context, "birthday", user.getBirthday());
        TextInputLayout email = this.createTextInputLayout(context, "email", user.getEmail());
        TextInputLayout imageAvatar = this.createTextInputLayout(context, "imageAvatar", user.getImageAvatar());
        TextInputLayout imageCover = this.createTextInputLayout(context, "imageCover", user.getImageCover());
        TextInputLayout bio = this.createTextInputLayout(context, "bio", user.getBio());
        TextInputLayout createdDate = this.createTextInputLayout(context, "createdDate", user.getCreatedDate());
        TextInputLayout password = this.createTextInputLayout(context, "password", user.getPassword());
        TextInputLayout userRole = this.createTextInputLayout(context, "role", user.getRole());
        TextInputLayout isActive = this.createTextInputLayout(context, "isActive", user.isActive() + "");
        TextInputLayout isOnline = this.createTextInputLayout(context, "isOnline", user.isOnline() + "");

        userId.setEnabled(false);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(viewToolbar);
        linearLayout.addView(userId);
        linearLayout.addView(name);
        linearLayout.addView(bio);
        linearLayout.addView(gender);
        linearLayout.addView(birthday);
        linearLayout.addView(email);
        linearLayout.addView(password);
        linearLayout.addView(userRole);
        linearLayout.addView(isActive);
        linearLayout.addView(isOnline);
        linearLayout.addView(imageAvatar);
        linearLayout.addView(imageCover);
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

        toolbar.setOnMenuItemClickListener(item -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Create user")
                    .setMessage("Are you sure want to create this user?")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        user.setName(name.getEditText().getText().toString());
                        user.setGender(gender.getEditText().getText().toString());
                        user.setBirthday(birthday.getEditText().getText().toString());
                        user.setCreatedDate(createdDate.getEditText().getText().toString());
                        user.setBio(bio.getEditText().getText().toString());
                        user.setEmail(email.getEditText().getText().toString());
                        user.setPassword(password.getEditText().getText().toString());
                        user.setImageAvatar(imageAvatar.getEditText().getText().toString());
                        user.setImageCover(imageCover.getEditText().getText().toString());
                        user.setOnline(isOnline.getEditText().getText().toString() == "true");
                        user.setActive(isActive.getEditText().getText().toString() == "true");
                        user.setRole(userRole.getEditText().getText().toString().trim());

                        if (user.getName() == null || user.getName().isEmpty()) {
                            Toast.makeText(context, "Name is null or empty!!", Toast.LENGTH_SHORT).show();
                        } else if (user.getEmail() == null || user.getEmail().isEmpty()) {
                            Toast.makeText(context, "Email is null or empty!!", Toast.LENGTH_SHORT).show();
                        } else if (user.getPassword() == null || user.getPassword().isEmpty()) {
                            Toast.makeText(context, "Password is null or empty!!", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.setId(FirebaseAuth.getInstance().getUid());
                                    user.setPassword(PasswordHashing.hashPassword(user.getPassword()));
                                    if (userDataSource.createUser(user)) {
                                        Toast.makeText(context, "Create successfully!", Toast.LENGTH_SHORT).show();
                                        this.userList.add(0, user);
                                        notifyItemInserted(0);
                                        firebaseUserDataSource.createUser(user);

                                        //Send email
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(task1 -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MyApp.getInstance(), "Verification email sent.", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(runnable -> {
                                            Toast.makeText(MyApp.getInstance(), "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                        });

                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(context, "Create failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }).create().show();
            return true;
        });
    }
}
