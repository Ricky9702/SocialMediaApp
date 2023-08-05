package com.example.h2ak.contract;

import com.example.h2ak.model.User;

public interface HomeFragmentContract {

    interface View {
        void changeProfileAvatar(User user);
    }

    interface Presenter {
        void loadCurrentUser();
    }
}
