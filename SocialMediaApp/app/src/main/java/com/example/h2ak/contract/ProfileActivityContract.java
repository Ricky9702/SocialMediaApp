package com.example.h2ak.contract;

import com.example.h2ak.model.User;

public interface ProfileActivityContract {

    interface View {
        void loadUserInformation(User user);
    }

    interface Presenter {
        void getUser();
    }
}
