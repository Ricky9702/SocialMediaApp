package com.example.h2ak;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseDataSourceImpl.FirebaseUserDataSourceImpl;
import com.example.h2ak.Firebase.FirebaseDataSync;
import com.example.h2ak.Firebase.FirebaseDataSource.FirebaseUserDataSource;
import com.example.h2ak.SQLite.SQLiteDataSource.SQLiteDataSourceImpl.UserDataSourceImpl;
import com.example.h2ak.SQLite.SQLiteDataSource.UserDataSource;
import com.example.h2ak.model.User;
import com.example.h2ak.view.activities.BaseMenuActivity;
import com.example.h2ak.view.activities.LoginActivity;
import com.example.h2ak.view.activities.ProfileActivity;
import com.example.h2ak.view.activities.RegisterActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {
    private static MyApp instance;
    private FirebaseUserDataSource firebaseUserDataSource;
    private User currentUser;
    private boolean isAppInForeground = false;
    private UserDataSource userDataSource;
    private Activity currentActivity;
    private FirebaseDataSync firebaseDataSync;
    private String currentUserId;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        FirebaseApp.initializeApp(this);
        registerActivityLifecycleCallbacks(this);
    }

    public static synchronized MyApp getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (activity instanceof BaseMenuActivity) {
            this.currentUserId = this.getCurrentUserId();

            userDataSource = UserDataSourceImpl.getInstance(this);
            firebaseDataSync = FirebaseDataSync.getInstance(this);
            firebaseUserDataSource = FirebaseUserDataSourceImpl.getInstance();
            firebaseDataSync = FirebaseDataSync.getInstance(this);
            firebaseDataSync.syncUser();
            firebaseDataSync.syncFriendShip();
            firebaseDataSync.syncInbox();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.setCurrentActivity(activity);
        if (!(this.getCurrentActivity() instanceof LoginActivity) && !(this.getCurrentActivity() instanceof RegisterActivity)) {
            isAppInForeground = true;
            if (getCurrentUser() != null) {
                getCurrentUser().setOnline(true);
                firebaseUserDataSource.updateOnlineField(getCurrentUser());
                Log.d("FirebaseUpdateUser", "update resumed");
            }
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        isAppInForeground = false;
        if (getCurrentUser() != null && !isAppInForeground) {
            getCurrentUser().setOnline(false);
            firebaseUserDataSource.updateOnlineField(getCurrentUser());
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity instanceof ProfileActivity) {
            if(((ProfileActivity) activity).getButtonLogout().isPressed()) {
                getCurrentUser().setOnline(false);
                firebaseUserDataSource.updateOnlineField(getCurrentUser());
            }
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}

