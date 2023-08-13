package com.example.h2ak.SQLite.SQLiteDataSource;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.PostCommentReaction;
import com.example.h2ak.model.PostReaction;

import java.util.Set;

public interface PostCommentReactionDataSource {
    boolean create(PostCommentReaction comment);
    boolean delete(PostCommentReaction comment);
    boolean update(PostCommentReaction comment);
    Set<PostCommentReaction> getAllReactionByComment(PostComment comment);
    PostCommentReaction find(PostComment comment, String currentUserId);
}
