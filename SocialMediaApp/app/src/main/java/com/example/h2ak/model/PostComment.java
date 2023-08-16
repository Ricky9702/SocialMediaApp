package com.example.h2ak.model;

import com.example.h2ak.utils.TextInputLayoutUtils;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class PostComment {
    private String id;
    private User user;
    private Post post;
    private String createdDate;
    private String content = "";
    private PostComment parent;

    {
        id = UUID.randomUUID().toString();
        createdDate = TextInputLayoutUtils.simpleDateFormat.format(new Date());
        parent = null;
    }


    public PostComment() {

    }

    public PostComment(String content, User user, Post post) {
        this.content = content;
        this.user = user;
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostComment that = (PostComment) o;
        return id.equals(that.id) && user.getId().equals(that.user.getId()) && post.getId().equals(that.post.getId()) && createdDate.equals(that.createdDate) && content.equals(that.content);
    }

    @Override
    public String toString() {
        return "PostComment{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", post=" + post +
                ", createdDate='" + createdDate + '\'' +
                ", content='" + content + '\'' +
                ", parent=" + parent +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, post, createdDate, content);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostComment getParent() {
        return parent;
    }

    public void setParent(PostComment parent) {
        this.parent = parent;
    }
}
