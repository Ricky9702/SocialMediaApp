package com.example.h2ak.model;

import com.example.h2ak.utils.TextInputLayoutUtils;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PostImages {
    private String id;
    private String imageUrl;
    private Post post;
    private String createdDate;

    {
        id = UUID.randomUUID().toString();
        this.createdDate = TextInputLayoutUtils.simpleDateFormat.format(new Date());
    }

    public PostImages () {

    }

    public PostImages(String imageUrl, Post post) {
        this.setImageUrl(imageUrl);
        this.setPost(post);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostImages that = (PostImages) o;
        return id.equals(that.id) && imageUrl.equals(that.imageUrl) && post.equals(that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUrl, post);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
}
