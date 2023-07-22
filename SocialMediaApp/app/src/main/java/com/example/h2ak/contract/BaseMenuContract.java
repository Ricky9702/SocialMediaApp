package com.example.h2ak.contract;

import android.content.Context;

import com.example.h2ak.model.User;

public interface BaseMenuContract {

    interface View {
        void changeProfileAvatar(User user);
    }

    interface Presenter {
        void getUser();
    }
}
