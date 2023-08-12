package com.example.h2ak.view.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.h2ak.R;
import com.example.h2ak.adapter.CreatePostChooseImageGridAdapter;
import com.example.h2ak.adapter.ImageSliderAdapter;
import com.example.h2ak.contract.CreatePostChooseImageFragmentContract;
import com.example.h2ak.presenter.CreatePostChooseImageFragmentPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator2;

public class CreatePostChooseImageFragment extends Fragment implements CreatePostChooseImageFragmentContract.View{

    private CreatePostChooseImageFragmentContract.Presenter presenter;
    CreatePostChooseImageGridAdapter adapter;
    RecyclerView recyclerViewImageSlider;
    ImageSliderAdapter imageSliderAdapter;
    GridView gridView;
    ImageButton btnMultipleImages;
    TextView textViewImageSliderPlaceHolder;
    Button btnNext;
    CircleIndicator2 indicator2;
    private boolean allowMultipleImages = false;
    private View view;
    private List<String> listImageUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_post_choose_image, container, false);

        btnMultipleImages = view.findViewById(R.id.btnMultipleImages);

        recyclerViewImageSlider = view.findViewById(R.id.recyclerViewImageSlider);
        recyclerViewImageSlider.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        imageSliderAdapter = new ImageSliderAdapter(this.getContext());
        recyclerViewImageSlider.setAdapter(imageSliderAdapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewImageSlider);

        indicator2 = view.findViewById(R.id.indicator);
        indicator2.attachToRecyclerView(recyclerViewImageSlider, snapHelper);

        imageSliderAdapter.registerAdapterDataObserver(indicator2.getAdapterDataObserver());

        textViewImageSliderPlaceHolder = view.findViewById(R.id.textViewImageSliderPlaceHolder);

        btnMultipleImages.setOnClickListener(view1 -> {

            allowMultipleImages = !allowMultipleImages;
            adapter.setAllowMultipleImages(allowMultipleImages);
            imageSliderAdapter.setListSelectedImageUrl(null);
            textViewImageSliderPlaceHolder.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);

            if (allowMultipleImages) {
                btnMultipleImages.setImageResource(R.drawable.multiple_images_active_com);
                Toast.makeText(this.getContext(), "Allow multiple images enabled", Toast.LENGTH_SHORT).show();
            } else {
                btnMultipleImages.setImageResource(R.drawable.multiple_images_not_active_com);
                Toast.makeText(this.getContext(), "Allow multiple images disabled", Toast.LENGTH_SHORT).show();
            }

        });

        btnNext = this.getActivity().findViewById(R.id.btnNext);
        btnNext.setText("Next");
        btnNext.setEnabled(false);

        Toolbar toolbar = this.getActivity().findViewById(R.id.toolBar);
        toolbar.setNavigationIcon(R.drawable.baseline_close_24);
        toolbar.setTitle("Create new post");
        toolbar.setNavigationOnClickListener(view1 -> {
            this.getActivity().finish();
        });

        btnNext.setOnClickListener(view1 -> {
            // Create a Bundle to hold your data
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("imageUrls", (ArrayList<String>) this.listImageUrl);

            // Create the destination fragment instance
            CreatePostChooseContentFragment fragment = new CreatePostChooseContentFragment();
            fragment.setArguments(bundle);

            // Replace the current fragment with the destination fragment
            FragmentTransaction transaction = this.getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutCreatePost, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new CreatePostChooseImageGridAdapter(this.getContext(), this);
        gridView = view.findViewById(R.id.gridViewRecentImages);
        gridView.setAdapter(adapter);

        presenter = new CreatePostChooseImageFragmentPresenter(this, this.getContext());

        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 999);
        } else {
            // Permission is already granted
            // Load the image
            presenter.loadRecentImagesFromDevice();
        }
    }


    @Override
    public void onMapImageSelected(Map<Integer, CreatePostChooseImageGridAdapter.ImageSelectionInfo> listImageUrl) {
        Log.d("listImageUrl: ", listImageUrl.size() + "");
        List<String> listUrl = new ArrayList<>();
        if (!listImageUrl.isEmpty()) {
            for (int key : listImageUrl.keySet()) {
                Log.d("onMapImageSelected", listImageUrl.get(key).getImageUrl());
                listUrl.add(listImageUrl.get(key).getImageUrl());
            }

            if (listImageUrl.size() == 1) {
                indicator2.setVisibility(View.GONE);
            } else {
                indicator2.setVisibility(View.VISIBLE);
            }

            btnNext.setEnabled(true);
            textViewImageSliderPlaceHolder.setVisibility(View.GONE);
        } else {
            btnNext.setEnabled(false);
            textViewImageSliderPlaceHolder.setVisibility(View.VISIBLE);
        }
        imageSliderAdapter.setListSelectedImageUrl(listUrl);
        this.setListImageUrl(listUrl);
    }

    @Override
    public void onRecievedRecentImages(List<String> listImageUrl) {
        adapter.setListImageUrl(listImageUrl);
    }

    public List<String> getListImageUrl() {
        return listImageUrl;
    }

    public void setListImageUrl(List<String> imageUrl) {
        this.listImageUrl = imageUrl;
    }
}