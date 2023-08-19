package com.example.h2ak.Firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.MySQLiteHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.FriendShipDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.InboxPostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.FriendShipDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.InboxPostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentReactionDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostImagesDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostReactionDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.FriendShip;
import com.example.h2ak.model.Inbox;
import com.example.h2ak.model.InboxPost;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.PostCommentReaction;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.model.PostReaction;
import com.example.h2ak.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDataSync {
    // User
    private DatabaseReference userRef;
    private UserDataSource userDataSource;

    // FriendShip
    private DatabaseReference friendShipRef;
    private FriendShipDataSource friendShipDataSource;

    // Inbox
    private DatabaseReference inboxRef;
    private InboxDataSource inboxDataSource;

    private DatabaseReference inboxPostRef;
    private InboxPostDataSource inboxPostDataSource;

    private DatabaseReference postRef;
    private PostDataSource postDataSource;

    private DatabaseReference postReactionRef;
    private PostReactionDataSource postReactionDataSource;

    private DatabaseReference postCommentRef;
    private PostCommentDataSource postCommentDataSource;

    private DatabaseReference postCommentReactionRef;
    private PostCommentReactionDataSource postCommentReactionDataSource;

    private DatabaseReference postImagesRef;
    private PostImagesDataSource postImagesDataSource;

    private Context context;
    private static FirebaseDataSync instance;
    private String currentUserId;

    public FirebaseDataSync(Context context, String currentUserId) {
        // User
        userRef = FirebaseHelper.getDatabaseReferenceByPath("User");
        userDataSource = UserDataSourceImpl.getInstance(context);

        friendShipRef = FirebaseHelper.getDatabaseReferenceByPath("FriendShip");
        friendShipDataSource = FriendShipDataSourceImpl.getInstance(context);

        inboxRef = FirebaseHelper.getDatabaseReferenceByPath("Inbox");
        inboxDataSource = InboxDataSourceImpl.getInstance(context);

        inboxPostRef = FirebaseHelper.getDatabaseReferenceByPath("InboxPost");
        inboxPostDataSource = InboxPostDataSourceImpl.getInstance(context);

        postRef = FirebaseHelper.getDatabaseReferenceByPath("Post");
        postDataSource = PostDataSourceImpl.getInstance(context);

        postReactionRef = FirebaseHelper.getDatabaseReferenceByPath("PostReaction");
        postReactionDataSource = PostReactionDataSourceImpl.getInstance(context);

        postCommentRef = FirebaseHelper.getDatabaseReferenceByPath("PostComment");
        postCommentDataSource = PostCommentDataSourceImpl.getInstance(context);

        postCommentReactionRef = FirebaseHelper.getDatabaseReferenceByPath("PostCommentReaction");
        postCommentReactionDataSource = PostCommentReactionDataSourceImpl.getInstance(context);

        postImagesRef = FirebaseHelper.getDatabaseReferenceByPath("PostImages");
        postImagesDataSource = PostImagesDataSourceImpl.getInstance(context);

        this.currentUserId = currentUserId;
    }

    public static FirebaseDataSync getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseDataSync(context.getApplicationContext(), MyApp.getInstance().getCurrentUserId());
        }
        return instance;
    }

    public void syncPostCommentReaction() {
        postCommentReactionRef.orderByChild(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    postCommentReactionRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PostCommentReaction commentReaction = getPostCommentReactionBySnapShot(snapshot);
                            if (commentReaction != null) {
                                postCommentReactionDataSource.create(commentReaction);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    postCommentReactionRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PostCommentReaction commentReaction = getPostCommentReactionBySnapShot(snapshot);
                            if (commentReaction != null) {
                                postCommentReactionDataSource.update(commentReaction);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PostCommentReaction commentReaction = getPostCommentReactionBySnapShot(snapshot);
                    if (commentReaction != null) {
                        postCommentReactionDataSource.delete(commentReaction);
                    }
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private PostCommentReaction getPostCommentReactionBySnapShot(DataSnapshot snapshot) {
        String TAG = "getPostCommentReactionBySnapShot";
        PostCommentReaction commentReaction = null;
        if (snapshot.exists()) {

            String id = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_ID).getValue(String.class);
            String type = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_TYPE).getValue(String.class);
            String createdDate = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_CREATED_DATE).getValue(String.class);
            String userId = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_USER_ID).getValue(String.class);
            String commentId = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_REACTION_POST_COMMENT_ID).getValue(String.class);

            if (id == null || id.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: id is null");
                return null;
            } else if (type == null || type.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: type is null");
                return  null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: createdDate is null");
                return  null;
            } else if (userId == null || userId.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: user_id is null");
                return  null;
            } else if (commentId == null || commentId.isEmpty()) {
                Log.d(TAG, "getPostReactionByCursor: comment_id is null");
                return null;
            } else {
                User user = userDataSource.getUserById(userId);
                PostComment comment = postCommentDataSource.getById(commentId);

                if (user == null) {
                    Log.d(TAG, "getPostReactionByCursor: user is null");
                    return null;
                }else if (comment == null) {
                    Log.d(TAG, "getPostReactionByCursor: comment is null");
                    return null;
                } else {
                    commentReaction = new PostCommentReaction(PostCommentReaction.CommentReactionType.valueOf(type), user,
                            comment);
                    commentReaction.setId(id);
                    commentReaction.setCreatedDate(createdDate);
                }
            }
        }
        return commentReaction;
    }


    public void syncPostImages() {
        postImagesRef.orderByChild(MySQLiteHelper.COLUMN_POST_IMAGES_CREATED_DATE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    postImagesRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PostImages postImages = getPostImagesBySnapShot(snapshot);
                            if (postImages != null) {
                                postImagesDataSource.createPostImages(postImages);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PostImages postImages = getPostImagesBySnapShot(snapshot);
                    if (postImages != null) {
                        postImagesDataSource.deletePostImages(postImages);
                    }
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private PostImages getPostImagesBySnapShot(DataSnapshot snapshot) {
        PostImages postImages = null;
        if (snapshot.exists()) {
            String id = snapshot.child(MySQLiteHelper.COLUMN_POST_IMAGES_ID).getValue(String.class);
            String url = snapshot.child(MySQLiteHelper.COLUMN_POST_IMAGES_IMAGE_URL).getValue(String.class);
            String postId = snapshot.child(MySQLiteHelper.COLUMN_POST_IMAGES_POST_ID).getValue(String.class);
            String createdDate = snapshot.child(MySQLiteHelper.COLUMN_POST_IMAGES_CREATED_DATE).getValue(String.class);

            if (id == null || id.isEmpty()) {
                return null;
            } else if (url == null || url.isEmpty()) {
                return null;
            } else if (postId == null || postId.isEmpty()) {
                return null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                return null;
            } else {
                Post post = postDataSource.findPost(postId);
                if (post != null) {
                    postImages = new PostImages(url, post);
                    postImages.setId(id);
                    postImages.setCreatedDate(createdDate);
                }
            }
        }
        return postImages;
    }

    public void syncInboxPost() {
        inboxPostRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {

                    inboxPostRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String id = snapshot.child(MySQLiteHelper.COLUMN_INBOX_POST_ID).getValue(String.class);
                            String postId = snapshot.child(MySQLiteHelper.COLUMN_INBOX_POST_POST_ID).getValue(String.class);
                            if (id == null || id.isEmpty()) {
                                Log.d("syncInboxPost", "onChildAdded: id is null");
                            } else if (postId == null || postId.isEmpty()) {
                                Log.d("syncInboxPost", "onChildAdded: postId is null");
                            } else {
                                Post post = postDataSource.findPost(postId);

                                if (post != null) {
                                    InboxPost inboxPost = new InboxPost();
                                    inboxPost.setId(id);
                                    inboxPost.setPost(post);
                                    inboxPostDataSource.create(inboxPost);
                                }
                                Log.d("syncInboxPost", "onChildAdded: post  is null");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String id = snapshot.child(MySQLiteHelper.COLUMN_INBOX_POST_ID).getValue(String.class);
                    String postId = snapshot.child(MySQLiteHelper.COLUMN_INBOX_POST_POST_ID).getValue(String.class);
                    if (id == null || id.isEmpty()) {
                        Log.d("syncInboxPost", "onChildRemoved: id is null");
                    } else if (postId == null || postId.isEmpty()) {
                        Log.d("syncInboxPost", "onChildRemoved: postId is null");
                    } else {
                        Post post = postDataSource.findPost(postId);

                        if (post != null) {
                            inboxPostDataSource.delete(id);
                        }
                        Log.d("syncInboxPost", "onChildRemoved: post  is null");
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void syncPostComment() {
        postCommentRef.orderByChild(MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    postCommentRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PostComment comment = getCommentBySnapShot(snapshot);
                            if (comment != null) {
                                postCommentDataSource.create(comment);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    postCommentRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PostComment comment = getCommentBySnapShot(snapshot);
                            if (comment != null) {
                                postCommentDataSource.update(comment);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PostComment comment = getCommentBySnapShot(snapshot);
                    if (comment != null) {
                        postCommentDataSource.delete(comment);

                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private PostComment getCommentBySnapShot(DataSnapshot snapshot) {
        PostComment postComment = null;
        PostComment parent = null;
        if (snapshot.exists()) {
            String id = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_ID).getValue(String.class);
            String createdDate = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_CREATED_DATE).getValue(String.class);
            String content = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_CONTENT).getValue(String.class);
            String userId = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_USER_ID).getValue(String.class);
            String postId = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_POST_ID).getValue(String.class);

            String parentId = snapshot.child(MySQLiteHelper.COLUMN_POST_COMMENT_PARENT_ID).getValue(String.class);

            if (parentId != null && !parentId.isEmpty()) {
                parent = postCommentDataSource.getById(parentId);
            }

            if (id == null || id.isEmpty()) {
                Log.d("getCommentBySnapShot", "getCommentByCursor: id is null");
                return null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                Log.d("getCommentBySnapShot", "getCommentByCursor: createdDate is null");
                return null;
            } else if (content == null || content.isEmpty()) {
                Log.d("getCommentBySnapShot", "getCommentByCursor: content is null");
                return null;
            } else if (userId == null || userId.isEmpty()) {
                Log.d("getCommentBySnapShot", "getCommentByCursor: userId is null");
                return null;
            } else if (postId == null || postId.isEmpty()) {
                Log.d("getCommentBySnapShot", "getCommentByCursor: postId is null");
                return null;
            } else {

                User user = userDataSource.getUserById(userId);
                Post post = postDataSource.findPost(postId);


                if (user == null || post == null) {

                    Log.d("getCommentBySnapShot", "getCommentByCursor: user or post is null");
                    return null;
                } else {
                    postComment = new PostComment(content, user, post);
                    postComment.setId(id);
                    postComment.setCreatedDate(createdDate);
                    if (parent != null) postComment.setParent(parent);
                }

            }
        }
        return postComment;
    }
    public void syncPostReaction() {
        postReactionRef.orderByChild(MySQLiteHelper.COLUMN_POST_REACTION_CREATED_DATE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    postReactionRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PostReaction postReaction = getPostReactionBySnapShot(snapshot);
                            if (postReaction != null) {
                                postReactionDataSource.create(postReaction);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    PostReaction postReaction = getPostReactionBySnapShot(snapshot);
                    if (postReaction != null) {
                        postReactionDataSource.delete(postReaction);
                    }
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private PostReaction getPostReactionBySnapShot(DataSnapshot snapshot) {
        if (snapshot.exists()) {

            String id = snapshot.child(MySQLiteHelper.COLUMN_POST_REACTION_ID).getValue(String.class);
            String type = snapshot.child(MySQLiteHelper.COLUMN_POST_REACTION_TYPE).getValue(String.class);
            String createdDate = snapshot.child(MySQLiteHelper.COLUMN_POST_REACTION_CREATED_DATE).getValue(String.class);
            String userId = snapshot.child(MySQLiteHelper.COLUMN_POST_REACTION_USER_ID).getValue(String.class);
            String postId = snapshot.child(MySQLiteHelper.COLUMN_POST_REACTION_POST_ID).getValue(String.class);

            if (id == null || id.isEmpty()) {
                return null;
            } else if (type == null || type.isEmpty()) {
                return  null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                return  null;
            } else if (userId == null || userId.isEmpty()) {
                return  null;
            } else if (postId == null || postId.isEmpty()) {
                return null;
            } else {
                User user = userDataSource.getUserById(userId);
                Post post = postDataSource.findPost(postId);

                if (user == null || post == null) {
                    return null;
                } else {
                    PostReaction postReaction = new PostReaction(PostReaction.PostReactionType.valueOf(type), user, post);
                    postReaction.setId(id);
                    postReaction.setCreatedDate(createdDate);
                    return postReaction;
                }
            }
        }

        return null;
    }


    public void syncPost() {
//        postRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Post post = getPostBySnapShot(dataSnapshot);
//                    if (post != null) {
//                        Log.d("SyncPost", "onDataChange: post not null");
//                        if (!postDataSource.createPost(post)) {
//                            if (postDataSource.updatePost(post)) {
//                                Log.d("SyncPost", "onDataChange: update post success");
//                            } else {
//                                Log.d("SyncPost", "onDataChange: update post failed");
//                            }
//                            Log.d("SyncPost", "onDataChange: creat post failed");
//                        }
//                    }
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        postRef.orderByChild(MySQLiteHelper.COLUMN_POST_CREATED_DATE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    postRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Post post = getPostBySnapShot(snapshot);
                            if (post != null) {
                                if (!postDataSource.createPost(post)) {
                                    Log.d("SyncPost", "onDataChange: create post failed");
                                } else  Log.d("SyncPost", "onDataChange: create post success");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    postRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Post post = getPostBySnapShot(snapshot);
                            if (post != null) {
                                if (postDataSource.updatePost(post))
                                    Log.d("SyncPost", "onDataChange: update post success");
                                else
                                    Log.d("SyncPost", "onDataChange: update post failed");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Post post = getPostBySnapShot(snapshot);
                    if (post != null) {
                        postDataSource.deletePost(post);
                    }
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Post getPostBySnapShot(DataSnapshot snapshot) {
        Post post = null;

        if (snapshot.exists()) {
            String id = snapshot.child(MySQLiteHelper.COLUMN_POST_ID).getValue(String.class);
            String content = snapshot.child(MySQLiteHelper.COLUMN_POST_CONTENT).getValue(String.class);
            String userId = snapshot.child(MySQLiteHelper.COLUMN_POST_USER_ID).getValue(String.class);
            String createdDate = snapshot.child(MySQLiteHelper.COLUMN_POST_CREATED_DATE).getValue(String.class);
            String privacy = snapshot.child(MySQLiteHelper.COLUMN_POST_PRIVACY).getValue(String.class);

            if (id == null || id.isEmpty()) {
                Log.d("DataSync", "getPostBySnapShot: id is null");
                return null;
            } else if (content == null) {
                Log.d("DataSync", "getPostBySnapShot: content is null");
                return null;
            } else if (userId == null || userId.isEmpty()) {
                Log.d("DataSync", "getPostBySnapShot: userId is null");
                return null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                Log.d("DataSync", "getPostBySnapShot: createdDate is null");
                return null;
            } else if (privacy == null || privacy.isEmpty()) {
                Log.d("DataSync", "getPostBySnapShot: privacy is null");
                return null;
            } else {
                User user = userDataSource.getUserById(userId);
                if (user == null) {
                    Log.d("DataSync", "getPostBySnapShot: user is null");
                    return null;
                }

                post = new Post(content, user, Post.PostPrivacy.valueOf(privacy));
                post.setId(id);
                post.setCreatedDate(createdDate);
            }
        }

        return post;
    }


    public void syncUser(OnDataChangeListener listener) {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SyncUser: ", "There is some change here!!");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {
                        String id = snapshot.getKey();
                        User existingUser = userDataSource.getUserById(id);

                        User user = new User();
                        user.setId(snapshot.child(MySQLiteHelper.COLUMN_USER_ID).getValue(String.class));
                        user.setActive(Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_USER_IS_ACTIVE).getValue(Boolean.class)));
                        user.setOnline(Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_USER_IS_ONLINE).getValue(Boolean.class)));
                        user.setRole(snapshot.child(MySQLiteHelper.COLUMN_USER_USER_ROLE).getValue(String.class));
                        user.setBirthday(snapshot.child(MySQLiteHelper.COLUMN_USER_BIRTHDAY).getValue(String.class));
                        user.setBio(snapshot.child(MySQLiteHelper.COLUMN_USER_BIO).getValue(String.class));
                        user.setPassword(snapshot.child(MySQLiteHelper.COLUMN_USER_PASSWORD).getValue(String.class));
                        user.setEmail(snapshot.child(MySQLiteHelper.COLUMN_USER_EMAIL).getValue(String.class));
                        user.setGender(snapshot.child(MySQLiteHelper.COLUMN_USER_GENDER).getValue(String.class));
                        user.setName(snapshot.child(MySQLiteHelper.COLUMN_USER_NAME).getValue(String.class));
                        user.setCreatedDate(snapshot.child(MySQLiteHelper.COLUMN_USER_CREATED_DATE).getValue(String.class));
                        user.setImageCover(snapshot.child(MySQLiteHelper.COLUMN_USER_IMAGE_COVER).getValue(String.class));
                        user.setImageAvatar(snapshot.child(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR).getValue(String.class));

                        if (existingUser == null) {
                            userDataSource.createUser(user);
                            listener.onDataChange();
                        } else {
                            userDataSource.UpdateUserChangeOnFirebase(user);
                            listener.onDataChange();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    User user = userDataSource.getUserById(snapshot.getKey());
                    if (user != null) {
                        userDataSource.deleteUser(user);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private User getUserBySnapShot(DataSnapshot snapshot) {
        if (snapshot.exists()) {
            User user = new User();
            user.setId(snapshot.child(MySQLiteHelper.COLUMN_USER_ID).getValue(String.class));
            user.setActive(Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_USER_IS_ACTIVE).getValue(Boolean.class)));
            user.setOnline(Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_USER_IS_ONLINE).getValue(Boolean.class)));
            user.setRole(snapshot.child(MySQLiteHelper.COLUMN_USER_USER_ROLE).getValue(String.class));
            user.setBirthday(snapshot.child(MySQLiteHelper.COLUMN_USER_BIRTHDAY).getValue(String.class));
            user.setBio(snapshot.child(MySQLiteHelper.COLUMN_USER_BIO).getValue(String.class));
            user.setPassword(snapshot.child(MySQLiteHelper.COLUMN_USER_PASSWORD).getValue(String.class));
            user.setEmail(snapshot.child(MySQLiteHelper.COLUMN_USER_EMAIL).getValue(String.class));
            user.setGender(snapshot.child(MySQLiteHelper.COLUMN_USER_GENDER).getValue(String.class));
            user.setName(snapshot.child(MySQLiteHelper.COLUMN_USER_NAME).getValue(String.class));
            user.setCreatedDate(snapshot.child(MySQLiteHelper.COLUMN_USER_CREATED_DATE).getValue(String.class));
            user.setImageCover(snapshot.child(MySQLiteHelper.COLUMN_USER_IMAGE_COVER).getValue(String.class));
            user.setImageAvatar(snapshot.child(MySQLiteHelper.COLUMN_USER_IMAGE_AVATAR).getValue(String.class));

            if (UserDataSourceImpl.checkValidFields(user)) {
                return user;
            }

        }
        return null;
    }


    public void syncFriendShip(OnDataChangeListener listener) {
        friendShipRef.orderByChild(MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    friendShipRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            FriendShip friendShip = getFriendShipBySnapShot(snapshot);
                            if (friendShip != null) {
                                if (friendShipDataSource.createFriendShip(friendShip)) {
                                    Log.d("syncFriendShip", "syncFriendShip: create success");
                                    listener.onDataChange();
                                } else {
                                    Log.d("syncFriendShip", "syncFriendShip: create failed");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    friendShipRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            FriendShip friendShip = getFriendShipBySnapShot(snapshot);
                            if (friendShip != null) {
                                if (friendShipDataSource.updateFriendShip(friendShip)) {
                                    Log.d("syncFriendShip", "syncFriendShip: create success");
                                    listener.onDataChange();
                                } else {
                                    Log.d("syncFriendShip", "syncFriendShip: create failed");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    FriendShip friendShip = getFriendShipBySnapShot(snapshot);
                    if (friendShip != null) {
                        if (friendShipDataSource.delete(friendShip)) {
                            Log.d("syncFriendShip: ", "delete success");
                            listener.onDataChange();
                        } else {
                            Log.d("syncFriendShip: ", "delete failed");
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private FriendShip getFriendShipBySnapShot(DataSnapshot snapshot) {
        FriendShip friendShip = null;
        if (snapshot.exists()) {
            String id = snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_ID).getValue(String.class);
            String createdDate = snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_CREATED_DATE).getValue(String.class);
            String status = snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_STATUS).getValue(String.class);
            String userId1 = snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_1).getValue(String.class);
            String userId2 = snapshot.child(MySQLiteHelper.COLUMN_FRIENDSHIP_USER_2).getValue(String.class);

            if (id == null || id.isEmpty()) {
                Log.d("getFriendShipBySnapShot", "getFriendShipBySnapShot: id is null");
                return null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                Log.d("getFriendShipBySnapShot", "getFriendShipBySnapShot: createdDate is null");
                return null;
            } else if (status == null || status.isEmpty()) {
                Log.d("getFriendShipBySnapShot", "getFriendShipBySnapShot: status is null");
                return null;
            } else if (userId1 == null || userId1.isEmpty()) {
                Log.d("getFriendShipBySnapShot", "getFriendShipBySnapShot: userId1 is null");
                return null;
            } else if (userId2 == null || userId2.isEmpty()) {
                Log.d("getFriendShipBySnapShot", "getFriendShipBySnapShot: userId2 is null");
                return null;
            } else {
                User user1 = userDataSource.getUserById(userId1);
                User user2 = userDataSource.getUserById(userId2);

                if (user1 == null || user2 == null) {
                    Log.d("getFriendShipBySnapShot", "getFriendShipBySnapShot: user1 or user2 is null");
                    return null;
                }
                friendShip = new FriendShip();
                friendShip.setId(id);
                friendShip.setCreatedDate(createdDate);
                friendShip.setStatus(status);
                friendShip.setUser1(user1);
                friendShip.setUser2(user2);
                friendShip.setFriendShipStatus(FriendShip.FriendShipStatus.valueOf(friendShip.getStatus()));

            }
        }
        return friendShip;
    }

    public void syncInbox(OnDataChangeListener listener) {
//        inBoxRef.orderByChild(MySQLiteHelper.COLUMN_INBOX_CREATED_DATE).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("SyncInbox: ", "There is some change here!!");
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Inbox inbox = getInboxBySnapShot(snapshot);
//                    if (inbox != null) {
//                        if (inboxDataSource.createInbox(inbox)) {
//                            listener.onDataChange();
//                            Log.d("SyncInbox: ", "Create success");
//                        } else {
//                            Log.d("SyncInbox: ", "Create failed");
//                            if (inboxDataSource.updateInbox(inbox)) {
//                                Log.d("SyncInbox: ", "Update success");
//                                listener.onDataChange();
//                            } else Log.d("SyncInbox: ", "Update failed");
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        inboxRef.orderByChild(MySQLiteHelper.COLUMN_INBOX_CREATED_DATE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    inboxRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Inbox inbox = getInboxBySnapShot(snapshot);
                            if (inbox != null) {
                                if (inboxDataSource.createInbox(inbox)) {
                                    Log.d("SyncInbox: ", "Create success");
                                    listener.onDataChange();
                                } else {
                                    Log.d("SyncInbox: ", "Create failed");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    inboxRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Inbox inbox = getInboxBySnapShot(snapshot);
                            if (inbox != null) {
                                if (inboxDataSource.updateInbox(inbox)) {
                                    Log.d("SyncInbox: ", "Create success");
                                    listener.onDataChange();
                                } else {
                                    Log.d("SyncInbox: ", "Create failed");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Inbox inbox = getInboxBySnapShot(snapshot);
                    if (inbox != null) {
                        if (inboxDataSource.deleteInbox(inbox)) {
                            Log.d("SyncInbox: ", "delete success");
                            listener.onDataChange();
                        } else {
                            Log.d("SyncInbox: ", "delete failed");
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Inbox getInboxBySnapShot(DataSnapshot snapshot) {
        Inbox inbox = null;
        if (snapshot.exists()) {
            String id = snapshot.child(MySQLiteHelper.COLUMN_INBOX_ID).getValue(String.class);
            String content = snapshot.child(MySQLiteHelper.COLUMN_INBOX_CONTENT).getValue(String.class);
            String createdDate = snapshot.child(MySQLiteHelper.COLUMN_INBOX_CREATED_DATE).getValue(String.class);
            String type = snapshot.child(MySQLiteHelper.COLUMN_INBOX_TYPE).getValue(String.class);
            boolean read = Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_INBOX_IS_READ).getValue(Boolean.class));
            boolean active = Boolean.TRUE.equals(snapshot.child(MySQLiteHelper.COLUMN_INBOX_IS_ACTIVE).getValue(Boolean.class));
            String userId1 = snapshot.child(MySQLiteHelper.COLUMN_INBOX_USER_1).getValue(String.class);
            String userId2 = snapshot.child(MySQLiteHelper.COLUMN_INBOX_USER_2).getValue(String.class);
            if (id == null || id.isEmpty()) {
                Log.d("getInboxBySnapShot", "getInboxBySnapShot: id is null");
                return null;
            } else if (content == null || content.isEmpty()) {
                Log.d("getInboxBySnapShot", "getInboxBySnapShot: content is null");
                return null;
            } else if (createdDate == null || createdDate.isEmpty()) {
                Log.d("getInboxBySnapShot", "getInboxBySnapShot: createdDate is null");
                return null;
            } else if (type == null || type.isEmpty()) {
                Log.d("getInboxBySnapShot", "getInboxBySnapShot: type is null");
                return null;
            } else if (userId1 == null || userId1.isEmpty()) {
                Log.d("getInboxBySnapShot", "getInboxBySnapShot: user1Id is null");
                return null;
            } else if (userId2 == null || userId2.isEmpty()) {
                Log.d("getInboxBySnapShot", "getInboxBySnapShot: user2Id is null");
                return null;
            } else {
                User user1 = userDataSource.getUserById(userId1);
                User user2 = userDataSource.getUserById(userId2);

                if (user1 == null || user2 == null) {
                    Log.d("getInboxBySnapShot", "getInboxBySnapShot: user1 or user2 is null");
                    return null;
                }
                inbox = new Inbox();
                inbox.setId(id);
                inbox.setContent(content);
                inbox.setCreatedDate(createdDate);
                inbox.setType(type);
                inbox.setRead(read);
                inbox.setInboxType(Inbox.InboxType.valueOf(inbox.getType()));
                inbox.setActive(active);
                inbox.setUserRecieveRequest(user1);
                inbox.setUserSentRequest(user2);
            }
        }
        return inbox;
    }

    public interface OnDataChangeListener {
        void onDataChange();
    }

}
