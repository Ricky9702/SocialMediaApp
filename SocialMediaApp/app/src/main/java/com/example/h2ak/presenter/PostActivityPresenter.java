package com.example.h2ak.presenter;

import android.content.Context;
import android.util.Log;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.PostImagesDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostImagesDataSourceImpl;
import com.example.h2ak.contract.PostActivityContract;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.utils.TextInputLayoutUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PostActivityPresenter implements PostActivityContract.Presenter {
    private PostActivityContract.View view;
    private Context context;
    private PostDataSource postDataSource;
    private PostImagesDataSource postImagesDataSource;

    public PostActivityPresenter(Context context, PostActivityContract.View view) {
        this.view = view;
        this.context = context;
        postDataSource = PostDataSourceImpl.getInstance(context);
        postImagesDataSource = PostImagesDataSourceImpl.getInstance(context);
    }

    public void getAllPostByUserIdWithPrivacy(String id, String privacy1, String privacy2) {
        List<Post> postList = new ArrayList<>();

        // current user
        if (id == null || id.isEmpty()) {
            id = MyApp.getInstance().getCurrentUserId();
            postList.addAll(postDataSource.getAllPostByUserId(id));
        } else {
            // client id

            // the user is stranger
            if (privacy2 == null || privacy2.isEmpty()) {
                postList.addAll(postDataSource.getAllPostByUserIdWithPrivacy(id, privacy1, null));
            } else {
                // the user is friends
                postList.addAll(postDataSource.getAllPostByUserIdWithPrivacy(id, privacy1, privacy2));
            }
        }

        Collections.sort(postList, (post1, post2) -> {
            Date date1 = parseDateFromString(post1.getCreatedDate());
            Date date2 = parseDateFromString(post2.getCreatedDate());

            // Sorting in descending order (latest items first)
            return date2.compareTo(date1);
        });

        view.onListPostRecieved(postList);
    }

    private Date parseDateFromString(String dateString) {
        try {
            return TextInputLayoutUtils.simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Return a default date in case of parsing error
        }
    }
}

