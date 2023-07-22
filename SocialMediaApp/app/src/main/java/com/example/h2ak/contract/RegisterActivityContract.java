package com.example.h2ak.contract;

import com.example.h2ak.model.User;

public interface RegisterActivityContract {

    interface View {
        void showProgressBar(boolean flag);
        void showError(String errorMessage);
        void onRegisterSuccess();
    }

    interface Presenter {
        User createUser(String name, String birthday, String gender, String email, String password, String confirmPassword);
        void register(User user);
        void addNonverifiedUser(User user);
    }
}
