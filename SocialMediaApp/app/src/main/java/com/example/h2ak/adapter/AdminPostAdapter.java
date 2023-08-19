package com.example.h2ak.adapter;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentReactionDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostImagesDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostReactionDataSourceImpl;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.PostCommentReaction;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.model.PostReaction;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminPostAdapter extends AdminBaseAdapter {
    private List<Post> postList;
    private PostDataSource postDataSource;
    private PostImagesDataSource postImagesDataSource;
    private PostCommentDataSource postCommentDataSource;
    private PostReactionDataSource postReactionDataSource;
    private PostCommentReactionDataSource postCommentReactionDataSource;

    public AdminPostAdapter(Context context) {
        super(context);
        postList = new ArrayList<>();
        postDataSource = PostDataSourceImpl.getInstance(context);
        postImagesDataSource = PostImagesDataSourceImpl.getInstance(context);
        postCommentDataSource = PostCommentDataSourceImpl.getInstance(context);
        postReactionDataSource = PostReactionDataSourceImpl.getInstance(context);
        postCommentReactionDataSource = PostCommentReactionDataSourceImpl.getInstance(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        super.onBindViewHolder(holder, position);

        holder.imageViewIcon.setImageResource(R.drawable.baseline_wysiwyg_24);
        holder.textViewItemName.setText(post.getUser().getEmail());

        LinearLayout linearLayout = new LinearLayout(context);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View viewToolbar = LayoutInflater.from(context).inflate(R.layout.app_bar_layout, null, false);

        Toolbar toolbar = viewToolbar.findViewById(R.id.toolBar);

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));

        TextInputLayout postId = createTextInputLayout(context, "id", post.getId());
        TextInputLayout content = createTextInputLayout(context, "content", post.getContent());
        TextInputLayout userId = createTextInputLayout(context, "userId", post.getUser().getId());
        TextInputLayout createdDate = createTextInputLayout(context, "createdDate", post.getCreatedDate());
        TextInputLayout privacy = createTextInputLayout(context, "privacy", post.getPrivacy());

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(viewToolbar);
        linearLayout.addView(postId);
        linearLayout.addView(content);
        linearLayout.addView(userId);
        linearLayout.addView(privacy);
        linearLayout.addView(createdDate);

        Map<TextInputLayout, PostImages> imagesMap = new LinkedHashMap<>();
        postImagesDataSource.getAllPostImagesByPost(post).forEach(image -> {
            if (image != null) {
                TextInputLayout images = createTextInputLayout(context, "images", image.getImageUrl());
                imagesMap.put(images, image);
                linearLayout.addView(images);
            }
        });

        Map<TextInputLayout, PostComment> commentMap = new LinkedHashMap<>();
        postCommentDataSource.getAllCommentByPost(post).forEach(comment -> {
            if (comment != null) {
                TextInputLayout postComment = createTextInputLayout(context, "comment by " + comment.getUser().getName() + " at " + TextInputLayoutUtils.covertTimeToText(comment.getCreatedDate()), comment.getContent());
                commentMap.put(postComment, comment);
                linearLayout.addView(postComment);
            }
        });


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
            postId.getEditText().setText(post.getId());
            content.getEditText().setText(post.getContent());
            userId.getEditText().setText(post.getUser().getId());
            privacy.getEditText().setText(post.getPrivacy());
            createdDate.getEditText().setText(post.getCreatedDate());

            imagesMap.forEach((textInputLayout, s) -> {
                textInputLayout.getEditText().setText(s.getImageUrl());
            });

            commentMap.forEach((textInputLayout, s) -> {
                textInputLayout.getEditText().setText(s.getContent());
            });

        });

        holder.btnView.setOnClickListener(view -> {
            postId.getEditText().setFocusable(false);
            userId.getEditText().setFocusable(false);
            content.getEditText().setFocusable(false);
            privacy.getEditText().setFocusable(false);
            createdDate.getEditText().setFocusable(false);
            imagesMap.keySet().forEach(textInputLayout -> textInputLayout.getEditText().setFocusable(false));
            commentMap.keySet().forEach(textInputLayout -> textInputLayout.getEditText().setFocusable(false));

            toolbar.setTitle("View post");
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
            if (toolbar.getMenu() != null) {
                if (toolbar.getMenu().findItem(R.id.itemCheck) != null) {
                    toolbar.getMenu().findItem(R.id.itemCheck).setVisible(false);
                }
            }
            dialog.show();
        });

        holder.btnEdit.setOnClickListener(view -> {
            postId.getEditText().setFocusableInTouchMode(true);
            content.getEditText().setFocusableInTouchMode(true);
            userId.getEditText().setFocusableInTouchMode(true);
            privacy.getEditText().setFocusableInTouchMode(true);
            createdDate.getEditText().setFocusableInTouchMode(true);
            imagesMap.keySet().forEach(textInputLayout -> textInputLayout.getEditText().setFocusableInTouchMode(true));
            commentMap.keySet().forEach(textInputLayout -> textInputLayout.getEditText().setFocusableInTouchMode(true));


            toolbar.setTitle("Edit post");
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

            if (toolbar.getMenu() != null) {
                if (toolbar.getMenu().findItem(R.id.itemCheck) == null) {
                    toolbar.inflateMenu(R.menu.edit_profile_menu_app_bar);
                }
                toolbar.getMenu().findItem(R.id.itemCheck).setVisible(true);
            }

            toolbar.setBackgroundColor(Color.parseColor("#FFFBFE"));

            dialog.show();

            CharSequence[] statusItems = new String[]{"PUBLIC", "FRIENDS", "ONLY_ME"};
            privacy.getEditText().setFocusable(false);
            privacy.getEditText().setOnClickListener(v -> {
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setTitle("Choose privacy")
                        .setItems(statusItems, (dialogInterface, i) -> {
                            privacy.getEditText().setText(statusItems[i]);
                        })
                        .create().show();
            });

            toolbar.setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                builder1.setTitle("Update post")
                        .setMessage("Are you sure want to apply changes to this post?")
                        .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {

                            String newPostContent = content.getEditText().getText().toString().trim();
                            String newPostPrivacy = privacy.getEditText().getText().toString().trim();

                            if (!newPostPrivacy.equals(post.getPrivacy()) || !newPostContent.equals(post.getContent())) {
                                if (postDataSource.updatePost(post)) {
                                    dialog.dismiss();
                                    Toast.makeText(view.getContext(), "Update successfully!", Toast.LENGTH_SHORT).show();
                                    notifyItemChanged(position);
                                } else {
                                    Toast.makeText(view.getContext(), "Update failed!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }

                            imagesMap.forEach((textInputLayout, postImages) -> {
                                String url = textInputLayout.getEditText().getText().toString().trim();
                                if (url.isEmpty() && imagesMap.size() == 1) {
                                    AlertDialog.Builder warning = new AlertDialog.Builder(context);
                                    warning.setTitle("Delete all images")
                                            .setMessage("Delete all images will lead to delete post, are you sure want to continue?")
                                            .setNegativeButton("Cancel", (dialogInterface1, i1) -> dialogInterface.dismiss())
                                            .setPositiveButton("Confirm", (dialogInterface1, i1) -> {
                                                if(postImagesDataSource.deletePostImages(postImages)) {
                                                    if(postDataSource.deletePost(post)) {
                                                        this.postList.remove(post);
                                                        this.notifyItemRemoved(holder.getAdapterPosition());
                                                    }
                                                }
                                            }).create().show();
                                } else if (!url.equals(postImages.getImageUrl())) {
                                    postImagesDataSource.deletePostImages(postImages);
                                    postImagesDataSource.createPostImages(new PostImages(url, post));
                                    notifyItemChanged(position);
                                } else if (url.isEmpty()) {
                                    postImagesDataSource.deletePostImages(postImages);
                                    notifyItemChanged(position);
                                }
                            });

                            commentMap.forEach((textInputLayout, comment) -> {
                                String newContent = textInputLayout.getEditText().getText().toString().trim();
                                if (newContent.isEmpty()) {
                                    postCommentDataSource.delete(comment);
                                    notifyItemChanged(position);
                                } else if (!newContent.equals(comment.getContent())) {
                                    comment.setContent(newContent);
                                    postCommentDataSource.update(comment);
                                    notifyItemChanged(position);
                                }
                            });
                            dialog.dismiss();
                        }).create().show();
                return true;
            });
        });

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
            builder1.setTitle("Delete post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {

                        postImagesDataSource.getAllPostImagesByPost(post).forEach(postImages -> {
                            if (postImages != null)
                                postImagesDataSource.deletePostImages(postImages);
                        });

                        postCommentDataSource.getAllCommentByPost(post).forEach(comment -> {
                            if (comment != null) {
                                postCommentReactionDataSource.getAllReactionByComment(comment).forEach(postCommentReaction -> {
                                    if (postCommentReaction != null)
                                        postCommentReactionDataSource.delete(postCommentReaction);
                                    postCommentDataSource.delete(comment);
                                });
                            }
                        });

                        postReactionDataSource.getAllReactionByPost(post).forEach(postReaction -> {
                            if (postReaction != null) postReactionDataSource.delete(postReaction);
                        });


                        if (postDataSource.deletePost(post)) {
                            Toast.makeText(view.getContext(), "Delete successfully!", Toast.LENGTH_SHORT).show();

                            this.postList.remove(post);
                            this.notifyItemRemoved(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(view.getContext(), "Delete failed!", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
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

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }
}
