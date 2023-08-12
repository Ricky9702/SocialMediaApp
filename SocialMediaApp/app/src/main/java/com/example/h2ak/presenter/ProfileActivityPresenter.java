package com.example.h2ak.presenter;

import android.content.Context;

import com.example.h2ak.MyApp;
import com.example.h2ak.SQLite.SQLiteDataSource.PostDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.PostDataSourceImpl;
import com.example.h2ak.contract.ProfileActivityContract;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.User;
import com.example.h2ak.utils.TextInputLayoutUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProfileActivityPresenter implements ProfileActivityContract.Presenter {

    private ProfileActivityContract.View view;
    private UserDataSource userDataSource;
    private FirebaseAuth firebaseAuth;
    private PostDataSource postDataSource;
    Context context;

    public ProfileActivityPresenter(Context context, ProfileActivityContract.View view) {
        this.view = view;
        userDataSource = UserDataSourceImpl.getInstance(context);
        firebaseAuth = FirebaseAuth.getInstance();
        this.context = context;
        postDataSource = PostDataSourceImpl.getInstance(context);
    }


    @Override
    public void getUser() {
            User user = userDataSource.getUserById(MyApp.getInstance().getCurrentUserId());
            view.loadUserInformation(user);
    }

    @Override
    public void getAllPost() {
        List<Post> postList = new ArrayList<>();
        postList.addAll(postDataSource.getAllPostByUserId(MyApp.getInstance().getCurrentUserId()));
        Collections.sort(postList, (post1, post2) -> {
            Date date1 = TextInputLayoutUtils.parseDateFromString(post1.getCreatedDate());
            Date date2 = TextInputLayoutUtils.parseDateFromString(post2.getCreatedDate());

            // Sorting in descending order (latest items first)
            return date2.compareTo(date1);
        });
        view.onPostListRecieved(postList);
    }
}
