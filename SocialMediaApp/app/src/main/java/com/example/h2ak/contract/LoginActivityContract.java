package com.example.h2ak.contract;

import com.example.h2ak.presenter.LoginActivtyPresenter;

public interface LoginActivityContract {
    interface View {
        void showProgressbar(boolean flag);
        void onLoginSuccess();
        void onLoginFailure(String errorMessage);
    }

    interface Presenter {
        void login(String email, String password);
        void updateUserActive(String email, boolean isActive);
    }
}
