package com.example.h2ak.Firebase.FirebaseDataSource;

import com.example.h2ak.model.Post;
import com.example.h2ak.model.PostImages;

public interface FirebasePostImagesDataSource {
    boolean create(PostImages postImages);
    boolean delete(PostImages postImages);
}
