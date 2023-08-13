package com.example.h2ak.model;

import com.example.h2ak.utils.TextInputLayoutUtils;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class PostCommentReaction {
    private String id;
    private String createdDate;
    private String type;
    private User user;
    private PostComment PostComment;

    {
        id = UUID.randomUUID().toString();
        createdDate = TextInputLayoutUtils.simpleDateFormat.format(new Date());
    }

    public PostCommentReaction(CommentReactionType commentReactionType, User user, PostComment postComment) {
        this.type = commentReactionType.getType();
        this.user = user;
        this.PostComment = postComment;
    }



    public enum CommentReactionType{
        LIKE("LIKE"), DISLIKE("DISLIKE");

        private String type;
        CommentReactionType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCommentReaction that = (PostCommentReaction) o;
        return id.equals(that.id) && createdDate.equals(that.createdDate) && type.equals(that.type) && user.equals(that.user) && Objects.equals(PostComment, that.PostComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdDate, type, user, PostComment);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public com.example.h2ak.model.PostComment getPostComment() {
        return PostComment;
    }

    public void setPostComment(com.example.h2ak.model.PostComment postComment) {
        PostComment = postComment;
    }
}
