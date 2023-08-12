package com.example.h2ak.model;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Post {
    private String id;
    private String content;
    private String createdDate;
    private String privacy;
    private User user;
    private PostPrivacy postPrivacy;

    {
        id = UUID.randomUUID().toString();
        this.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        this.setPrivacy(PostPrivacy.PUBLIC.getPrivacy());
    }

    public Post() {

    }

    public Post(String content, User user, PostPrivacy postPrivacy) {
        this.content = content;
        this.user = user;
       this.privacy = postPrivacy.getPrivacy();
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
        this.postPrivacy = PostPrivacy.valueOf(privacy);
    }

    public enum PostPrivacy{
        PUBLIC("PUBLIC"),
        FRIENDS("FRIENDS"),
        ONLY_ME("ONLY_ME");

        private String privacy;
        PostPrivacy(String privacy) {
            this.privacy = privacy;
        }

        public String getPrivacy() {
            return privacy;
        }

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

}
