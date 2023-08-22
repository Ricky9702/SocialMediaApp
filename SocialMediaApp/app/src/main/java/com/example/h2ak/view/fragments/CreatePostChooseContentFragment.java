package com.example.h2ak.view.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.h2ak.MyApp;
import com.example.h2ak.R;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.adapter.PostPreviewAdapter;
import com.example.h2ak.adapter.SpinnerGenderAdapter;
import com.example.h2ak.contract.CreatePostChooseContentFragmentContract;
import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;
import com.example.h2ak.presenter.CreatePostChooseContentFragmentPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CreatePostChooseContentFragment extends Fragment implements CreatePostChooseContentFragmentContract.View {
    FrameLayout frameLayoutProgressbar;
    RecyclerView recyclerViewPreviewPost;
    PostPreviewAdapter postAdapter;
    FloatingActionButton floatingButtonEdit;
    private Post post;
    private CreatePostChooseContentFragmentPresenter presenter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_post_choose_content, container, false);

        frameLayoutProgressbar = view.findViewById(R.id.frameLayoutProgressbar);

        recyclerViewPreviewPost = view.findViewById(R.id.recyclerViewPreviewPost);
        postAdapter = new PostPreviewAdapter(this.getContext());
        recyclerViewPreviewPost.setLayoutManager(new LinearLayoutManager(this.getContext()));


        post = new Post("", MyApp.getInstance().getCurrentUser(), Post.PostPrivacy.PUBLIC);
        postAdapter.setPost(post, true);

        ArrayList<String> imageUrls = getArguments().getStringArrayList("imageUrls");

        List<PostImages> postImagesList = new ArrayList<>();
        int count = 0;
        for (String url : imageUrls) {

            PostImages postImages = new PostImages(url, post);

            String newCreatedDate = postImages.getCreatedDate() + "_" + count;

            postImages.setCreatedDate(newCreatedDate);

            postImagesList.add(postImages);

            ++count;
        }

        postAdapter.setPostImages(postImagesList);

        // Edit toolbar
        Toolbar toolbar = this.getActivity().findViewById(R.id.toolBar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setTitle("Preview Post");
        toolbar.setNavigationOnClickListener(view1 -> {
            postAdapter.setSnapHelper(null);
            getParentFragmentManager().popBackStack();
        });

        presenter = new CreatePostChooseContentFragmentPresenter(this.getContext(), this);

        Button btnNext = this.getActivity().findViewById(R.id.btnNext);
        btnNext.setText("Publish");
        btnNext.setOnClickListener(view1 -> {
                presenter.createPost(post, postImagesList);
        });

        recyclerViewPreviewPost.setAdapter(postAdapter);

        floatingButtonEdit = view.findViewById(R.id.floatingButtonEdit);

        floatingButtonEdit.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

            String[] items = new String[]{"Edit content", "Edit privacy"};

            builder.setItems(items, (dialogInterface, i) -> {
                String selectedItem = items[i];
                if (selectedItem.equals("Edit content")) {
                    LinearLayout linearLayout = new LinearLayout(this.getContext());

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(25, 0 , 25, 0);

                    EditText content = new EditText(this.getContext());
                    content.setHint("Write your new content.");
                    content.setText(post.getContent());

                    linearLayout.addView(content, params);

                    AlertDialog.Builder b = new AlertDialog.Builder(this.getContext());
                    b.setTitle("Edit content")
                            .setView(linearLayout)
                            .setPositiveButton("Change", (d, pos) -> {
                                post.setContent(content.getText().toString().trim());
                                post.setCreatedDate(new Post().getCreatedDate());
                                postAdapter.setPost(post,true);
                            })
                            .setNegativeButton("Cancel", (d, pos) -> dialogInterface.dismiss())
                            .create().show();
                } else {

                    LinearLayout linearLayout = new LinearLayout(this.getContext());

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(25, 0 , 25, 0);

                    //Create adapter spinner
                    String[] options = new String[]{"PUBLIC", "FRIENDS", "ONLY_ME"};
                    SpinnerGenderAdapter adapter = new SpinnerGenderAdapter(this.getContext(), options);

                    //Create spinner
                    Spinner spinner = new Spinner(this.getContext());
                    spinner.setAdapter(adapter);

                    int selectedIndex = Arrays.asList(options).indexOf(post.getPrivacy());

                    spinner.setSelection(selectedIndex);

                    linearLayout.addView(spinner, params);

                    AlertDialog.Builder b = new AlertDialog.Builder(this.getContext());
                    b.setTitle("Edit privacy")
                            .setView(linearLayout)
                            .setPositiveButton("Change", (d, pos) -> {
                                post.setPrivacy(spinner.getSelectedItem().toString());
                                post.setCreatedDate(new Post().getCreatedDate());
                                postAdapter.setPost(post,true);
                            })
                            .setNegativeButton("Cancel", (d, pos) -> dialogInterface.dismiss())
                            .create().show();
                }
            }).setTitle("Edit post")
                    .create()
                    .show();

        });

        return view;
    }

    @Override
    public void showProgressbar(boolean flag) {
        if (flag) frameLayoutProgressbar.setVisibility(View.VISIBLE);
        else {
            frameLayoutProgressbar.setVisibility(View.GONE);
            this.getActivity().finish();
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
