package com.example.h2ak.model;

import com.example.h2ak.utils.TextInputLayoutUtils;

import java.util.Date;
import java.util.UUID;

public class PostReaction {
    private String id;
    private String createdDate;
    private String type;
    private User user;
    private Post post;

    {
        id = UUID.randomUUID().toString();
        createdDate = TextInputLayoutUtils.simpleDateFormat.format(new Date());
    }

    public PostReaction(PostReactionType postReactionType, User user, Post post) {
        this.type = postReactionType.type;
        this.user = user;
        this.post = post;
    }

    public enum PostReactionType{
        LIKE("LIKE");

        private String type;
        PostReactionType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
