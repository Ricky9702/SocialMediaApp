package com.example.h2ak.model;

public class InboxPost extends Inbox{
    private Post post;

    public InboxPost() {
        super();
    }

    public InboxPost(String content, Inbox.InboxType inboxType, User userRecieveRequest, User userSentRequest, Post post) {
        super(content, inboxType, userRecieveRequest, userSentRequest);
        this.post = post;
    }

    @Override
    public Post getPost() {
        return post;
    }

    @Override
    public void setPost(Post post) {
        this.post = post;
    }
}
