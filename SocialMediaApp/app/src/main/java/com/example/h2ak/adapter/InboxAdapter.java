package com.example.h2ak.adapter;

import android.app.AlertDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.contract.InboxFragmentContract;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Inbox;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private Context context;
    private List<Inbox> inboxList = new ArrayList<>();
    private InboxDataSource inboxDataSource;
    private FriendShipDataSource friendShipDataSource;
    private InboxFragmentContract.View view;

    public InboxAdapter(Context context, InboxFragmentContract.View view) {
        this.context = context;
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
        Inbox inbox = getInboxList().get(position);

        if (inbox.getUserSentRequest().getImageAvatar() != null && !inbox.getUserSentRequest().getImageAvatar().isEmpty()) {
            Glide.with(context)
                    .load(inbox.getUserSentRequest().getImageAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                    .placeholder(R.drawable.baseline_person_24)// Disable memory caching to reload the image
                    .into(holder.imageViewUserSentRequest);
        }
        else {
            holder.imageViewUserSentRequest.setImageResource(R.drawable.baseline_person_24);
        }

        holder.textViewContent.setText(inbox.getContent());
        holder.textViewInboxCreatedDate.setText(inbox.getCreatedDate());
        holder.linearLayoutParent.setEnabled(true);

        holder.btnInboxAction.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnInboxAction);
            popupMenu.inflate(R.menu.inbox_friend_request_menu);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.itemDelete) {
                    if(inboxDataSource.deleteInbox(inbox)) {
                        inboxList.remove(position);
                        notifyItemChanged(position);
                        this.view.onReadInbox(true);
                    }
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

        if (inbox.getType().equals(Inbox.InboxType.MESSAGE.getType())) {
            holder.linearLayoutParent.setOnClickListener(view -> {
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
        } else {
            holder.linearLayoutParent.setOnClickListener(view -> {
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
                CircularImageView imageViewProfileAvatar = childView.findViewById(R.id.imageViewProfileAvatar);
                TextView textViewProfileName = childView.findViewById(R.id.textViewProfileName);
                ImageView imageViewGoto = childView.findViewById(R.id.imageViewGoto);

                imageViewGoto.setVisibility(View.GONE);

                // Set data to view of childView
                Glide.with(context)
                        .load(inbox.getUserSentRequest().getImageAvatar())
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Disable caching to reload the image
                        .placeholder(R.color.not_active_icon)// Disable memory caching to reload the image
                        .into(imageViewProfileAvatar);
                textViewProfileName.setText(inbox.getUserSentRequest().getName());

                // Add childView to ViewGroup
                linearLayout.addView(childView);

                // create dialogue for friend request

                // this friendship is for if the current user accept or denied, then will update
                FriendShip friendShip = friendShipDataSource.findLastestFriendShip(inbox.getUserSentRequest(), inbox.getUserRecieveRequest());
                friendShip.setId(new FriendShip().getId());
                if (friendShip.getStatus() != null && friendShip.getStatus().equals(FriendShip.FriendShipStatus.ACCEPTED)) {
                    holder.linearLayoutParent.setEnabled(false);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                builder.setTitle("Friend Request")
                        .setMessage("Do you want to accept the friend request from " + inbox.getUserSentRequest().getName() + "?")
                        .setView(linearLayout)
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            if (friendShip != null) {
                                friendShip.setStatus(FriendShip.FriendShipStatus.ACCEPTED.getStatus());
                                if (friendShipDataSource.createFriendShip(friendShip)) {
                                    Log.d(this.getClass().getName(), "onBindViewHolder: update friendship success");

                                    // create inbox for user sent the friend request, that they are now friends

                                    // The user name that is accept friend request
                                    String message = Inbox.FRIEND_REQUEST_ACCEPTED_MESSAGE.replace("{{senderName}}", inbox.getUserRecieveRequest().getName());

                                    Inbox newInbox = new Inbox(message, Inbox.InboxType.MESSAGE, inbox.getUserSentRequest(), inbox.getUserRecieveRequest());
                                    if (inboxDataSource.createInbox(newInbox)) {
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
                            friendShipDataSource.createFriendShip(friendShip);
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
        return getInboxList().size();
    }

    public List<Inbox> getInboxList() {
        return inboxList;
    }

    public void setInboxList(List<Inbox> inboxList) {
        this.inboxList = inboxList;
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
