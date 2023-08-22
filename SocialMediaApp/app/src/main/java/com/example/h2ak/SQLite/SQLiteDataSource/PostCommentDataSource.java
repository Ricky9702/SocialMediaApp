package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.User;

import org.w3c.dom.Comment;

import java.util.Set;

public interface PostCommentDataSource {
    boolean create(PostComment comment);
    boolean delete(PostComment comment);
    boolean update(PostComment comment);
    boolean updateParentField(PostComment comment);
    Set<PostComment> getAllCommentByPost(Post post);
    Set<PostComment> getAllCommentByUser(User user);
    Set<PostComment> getAllCommentByParent(PostComment comment);
    PostComment getById(String id);
    PostComment getNewestComment(User user);
}
