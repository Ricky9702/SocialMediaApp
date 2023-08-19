package com.example.h2ak.contract;

public interface LoginActivityContract {
    interface View {
        void showProgressbar(boolean flag);
        void onLoginSuccess();
        void onLoginFailure(String errorMessage);

        void onAdminLogin();
    }

    interface Presenter {
        void login(String email, String password, EmailVerifyingListener listener);
        void updateUserWithEmailVerified(boolean isActive);
    }

     interface EmailVerifyingListener {
        void onEmailVerified(boolean isVerified);
    }
}
