package com.example.h2ak.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxPostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxPostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.contract.InboxFragmentContract;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.example.h2ak.view.activities.PostActivity;
import com.example.h2ak.view.activities.UserProfileActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private Context context;
    private List<Inbox> inboxList = new ArrayList<>();
    private InboxDataSource inboxDataSource;
    private InboxPostDataSource inboxPostDataSource;
    private FriendShipDataSource friendShipDataSource;
    private InboxFragmentContract.View view;
    private String kw = "";

    public InboxAdapter(Context context, InboxFragmentContract.View view) {
        this.context = context;
        inboxPostDataSource = InboxPostDataSourceImpl.getInstance(context);
        inboxDataSource = InboxDataSourceImpl.getInstance(context);
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_inbox_friend_request_layout, parent, false);
        return new InboxAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inbox inbox = inboxList.stream().filter(inbox1 -> inbox1.getContent().toLowerCase().contains(kw.toLowerCase()))
                .collect(Collectors.toList()).get(position);
        if (inbox.getUserSentRequest().getImageAvatar() != null && !inbox.getUserSentRequest().getImageAvatar().isEmpty()) {
            Glide.with(context)
                    .load(inbox.getUserSentRequest().getImageAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                    .placeholder(R.drawable.baseline_avatar_place_holder)// Disable memory caching to reload the image
                    .into(holder.imageViewUserSentRequest);
        }
        else {
            holder.imageViewUserSentRequest.setImageResource(R.drawable.baseline_avatar_place_holder);
        }

        holder.textViewContent.setText(inbox.getContent());
        holder.textViewInboxCreatedDate.setText(TextInputLayoutUtils.covertTimeToText(inbox.getCreatedDate()));
        holder.linearLayoutParent.setEnabled(true);

        holder.btnInboxAction.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnInboxAction);
            popupMenu.inflate(R.menu.inbox_friend_request_menu);
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.itemDelete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Warning")
                            .setMessage("Are you sure you want to delete this inbox?")
                            .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setPositiveButton("Confirm", (dialogInterface, i) -> {
                                if(inboxDataSource.deleteInbox(inbox)) {
                                    inboxList.remove(position);
                                    notifyItemChanged(position);
                                    this.view.onReadInbox(true);
                                    Toast.makeText(context, "Delete inbox successfully!", Toast.LENGTH_SHORT).show();
                                    inboxPostDataSource.delete(inbox.getId());
                                }
                            }).create().show();

                }
                return true;
            });
        });


        // change color for inbox which has been read
        if (inbox.isRead()) {
            holder.textViewContent.setTextColor(Color.GRAY);
            holder.textViewInboxCreatedDate.setTextColor(Color.GRAY);
            holder.imageViewUserSentRequest.setAlpha(Float.parseFloat("0.5"));
        }

        if (!inbox.isActive()) {
            holder.linearLayoutParent.setEnabled(false);
        }

        switch (Inbox.InboxType.valueOf(inbox.getType())) {

            case MESSAGE -> holder.linearLayoutParent.setOnClickListener(view -> {
                inbox.setRead(true);
                this.notifyItemChanged(position);
                inboxDataSource.updateInbox(inbox);
                this.view.onReadInbox(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Friend Request Result")
                        .setMessage(holder.textViewContent.getText())
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .create().show();
            });

            case POST_MESSAGE -> holder.linearLayoutParent.setOnClickListener(view1 -> {
                inbox.setRead(true);
                this.notifyItemChanged(position);
                inboxDataSource.updateInbox(inbox);
                this.view.onReadInbox(true);
                // get the newest post from the user sent request
                Post post = inboxPostDataSource.find(inbox.getId()).getPost();
                if (post != null) {
                    Intent intent = new Intent(context, PostActivity.class);
                    List<String> listPostId = new ArrayList<>();
                    listPostId.add(post.getId());
                    intent.putStringArrayListExtra("listPostId", (ArrayList<String>) listPostId);
                    context.startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Post deleted")
                            .setMessage("This post is no longer exists")
                            .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                            .create().show();
                }
            });

            case FRIEND_REQUEST -> holder.linearLayoutParent.setOnClickListener(view -> {
                // set inbox is read
                inbox.setRead(true);
                this.notifyItemChanged(position);
                inboxDataSource.updateInbox(inbox);
                this.view.onReadInbox(true);

                if (inboxDataSource.updateInbox(inbox)) {
                    Log.d("InboxAdapter onBindViewHolder", "Update inbox success");
                } else {
                    Log.d("InboxAdapter onBindViewHolder", "Update inbox failed");
                }

                // Create View Group
                LinearLayout linearLayout = new LinearLayout(this.context);
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Inflate childView
                View childView = LayoutInflater.from(context).inflate(R.layout.custom_search_profile_layout, linearLayout, false);

                // Get view from childView
                LinearLayout linearLayoutMutualFriends = childView.findViewById(R.id.linearLayoutMutualFriends);
                CircularImageView imageViewFriend1 = childView.findViewById(R.id.imageViewFriend1);
                CircularImageView imageViewFriend2 = childView.findViewById(R.id.imageViewFriend2);
                CircularImageView imageViewProfileAvatar = childView.findViewById(R.id.imageViewProfileAvatar);
                TextView textViewProfileName = childView.findViewById(R.id.textViewProfileName);
                ImageView imageViewGoto = childView.findViewById(R.id.imageViewGoto);
                TextView textViewMutualFriendsCount = childView.findViewById(R.id.textViewMutualFriends);
                TextView textViewCreatedDate = childView.findViewById(R.id.textViewProfileCreatedDate);
                textViewCreatedDate.setVisibility(View.GONE);

                imageViewProfileAvatar.setOnClickListener(view1 -> {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("USER_ID", inbox.getUserSentRequest().getId());
                    context.startActivity(intent);
                });

                List<User> mutualFriendsList = new ArrayList<>();
                mutualFriendsList.addAll(friendShipDataSource.getMutualFriends(inbox.getUserRecieveRequest(), inbox.getUserSentRequest()));

                if (mutualFriendsList.isEmpty()) {
                    linearLayoutMutualFriends.setVisibility(View.GONE);
                } else {
                    int maxImageView = Math.min(mutualFriendsList.size(), 2);

                    Glide.with(context)
                            .load(mutualFriendsList.get(0).getImageAvatar())
                            .placeholder(R.drawable.baseline_avatar_place_holder)
                            .into(imageViewFriend1);

                    if (maxImageView == 1) {
                        imageViewFriend2.setVisibility(View.GONE);
                    } else {
                        Glide.with(context)
                                .load(mutualFriendsList.get(1).getImageAvatar())
                                .placeholder(R.drawable.baseline_avatar_place_holder)
                                .into(imageViewFriend2);
                        imageViewFriend2.setVisibility(View.VISIBLE);
                    }
                    textViewMutualFriendsCount.setText(String.format("%d mutual friends", mutualFriendsList.size()));
                    linearLayoutMutualFriends.setVisibility(View.VISIBLE);
                }





                imageViewGoto.setVisibility(View.GONE);

                // Set data to view of childView
                Glide.with(context)
                        .load(inbox.getUserSentRequest().getImageAvatar())
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                        .placeholder(R.drawable.baseline_avatar_place_holder)// Disable memory caching to reload the image
                        .into(imageViewProfileAvatar);
                textViewProfileName.setText(inbox.getUserSentRequest().getName());

                // Add childView to ViewGroup
                linearLayout.addView(childView);

                // create dialogue for friend request

                // this friendship is for if the current user accept or denied, then will update
                FriendShip friendShip = friendShipDataSource.findLastestFriendShip(inbox.getUserSentRequest(), inbox.getUserRecieveRequest());
                friendShip.setCreatedDate(new FriendShip().getCreatedDate());

                AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                builder.setTitle("Friend Request")
                        .setMessage("Do you want to accept the friend request from " + inbox.getUserSentRequest().getName() + "?")
                        .setView(linearLayout)
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            if (friendShip != null) {
                                friendShip.setStatus(FriendShip.FriendShipStatus.ACCEPTED.getStatus());
                                if (friendShipDataSource.updateFriendShip(friendShip)) {
                                    Log.d(this.getClass().getName(), "onBindViewHolder: update friendship success");

                                    // create inbox for user sent the friend request, that they are now friends

                                    // The user name that is accept friend request
                                    String message = Inbox.FRIEND_REQUEST_ACCEPTED_MESSAGE.replace("{{senderName}}", inbox.getUserRecieveRequest().getName());

                                    Inbox newInbox = new Inbox(message, Inbox.InboxType.MESSAGE, inbox.getUserSentRequest(), inbox.getUserRecieveRequest());
                                    if (inboxDataSource.createInbox(newInbox)) {
                                        message = Inbox.FRIEND_REQUEST_ACCEPTED_MESSAGE.replace("{{senderName}}", inbox.getUserSentRequest().getName());
                                        Inbox newInbox2 = new Inbox(message, Inbox.InboxType.MESSAGE, inbox.getUserRecieveRequest(), inbox.getUserSentRequest());
                                        if (inboxDataSource.createInbox(newInbox2)) {
                                            Log.d(this.getClass().getName(), "onBindViewHolder: create inbox success X2");
                                        } else {
                                            Log.d(this.getClass().getName(), "onBindViewHolder: create inbox failed X2");
                                        }
                                        Log.d(this.getClass().getName(), "onBindViewHolder: create inbox success");
                                    } else {
                                        Log.d(this.getClass().getName(), "onBindViewHolder: create inbox failed");
                                    }

                                } else {
                                    Log.d(this.getClass().getName(), "onBindViewHolder: update friendship failed");
                                }
                                inbox.setActive(false);
                                inboxDataSource.updateInbox(inbox);
                            } else {
                                Log.d(this.getClass().getName(), "onBindViewHolder: friendship is null");
                            }
                        })
                        .setNegativeButton("Delete", (dialogInterface, i) -> {
                            friendShip.setStatus(FriendShip.FriendShipStatus.DELETED.getStatus());
                            friendShipDataSource.updateFriendShip(friendShip);
                            inbox.setActive(false);

                            // create inbox for user sent the friend request, that the user recieve is denied the request

                            // The user name that is accept friend request
                            String message = Inbox.FRIEND_REQUEST_DENIED_MESSAGE.replace("{{senderName}}", inbox.getUserRecieveRequest().getName());

                            Inbox newInbox = new Inbox(message, Inbox.InboxType.MESSAGE, inbox.getUserSentRequest(), inbox.getUserRecieveRequest());
                            if (inboxDataSource.createInbox(newInbox)) {
                                Log.d(this.getClass().getName(), "onBindViewHolder: create inbox success");
                            } else {
                                Log.d(this.getClass().getName(), "onBindViewHolder: create inbox failed");
                            }

                        })
                        .create().show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return inboxList.stream().filter(inbox1 -> inbox1.getContent().toLowerCase().contains(kw.toLowerCase()))
                .collect(Collectors.toList()).size();
    }

    public List<Inbox> getInboxList() {
        return inboxList;
    }

    public void setInboxList(List<Inbox> inboxList) {
        this.inboxList = inboxList;
        this.notifyDataSetChanged();
    }

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
        this.notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayoutParent;
        CircularImageView imageViewUserSentRequest;
        TextView textViewContent, textViewInboxCreatedDate;
        ImageButton btnInboxAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayoutParent = itemView.findViewById(R.id.linearLayoutParent);
            imageViewUserSentRequest = itemView.findViewById(R.id.imageViewUserSentRequest);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewInboxCreatedDate = itemView.findViewById(R.id.textViewInboxCreatedDate);
            btnInboxAction = itemView.findViewById(R.id.btnInboxAction);
        }
    }
}
