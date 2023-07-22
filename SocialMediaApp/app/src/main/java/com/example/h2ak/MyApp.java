package com.example.h2ak;

import android.app.Application;

public class MyApp extends Application {
    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized MyApp getInstance() {
        return instance;
    }

}
