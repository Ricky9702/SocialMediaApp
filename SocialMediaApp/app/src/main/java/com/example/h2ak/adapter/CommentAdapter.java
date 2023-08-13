package com.example.h2ak.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentReactionDataSourceImpl;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.PostCommentReaction;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.example.h2ak.view.activities.UserProfileActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private String TAG = "CommentAdapter";
    private List<PostComment> postCommentList;
    private Context context;
    private PostCommentDataSource postCommentDataSource;
    private PostCommentReactionDataSource postCommentReactionDataSource;
    private ViewGroup parent;

    public CommentAdapter(Context context) {
        postCommentList = new ArrayList<>();
        this.context = context;
        postCommentDataSource = PostCommentDataSourceImpl.getInstance(context);
        postCommentReactionDataSource = PostCommentReactionDataSourceImpl.getInstance(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View v = LayoutInflater.from(context).inflate(R.layout.custom_post_comment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostComment comment = postCommentList.get(position);

        User user = comment.getUser();

        if (user.getImageAvatar().isEmpty()) {
            Glide.with(context)
                    .load(R.drawable.baseline_avatar_place_holder)
                    .placeholder(R.drawable.baseline_avatar_place_holder)
                    .error(R.drawable.baseline_report_gmailerrorred_24)
                    .into(holder.imageViewProfileAvatar);
        } else {
            Glide.with(context)
                    .load(user.getImageAvatar())
                    .placeholder(R.drawable.baseline_avatar_place_holder)
                    .error(R.drawable.baseline_report_gmailerrorred_24)
                    .into(holder.imageViewProfileAvatar);
        }


        holder.textViewProfileName.setText(user.getName());
        holder.textViewContent.setText(comment.getContent());
        holder.textViewCommentCreatedDate.setText(TextInputLayoutUtils.covertTimeToText(comment.getCreatedDate()));

        holder.textViewProfileName.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("USER_ID", user.getId());
            context.startActivity(intent);
        });

        holder.imageViewProfileAvatar.setOnClickListener(view -> {
            holder.textViewProfileName.performLongClick();
        });

        User postUser = comment.getPost().getUser();
        // if the post is owned by current user, then allow to delete all comments inside post
        if (MyApp.getInstance().getCurrentUserId().equals(postUser.getId())) {
            holder.btnAction.setEnabled(true);
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.btnAction);
                popupMenu.inflate(R.menu.inbox_friend_request_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.itemDelete) {
                        // Delete
                        AlertDialog.Builder b = new AlertDialog.Builder(context);
                        b.setTitle("Delete comment")
                                .setMessage("Are you sure you want to delete this comment?")
                                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                                    if (postCommentDataSource.delete(comment)) {
                                        Log.d(TAG, "delete: success");
                                        int actualPosition = holder.getAdapterPosition();
                                        postCommentList.remove(actualPosition);
                                        notifyItemRemoved(actualPosition);
                                        notifyItemRangeChanged(actualPosition, postCommentList.size());
                                        Toast.makeText(context, "Delete comment successfully!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d(TAG, "delete: failed");
                                        Toast.makeText(context, "Delete comment failed!!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create().show();
                    }
                    return true;
                });
                popupMenu.show();
            });
        }
        // allow edit/delete the comment made by user
        if (comment.getUser().getId().equals(MyApp.getInstance().getCurrentUserId())) {
            // this is another user post, so only allow to edit the current user comment
            holder.btnAction.setEnabled(true);
            holder.btnAction.setVisibility(View.VISIBLE);

            holder.btnAction.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.btnAction);
                popupMenu.inflate(R.menu.custom_post_current_user);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    // Edit
                    if (menuItem.getItemId() == R.id.itemEditPost) {

                        LinearLayout linearLayout = new LinearLayout(context);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(25, 0, 25, 0);

                        EditText content = new EditText(context);
                        content.setHint("Write your new content.");
                        content.setText(comment.getContent());

                        linearLayout.addView(content, params);

                        AlertDialog.Builder b = new AlertDialog.Builder(context);
                        b.setTitle("Edit content")
                                .setView(linearLayout)
                                .setPositiveButton("Change", (dialogInterface, pos) -> {
                                    comment.setContent(content.getText().toString().trim());
                                    comment.setCreatedDate(new PostComment().getCreatedDate());
                                    if (postCommentDataSource.update(comment)) {
                                        Log.d(TAG, "update: success");
                                        Toast.makeText(context, "Update content successfully!!", Toast.LENGTH_SHORT).show();
                                        notifyItemChanged(holder.getAdapterPosition());
                                        notifyItemRangeChanged(0 , holder.getAdapterPosition());
                                    } else {
                                        Log.d(TAG, "update: failed");
                                        Toast.makeText(context, "Update content failed!!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Cancel", (dialogInterface, pos) -> dialogInterface.dismiss())
                                .create().show();

                    } else {
                        // Delete
                        AlertDialog.Builder b = new AlertDialog.Builder(context);
                        b.setTitle("Delete comment")
                                .setMessage("Are you sure you want to delete this comment?")
                                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                                    if (postCommentDataSource.delete(comment)) {
                                        Log.d(TAG, "delete: success");
                                        int actualPosition = holder.getAdapterPosition();
                                        postCommentList.remove(actualPosition);
                                        notifyItemRemoved(actualPosition);
                                        notifyItemRangeChanged(actualPosition, postCommentList.size());
                                        Toast.makeText(context, "Delete comment successfully!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d(TAG, "delete: failed");
                                        Toast.makeText(context, "Delete comment failed!!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create().show();
                    }
                    return true;
                });
                popupMenu.show();
            });
        }




        // check if the user does like the comment
        PostCommentReaction postCommentReaction = postCommentReactionDataSource.find(comment, MyApp.getInstance().getCurrentUserId());

        Log.d(TAG, "postCommentReaction: "+ postCommentReaction+"");

        if (postCommentReaction != null) { // the user has reaction
            // user like
            if (postCommentReaction.getType().equals(PostCommentReaction.CommentReactionType.LIKE.getType())) {
                holder.btnCommentLike.setImageDrawable(context.getDrawable(R.drawable.baseline_thumb_up_off_alt__active_24));
                holder.btnCommentDislike.setImageDrawable(context.getDrawable(R.drawable.baseline_thumb_down_off_alt_not_active_24));
            } else { // user dislike
                holder.btnCommentDislike.setImageDrawable(context.getDrawable(R.drawable.baseline_thumb_down_off_alt_active_24));
                holder.btnCommentLike.setImageDrawable(context.getDrawable(R.drawable.baseline_thumb_up_off_alt_24));
            }
        } else { // no reaction from user
            holder.btnCommentLike.setImageDrawable(context.getDrawable(R.drawable.baseline_thumb_up_off_alt_24));
            holder.btnCommentDislike.setImageDrawable(context.getDrawable(R.drawable.baseline_thumb_down_off_alt_not_active_24));
        }

        // push size likes of comment to view
        List<PostCommentReaction> postCommentReactions = new ArrayList<>();
        postCommentReactions.addAll(postCommentReactionDataSource.getAllReactionByComment(comment).stream().filter(reaction -> reaction.getType().equals("LIKE")).collect(Collectors.toList()));
        Log.d(TAG, "postCommentReactions: " + postCommentReactions.size());
        holder.textViewCommentLikeSize.setText(String.format("%s", postCommentReactions.size() > 0 ? postCommentReactions.size() : ""));


        // add like, dislike to comment

            // like comment
        holder.btnCommentLike.setOnClickListener(view -> {
            if (postCommentReaction == null) { // the user has no reaction, create like if the user is like
                PostCommentReaction like = new PostCommentReaction(PostCommentReaction.CommentReactionType.LIKE, MyApp.getInstance().getCurrentUser(), comment);
                if (postCommentReactionDataSource.create(like)) {
                    Log.d(TAG, "postCommentReactionDataSource: create success");
                    this.notifyItemChanged(holder.getAdapterPosition());
                } else { Log.d(TAG, "postCommentReactionDataSource: create failed");}
            } else  { // the user has reaction
                if (postCommentReaction.getType().equals(PostCommentReaction.CommentReactionType.LIKE.getType())) {
                    // the user doubled click like button, remove the reaction
                    if (postCommentReactionDataSource.delete(postCommentReaction)) {
                        Log.d(TAG, "postCommentReactionDataSource: delete success");
                        this.notifyItemChanged(holder.getAdapterPosition());
                    } else {
                        Log.d(TAG, "postCommentReactionDataSource: delete failed");
                    }

                } else { // the user has dislike, then update that to like
                    postCommentReaction.setType(PostCommentReaction.CommentReactionType.LIKE.getType());
                    postCommentReactionDataSource.update(postCommentReaction);
                    this.notifyItemChanged(holder.getAdapterPosition());
                }
            }
        });
            // dislike comment
        holder.btnCommentDislike.setOnClickListener(view -> {
            if (postCommentReaction == null) { // the user has no reaction, create like if the user is like
                PostCommentReaction dislike = new PostCommentReaction(PostCommentReaction.CommentReactionType.DISLIKE, MyApp.getInstance().getCurrentUser(), comment);
                if (postCommentReactionDataSource.create(dislike)) {
                    Log.d(TAG, "postCommentReactionDataSource: create success");
                    this.notifyItemChanged(holder.getAdapterPosition());
                } else { Log.d(TAG, "postCommentReactionDataSource: create failed");}
            }

            else  { // the user has reaction
                if (postCommentReaction.getType().equals(PostCommentReaction.CommentReactionType.DISLIKE.getType())) {
                    // the user doubled click dislike button, remove the reaction
                    if(postCommentReactionDataSource.delete(postCommentReaction)) {
                        Log.d(TAG, "postCommentReactionDataSource: delete success");
                        this.notifyItemChanged(holder.getAdapterPosition());
                    } else {
                        Log.d(TAG, "postCommentReactionDataSource: delete failed");
                    }

                } else { // the user has like, then update that to dislike
                    postCommentReaction.setType(PostCommentReaction.CommentReactionType.DISLIKE.getType());
                    postCommentReactionDataSource.update(postCommentReaction);
                    this.notifyItemChanged(holder.getAdapterPosition());
                }
            }
        });

        // add reply comment
        List<PostComment> listCommentChild = new ArrayList<>();
        listCommentChild.addAll(postCommentDataSource.getAllCommentByParent(comment));
        holder.textViewCommentChildSize.setText(listCommentChild.size() > 0 ? listCommentChild.size() + " replies" : "asdasd");

        holder.textViewCommentChildSize.setOnClickListener(view -> {
            View childLayout = LayoutInflater.from(context).inflate(R.layout.custom_post_comment_child, parent, false);
            View parentComment = childLayout.findViewById(R.id.custom_post_comment_item);
//            onBindViewHolder(test, holder.getAdapterPosition());
        });

    }


    @Override
    public int getItemCount() {
        return postCommentList.size();
    }

    public List<PostComment> getPostCommentList() {
        return postCommentList;
    }

    public void addPostComment(PostComment postComment) {
        this.postCommentList.add(postComment);

        Collections.sort(postCommentList, (postComment1, postComment2) -> {
            Date date1 = TextInputLayoutUtils.parseDateFromString(postComment1.getCreatedDate());
            Date date2 = TextInputLayoutUtils.parseDateFromString(postComment2.getCreatedDate());

            // Sorting in descending order (latest items first)
            return date2.compareTo(date1);
        });

        this.notifyItemInserted(0);
        this.notifyItemChanged(0);
    }


    public void setPostCommentList(List<PostComment> postCommentList) {
        this.postCommentList = postCommentList;

        Collections.sort(postCommentList, (post1, post2) -> {
            Date date1 = TextInputLayoutUtils.parseDateFromString(post1.getCreatedDate());
            Date date2 = TextInputLayoutUtils.parseDateFromString(post2.getCreatedDate());

            // Sorting in descending order (latest items first)
            return date2.compareTo(date1);
        });
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView imageViewProfileAvatar;
        TextView textViewProfileName, textViewContent, textViewCommentCreatedDate, textViewCommentLikeSize, textViewCommentChildSize;
        ImageButton btnAction, btnCommentLike, btnCommentDislike, btnCommentReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProfileAvatar = itemView.findViewById(R.id.imageViewProfileAvatar);
            textViewProfileName = itemView.findViewById(R.id.textViewProfileName);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewCommentCreatedDate = itemView.findViewById(R.id.textViewCommentCreatedDate);
            textViewCommentLikeSize = itemView.findViewById(R.id.textViewCommentLikeSize);
            textViewCommentChildSize = itemView.findViewById(R.id.textViewCommentChildSize);
            btnCommentLike = itemView.findViewById(R.id.btnCommentLike);
            btnCommentDislike = itemView.findViewById(R.id.btnCommentDislike);
            btnCommentReply = itemView.findViewById(R.id.btnCommentReply);
            btnAction = itemView.findViewById(R.id.btnAction);
            btnAction.setVisibility(View.GONE);
            btnAction.setEnabled(false);
        }

    }
}
