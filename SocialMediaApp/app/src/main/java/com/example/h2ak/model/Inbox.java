package com.example.h2ak.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Inbox {
    public static final String FRIEND_REQUEST_ACCEPTED_MESSAGE = "You and {{senderName}} are now friends.";
    public static final String FRIEND_REQUEST_DENIED_MESSAGE = "{{senderName}} denied your friend request.";
    public static final String FRIEND_REQUEST_MESSAGE = "{{senderName}} sent you a friend request.";
    public InboxType inboxType;
    private String id;
    private User userSentRequest;
    private String content;
    private String createdDate;
    private boolean read;
    private boolean active;
    private String type;
    private User userRecieveRequest;

    {
        this.createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        id = UUID.randomUUID().toString();
        this.active = true;
        this.read = false;
    }

    public Inbox(String content, Inbox.InboxType inboxType, User userRecieveRequest, User userSentRequest) {
        this.content = content;
        this.inboxType = inboxType;
        this.userRecieveRequest = userRecieveRequest;
        this.userSentRequest = userSentRequest;
        this.type = inboxType.getType();
    }

    public Inbox() {

    }

    public String getId() {
        return this.id;
    }

    public User getUserSentRequest() {
        return this.userSentRequest;
    }

    public String getContent() {
        return this.content;
    }

    public String getCreatedDate() {
        return this.createdDate;
    }

    public boolean isRead() {
        return this.read;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getType() {
        return this.type;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public User getUserRecieveRequest() {
        return this.userRecieveRequest;
    }

    public void setActive(boolean b) {
        this.active = b;
    }

    public void setContent(String message) {
        this.content = message;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setInboxType(InboxType inboxType) {
        this.inboxType = inboxType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserSentRequest(User currentUser) {
        this.userSentRequest = currentUser;
    }

    public void setUserRecieveRequest(User user2) {
        this.userRecieveRequest = user2;
    }

    public void setId(String string) {
        this.id = string;
    }

    public enum InboxType{
        FRIEND_REQUEST("FRIEND_REQUEST"),
        MESSAGE("MESSAGE");
        private String type;
        InboxType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

    }

}
