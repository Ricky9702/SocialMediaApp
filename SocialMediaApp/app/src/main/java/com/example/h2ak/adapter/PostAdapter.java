package com.example.h2ak.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.PostCommentDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostReactionDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostCommentDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostImagesDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostReactionDataSourceImpl;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostComment;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.model.PostReaction;
import com.example.h2ak.utils.GalleryUtils;
import com.example.h2ak.utils.PermissionUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.relex.circleindicator.CircleIndicator2;

public class PostAdapter extends PostPreviewAdapter {
    private Context context;
    private List<PostImages> insertPostImagesList = new ArrayList<>();
    private List<Post> postList = new ArrayList<>();
    private GalleryUtils galleryUtils;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    private ImageSliderEditAdapter adapter;
    private Post currentPost;
    private OnChangePostListener listener;
    private Boolean isEnableAction = true;
    private PostImagesDataSource postImagesDataSource;
    private PostDataSource postDataSource;
    private PostReactionDataSource postReactionDataSource;
    private PostCommentDataSource postCommentDataSource;

    public PostAdapter(Context context, OnChangePostListener listener) {
        super(context);
        this.context = context;
        galleryUtils = new GalleryUtils();
        this.listener = listener;
        postCommentDataSource = PostCommentDataSourceImpl.getInstance(context);
        postReactionDataSource = PostReactionDataSourceImpl.getInstance(context);
        postImagesDataSource = PostImagesDataSourceImpl.getInstance(context);
        postDataSource = PostDataSourceImpl.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        this.setPost(post, false);

        List<PostImages> postImages = postImagesDataSource.getAllPostImagesByPost(postList.get(position)).stream().collect(Collectors.toList());

        if (!insertPostImagesList.isEmpty()) {
            postImages.addAll(insertPostImagesList);
        }


        this.setSnapHelper(null);

        this.setPostImages(postImages);

        super.onBindViewHolder(holder, position);

        holder.LinearLayoutPostStats.setVisibility(View.VISIBLE);
        holder.linearLayoutLikeCommentPost.setVisibility(View.VISIBLE);
        holder.btnPostAction.setVisibility(View.VISIBLE);

        if (holder.textViewPostContent.getText().toString().trim().isEmpty()) {
            holder.textViewPostContent.setVisibility(View.GONE);
        } else {
            holder.textViewPostContent.setVisibility(View.VISIBLE);
        }

        if(!isEnableAction) {
            holder.btnPostAction.setVisibility(View.GONE);
            holder.btnPostAction.setEnabled(false);
        } else {
            holder.btnPostAction.setVisibility(View.VISIBLE);
            holder.btnPostAction.setEnabled(true);
        }

        // Add comment system

        Set<PostComment> postCommentSet = postCommentDataSource.getAllCommentByPost(post);
        Log.d("TAG", "postCommentSet: " + postCommentSet.size());

        // check if then current has not click the like button yet
        Set<PostReaction> postReactionSet = postReactionDataSource.getAllReactionByPost(post);

        Log.d("TAG", "postReactionSet: "+ postReactionSet.size());


        // Check if the post has any comments
        if (postCommentSet.isEmpty()) {
            holder.textViewPostStatsComment.setVisibility(View.GONE);
        } else {
            holder.textViewPostStatsComment.setText(String.format("%d Comments", postCommentSet.size()));
            holder.textViewPostStatsComment.setVisibility(View.VISIBLE);
        }


        holder.textViewPostStatsComment.setOnClickListener(view1 -> buildTabDialog(postReactionSet, postCommentSet, post,  1));

        holder.btnCommentPost.setOnClickListener(view -> buildTabDialog(postReactionSet, postCommentSet, post,  1));

        // Add like system

        Drawable likeDrawableActive = context.getDrawable(R.drawable.baseline_thumb_up_off_alt__active_24);
        Drawable likeDrawableNotActive = context.getDrawable(R.drawable.baseline_thumb_up_off_alt_24);

        PostReaction found = postReactionDataSource.find(post, MyApp.getInstance().getCurrentUserId());

        // there is not like yet
        if (postReactionSet.isEmpty()) {
            holder.btnLikePost.setImageDrawable(likeDrawableNotActive);
            holder.textViewPostStatsLike.setVisibility(View.GONE);
        } else {
            // check if then current user is like yet?
            if (found != null ) {
                holder.btnLikePost.setImageDrawable(likeDrawableActive);
            }

            holder.textViewPostStatsLike.setVisibility(View.VISIBLE);
            holder.textViewPostStatsLike.setText(String.format("%d Likes", postReactionSet.size()));


            // inflate the users profile like
            holder.textViewPostStatsLike.setOnClickListener(view -> buildTabDialog(postReactionSet, postCommentSet, post,  0));

        }

        holder.btnLikePost.setOnClickListener(view -> {

            // the current user does not click yet then create a new reaction
            if (found == null) {
                PostReaction postReaction = new PostReaction(PostReaction.PostReactionType.LIKE, MyApp.getInstance().getCurrentUser(), post);
                if (postReactionDataSource.create(postReaction)) {
                    postReactionSet.add(postReaction);
                    notifyItemInserted(holder.getAdapterPosition());
                    Log.d("PostAdapter", "createPostReaction: success ");
                } else {
                    Log.d("PostAdapter", "createPostReaction: failed ");
                }
            } else {
                // delete the like
                if (postReactionDataSource.delete(found)) {
                    postReactionSet.remove(found);
                    notifyItemRemoved(holder.getAdapterPosition());
                    Log.d("PostAdapter", "deletePostReaction: success ");
                } else {
                    Log.d("PostAdapter", "deletePostReaction: failed ");
                }
            }
            this.notifyItemChanged(holder.getAdapterPosition());
        });



        // Add edit, delete post for current user
        holder.btnPostAction.setOnClickListener(view -> {

            this.setCurrentPost(post);

            PopupMenu popupMenu = new PopupMenu(context, holder.btnPostAction);

            popupMenu.inflate(R.menu.custom_post_current_user);

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(menuItem -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                if (menuItem.getItemId() == R.id.itemEditPost) {

                    String[] items = new String[]{"Change content", "Change privacy", "Change images"};

                    builder.setTitle("Edit Post")
                            .setItems(items, (dialogInterface, i) -> {
                                if (items[i].contains("content")) {
                                    LinearLayout linearLayout = new LinearLayout(context);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(25, 0, 25, 0);

                                    EditText content = new EditText(context);
                                    content.setHint("Write your new content.");
                                    content.setText(post.getContent());

                                    linearLayout.addView(content, params);

                                    AlertDialog.Builder b = new AlertDialog.Builder(context);
                                    b.setTitle("Edit content")
                                            .setView(linearLayout)
                                            .setPositiveButton("Change", (d, pos) -> {
                                                if (postDataSource.updatePost(post)) {
                                                    post.setContent(content.getText().toString().trim());
                                                    this.setPost(post, true);
                                                    Toast.makeText(context, "Update post successfully!!", Toast.LENGTH_SHORT).show();
                                                    postDataSource.updatePost(post);
                                                } else {
                                                    Toast.makeText(context, "Update post failed!!", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .setNegativeButton("Cancel", (d, pos) -> dialogInterface.dismiss())
                                            .create().show();
                                } else if (items[i].contains("privacy")) {
                                    LinearLayout linearLayout = new LinearLayout(context);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(25, 0, 25, 0);

                                    //Create adapter spinner
                                    String[] options = new String[]{"PUBLIC", "FRIENDS", "ONLY_ME"};
                                    SpinnerGenderAdapter adapter = new SpinnerGenderAdapter(context, options);

                                    //Create spinner
                                    Spinner spinner = new Spinner(context);
                                    spinner.setAdapter(adapter);

                                    int selectedIndex = Arrays.asList(options).indexOf(post.getPrivacy());

                                    spinner.setSelection(selectedIndex);

                                    linearLayout.addView(spinner, params);

                                    AlertDialog.Builder b = new AlertDialog.Builder(context);
                                    b.setTitle("Edit privacy")
                                            .setView(linearLayout)
                                            .setPositiveButton("Change", (d, pos) -> {
                                                if (postDataSource.updatePost(post)) {
                                                    post.setPrivacy(spinner.getSelectedItem().toString());
                                                    this.setPost(post, true);
                                                    Toast.makeText(context, "Update post successfully!!", Toast.LENGTH_SHORT).show();
                                                    postDataSource.updatePost(post);
                                                } else {
                                                    Toast.makeText(context, "Update post failed!!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("Cancel", (d, pos) -> dialogInterface.dismiss())
                                            .create().show();
                                } else {
                                    // change the image of post
                                    LinearLayout linearLayout = new LinearLayout(context);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(25, 25, 25, 0);

                                    View v = LayoutInflater.from(context).inflate(R.layout.custom_edit_post_images_layout, holder.recyclerViewImageSlider, false);

                                    RecyclerView recyclerViewImageSlider = v.findViewById(R.id.recyclerViewImageSlider);

                                    CircleIndicator2 indicator2 = v.findViewById(R.id.indicator);

                                    recyclerViewImageSlider.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

                                    ImageSliderEditAdapter imageSliderEditAdapter = new ImageSliderEditAdapter(context);

                                    imageSliderEditAdapter.setPostImagesList(postImages);

                                    recyclerViewImageSlider.setAdapter(imageSliderEditAdapter);

                                    PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();

                                    pagerSnapHelper.attachToRecyclerView(recyclerViewImageSlider);

                                    indicator2.attachToRecyclerView(recyclerViewImageSlider, pagerSnapHelper);

                                    imageSliderEditAdapter.registerAdapterDataObserver(indicator2.getAdapterDataObserver());

                                    TextView textViewAddImage = v.findViewById(R.id.textViewAddImage);

                                    textViewAddImage.setOnClickListener(view1 -> {
                                        if (PermissionUtils.checkStoragePermission(context)) {
                                            this.setAdapter(imageSliderEditAdapter);
                                            galleryUtils.openGallery(galleryActivityResultLauncher);
                                        } else {
                                            PermissionUtils.requestStoragePermission(context);
                                        }
                                    });


                                    linearLayout.addView(v, params);

                                    AlertDialog.Builder b = new AlertDialog.Builder(context);
                                    b.setTitle("Edit Images")
                                            .setView(linearLayout)
                                            .setNegativeButton("Cancel", (dialogInterface1, i1) -> {
                                                dialogInterface.dismiss();
                                            })
                                            .setPositiveButton("Confirm change", (dialogInterface1, i1) -> {
                                                int actualPosition = holder.getAdapterPosition();

                                                Boolean[] isSuccess = new Boolean[2];
                                                isSuccess[0] = null;
                                                isSuccess[1] = null;

                                                List<PostImages> insertList = new ArrayList<>();
                                                List<PostImages> deleteList = new ArrayList<>();

                                                insertList.addAll(imageSliderEditAdapter.getInsertList());
                                                deleteList.addAll(imageSliderEditAdapter.getDeletedList());

                                                if (!insertList.isEmpty()) {
                                                    insertList.forEach(postImages1 ->
                                                            FirebaseHelper.uploadImageToFirebaseCloud(context,
                                                                    Uri.parse(postImages1.getImageUrl()),
                                                                    FirebaseHelper.getImagePostStorageRef(),
                                                                    new FirebaseHelper.OnImageUploadListener() {
                                                                        @Override
                                                                        public void onImageUploadSuccess(String imageUrl) {
                                                                            postImages1.setImageUrl(imageUrl);
                                                                            if (postImagesDataSource.createPostImages(postImages1)) {
                                                                                Log.d("insertList", "create success ");
                                                                                if (isSuccess[0] == null) {
                                                                                    isSuccess[0] = true;
                                                                                    Toast.makeText(context.getApplicationContext(), "create image successfully!!", Toast.LENGTH_SHORT).show();
                                                                                    notifyItemChanged(actualPosition);
                                                                                    post.setCreatedDate(new Post().getCreatedDate());
                                                                                    postDataSource.updatePost(post);
                                                                                    listener.onChange(true);
                                                                                }
                                                                            } else {
                                                                                Log.d("insertList", "create image failed!! ");
                                                                                if (isSuccess[0] == null) {
                                                                                    isSuccess[0] = false;
                                                                                    Toast.makeText(context.getApplicationContext(), "create image failed!!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onImageUploadFailure(String errorMessage) {
                                                                            Log.d("onImageUploadFailure", "onImageUploadFailure: " + errorMessage);
                                                                        }
                                                                    }));
                                                }
                                                if (!deleteList.isEmpty()) {

                                                    if (imageSliderEditAdapter.getPostImagesList().isEmpty()) {
                                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                        builder1.setTitle("Delete Warning")
                                                                .setMessage("Are you sure want to delete all images which will lead to delete post?")
                                                                .setNegativeButton("Cancel", (dialogInterface2, i2) -> {
                                                                    dialogInterface.dismiss();
                                                                })
                                                                .setPositiveButton("Confirm", (dialogInterface2, i2) -> {
                                                                    deleteList.forEach(postImages1 -> {
                                                                        if (postImagesDataSource.deletePostImages(postImages1)) {
                                                                            Log.d("deleteList", "delete success ");
                                                                            isSuccess[1] = true;
                                                                        } else {
                                                                            Log.d("deleteList", "delete failed ");
                                                                            isSuccess[1] = false;
                                                                        }
                                                                        if (postDataSource.deletePost(post)) {
                                                                            postList.remove(actualPosition);
                                                                            notifyItemRemoved(actualPosition);
                                                                            notifyItemRangeChanged(actualPosition, postList.size());
                                                                            Toast.makeText(context, "Delete post successfully!!", Toast.LENGTH_SHORT).show();
                                                                            listener.onChange(true);
                                                                        } else {
                                                                            Toast.makeText(context, "Delete post failed!!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                })
                                                                .create().show();
                                                    } else {
                                                        deleteList.forEach(postImages1 -> {
                                                            if (postImagesDataSource.deletePostImages(postImages1)) {
                                                                Log.d("deleteList", "delete success ");
                                                                isSuccess[1] = true;
                                                            } else {
                                                                Log.d("deleteList", "delete failed ");
                                                                isSuccess[1] = false;
                                                            }
                                                        });
                                                    }
                                                }

                                                if (isSuccess[1] != null && isSuccess[1]) {
                                                    Toast.makeText(context, "delete image successfully!!", Toast.LENGTH_SHORT).show();
                                                    notifyItemChanged(actualPosition);
                                                    post.setCreatedDate(new Post().getCreatedDate());
                                                    postDataSource.updatePost(post);
                                                    listener.onChange(true);
                                                } else if (isSuccess[1] != null && !isSuccess[1]) {
                                                    Toast.makeText(context, "delete image failed!!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setOnDismissListener(dialogInterface1 -> {
                                                imageSliderEditAdapter.insertDeletedItems();
                                            })
                                            .create().show();
                                }
                            })
                            .create().show();
                } else {
                    // delete post
                    builder.setTitle("Delete post")
                            .setMessage("Are you sure want to delete this post?")
                            .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setPositiveButton("Confirm", (dialogInterface, i) -> {
                                if (postDataSource.deletePost(post)) {

                                    // delete post images inside the post
                                    postImages.forEach(postImages1 -> postImagesDataSource.deletePostImages(postImages1));

                                    // delete post like inside the post
                                    postReactionSet.forEach(postReaction -> postReactionDataSource.delete(postReaction));

                                    postCommentSet.forEach(postComment -> postCommentDataSource.delete(postComment));


                                    int actualPosition = holder.getAdapterPosition();
                                    postList.remove(actualPosition);
                                    notifyItemRemoved(actualPosition);
                                    notifyItemRangeChanged(actualPosition, postList.size());
                                    Toast.makeText(context, "Delete post successfully!!", Toast.LENGTH_SHORT).show();
                                    listener.onChange(true);
                                } else {
                                    Toast.makeText(context, "Delete post failed!!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create().show();
                }
                return true;
            });

        });

    }

    private void buildTabDialog(Set<PostReaction> postReactionSet, Set<PostComment> postCommentSet, Post post, int selected) {

        AlertDialog.Builder tabLayout = new AlertDialog.Builder(context);
        LinearLayout linearLayoutParent = new LinearLayout(context);
        linearLayoutParent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 10, 0, 0);

        View v = LayoutInflater.from(context).inflate(R.layout.custom_post_tab, null , false);

        TabLayout tab = v.findViewById(R.id.tabLayout);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // likes
                if (tab.getPosition() == 0) {

                    if (linearLayoutParent.getChildCount() > 1) {
                        linearLayoutParent.removeViewAt(1);
                    }

                    Log.d("TAG", "onTabSelected: "+ postReactionSet.size());

                    RecyclerView recyclerViewSearchHistory = new RecyclerView(context);

                    recyclerViewSearchHistory.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewSearchHistory.setVisibility(View.VISIBLE);

                    ProfileItemAdapter ProfileItemAdapter = new ProfileItemAdapter(context);
                    ProfileItemAdapter.setUserList(postReactionSet.stream().flatMap(postReaction -> Stream.of(postReaction.getUser())).collect(Collectors.toList()));
                    recyclerViewSearchHistory.setAdapter(ProfileItemAdapter);

//                    LinearLayout linearLayout = new LinearLayout(context);
//                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//                    linearLayout.addView(recyclerViewSearchHistory);

//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//                    builder.setTitle("People who reacted")
//                            .setView(linearLayout);
//
//                    final AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//
//                    Rect displayRectangle = new Rect();
//                    Window window = ((Activity)context).getWindow();
//
//                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//                    alertDialog.getWindow().setLayout((int)(displayRectangle.width()), (int)(displayRectangle.height() * 0.9f));

                    linearLayoutParent.addView(recyclerViewSearchHistory);
                } else {
                    // comments


                    if (linearLayoutParent.getChildCount() > 1) {
                        linearLayoutParent.removeViewAt(1);
                    }


                    View viewComment = LayoutInflater.from(context).inflate(R.layout.custom_post_comment, null, false);

                    ImageButton btnSend = viewComment.findViewById(R.id.btnSend);
                    btnSend.setEnabled(false);
                    TextInputEditText editTextComment = viewComment.findViewById(R.id.editTextComment);

                    RecyclerView recyclerViewComments = viewComment.findViewById(R.id.recyclerViewComments);
                    recyclerViewComments.setLayoutManager(new LinearLayoutManager(context));

                    CommentAdapter commentAdapter = new CommentAdapter(context);
                    recyclerViewComments.setAdapter(commentAdapter);

                    if (!postCommentSet.isEmpty()) {
                        commentAdapter.setPostCommentList(postCommentSet.stream().collect(Collectors.toList()));
                    }

                    btnSend.setOnClickListener(view1 -> {
                        String content = editTextComment.getText().toString().trim();

                        if (content != null && !content.isEmpty()) {
                            PostComment postComment = new PostComment(content, MyApp.getInstance().getCurrentUser(), post);
                            if (postCommentDataSource.create(postComment)) {
                                recyclerViewComments.scrollToPosition(0);
                                Log.d("PostAdapter", "create comment: success");
                                editTextComment.setText("");
                                commentAdapter.addPostComment(postComment);
                            } else { Log.d("PostAdapter", "create comment: failed"); }
                        }


                    });


                    editTextComment.setOnFocusChangeListener((view, b) -> {
                        if (b) {
                            tabLayout.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    });

                    editTextComment.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            String content = editTextComment.getText().toString().trim();
                            if (content == null || content.isEmpty()) {
                                btnSend.setEnabled(false);
                                btnSend.setImageDrawable(context.getDrawable(R.drawable.baseline_send_not_active24));
                            } else {
                                btnSend.setEnabled(true);
                                btnSend.setImageDrawable(context.getDrawable(R.drawable.baseline_send_active_24));
                            }
                        }
                    });

//                    LinearLayout linearLayout = new LinearLayout(context);
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                    params.setMargins(10, 0, 0, 10);
//                    linearLayout.addView(v, params);

//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//                    builder.setTitle("Comments")
//                            .setView(linearLayout);
//
//                    final AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//
//                    Rect displayRectangle = new Rect();
//                    Window window = ((Activity)context).getWindow();
//
//                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//                    alertDialog.getWindow().setLayout((int)(displayRectangle.width()), (int)(displayRectangle.height() * 0.9f));

                    linearLayoutParent.addView(viewComment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        linearLayoutParent.addView(v, params);

        tabLayout.setView(linearLayoutParent);

        if (selected == 0) {
            tab.getTabAt(1).select();
            tab.getTabAt(0).select();
        } else if (selected == 1){
            tab.getTabAt(1).select();
        }

        AlertDialog alertDialog = tabLayout.create();
        alertDialog.show();

        Rect displayRectangle = new Rect();
        Window window = alertDialog.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;

        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        window.setAttributes(wlp);

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        alertDialog.getWindow().setLayout((int) (displayRectangle.width()), (int) (displayRectangle.height()));


    }

    @Override
    public int getItemCount() {
        return this.postList.size();
    }


    @Override
    public void setPostImages(List<PostImages> postImages) {
        super.setPostImages(postImages);
    }

    @Override
    public void setSnapHelper(PagerSnapHelper snapHelper) {
        super.setSnapHelper(snapHelper);
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }


    public ActivityResultLauncher<Intent> getGalleryActivityResultLauncher() {
        return galleryActivityResultLauncher;
    }

    public void setGalleryActivityResultLauncher(ActivityResultLauncher<Intent> galleryActivityResultLauncher) {
        this.galleryActivityResultLauncher = galleryActivityResultLauncher;
    }

    public List<PostImages> getInsertPostImagesList() {
        return insertPostImagesList;
    }

    public void setInsertPostImagesList(List<PostImages> insertPostImagesList) {
        this.insertPostImagesList = insertPostImagesList;
    }

    public Boolean getEnableAction() {
        return isEnableAction;
    }

    public void setEnableAction(Boolean enableAction) {
        isEnableAction = enableAction;
    }


    public interface OnChangePostListener {
        void onChange(boolean flag);
    }

    public ImageSliderEditAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ImageSliderEditAdapter adapter) {
        this.adapter = adapter;
    }

    public Post getCurrentPost() {
        return currentPost;
    }

    public void setCurrentPost(Post currentPost) {
        this.currentPost = currentPost;
    }
}

