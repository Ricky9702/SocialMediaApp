package com.example.h2ak.presenter;

import android.content.Context;
import android.net.Uri;

import com.example.h2ak.Firebase.FirebaseHelper;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostImagesDataSourceImpl;
import com.example.h2ak.contract.CreatePostChooseContentFragmentContract;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;

import java.io.File;
import java.util.List;

public class CreatePostChooseContentFragmentPresenter implements CreatePostChooseContentFragmentContract.Presenter{

    private PostDataSource postDataSource;
    private PostImagesDataSource postImagesDataSource;
    private CreatePostChooseContentFragmentContract.View view;
    private Context context;

    public CreatePostChooseContentFragmentPresenter(Context context, CreatePostChooseContentFragmentContract.View view) {
        this.context = context;
        this.view = view;
        postDataSource = PostDataSourceImpl.getInstance(context);
        postImagesDataSource = PostImagesDataSourceImpl.getInstance(context);
    }

    @Override
    public void createPost(Post post, List<PostImages> postImagesList) {
        view.showProgressbar(true);
        if(postDataSource.createPost(post)) {
            postImagesList.forEach(postImages -> {
                FirebaseHelper.uploadImageToFirebaseCloud(context, Uri.fromFile(new File(postImages.getImageUrl()))
                        , FirebaseHelper.getImagePostStorageRef(), new FirebaseHelper.OnImageUploadListener() {
                            @Override
                            public void onImageUploadSuccess(String imageUrl) {
                                postImages.setImageUrl(imageUrl);
                                postImagesDataSource.createPostImages(postImages);
                            }

                            @Override
                            public void onImageUploadFailure(String errorMessage) {

                            }
                        });
            });
            view.showProgressbar(false);
            view.showMessage("Create post successfully!!");
        } else {
            view.showProgressbar(false);
            view.showMessage("Failed to create post!!");
        }
    }
}
