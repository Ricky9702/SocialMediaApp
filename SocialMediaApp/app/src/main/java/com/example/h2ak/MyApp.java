package com.example.h2ak;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.example.h2ak.view.fragments.FriendFragment;
import com.example.h2ak.view.fragments.InboxFragment;
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
    private static final long OFFLINE_DELAY = 120000; // 2 minutes
    private Handler offlineHandler = new Handler();

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
        if (activity instanceof BaseMenuActivity) {
            userDataSource = UserDataSourceImpl.getInstance(this);
            firebaseUserDataSource = FirebaseUserDataSourceImpl.getInstance();
            firebaseDataSync = FirebaseDataSync.getInstance(this);

            firebaseDataSync.syncUser(new FirebaseDataSync.OnDataChangeListener() {
                @Override
                public void onDataChange() {
                    if (currentActivity instanceof BaseMenuActivity) {

                        // if current fragment is friend list
                        Fragment fragment = ((BaseMenuActivity) currentActivity).getSupportFragmentManager().findFragmentById(R.id.frameLayout);

                        // update current friend list ui
                        if (fragment instanceof FriendFragment) {
                            ((FriendFragment) fragment).getPresenter().getFriendList();
                        }
                    }
                }
            });

            firebaseDataSync.syncFriendShip(new FirebaseDataSync.OnDataChangeListener() {
                @Override
                public void onDataChange() {
                    // update current friend fragment ui
                    if (currentActivity instanceof BaseMenuActivity) {

                        // if current fragment is friend list
                        Fragment fragment = ((BaseMenuActivity) currentActivity).getSupportFragmentManager().findFragmentById(R.id.frameLayout);

                        // update current friend list ui
                        if (fragment instanceof FriendFragment) {
                            ((FriendFragment) fragment).getPresenter().getFriendList();
                        }
                    }
                }
            });

            firebaseDataSync.syncInbox(new FirebaseDataSync.OnDataChangeListener() {
                @Override
                public void onDataChange() {
                    if (currentActivity instanceof BaseMenuActivity) {
                        ((BaseMenuActivity) currentActivity).getPresenter().loadingListInboxUnRead();

                        // if current fragment is inbox
                        Fragment fragment = ((BaseMenuActivity) currentActivity).getSupportFragmentManager().findFragmentById(R.id.frameLayout);

                        // update current inbox ui
                        if (fragment instanceof InboxFragment) {
                            ((InboxFragment) fragment).getPresenter().getListInboxes();
                        }
                    }
                }
            });

            this.currentUserId = this.getCurrentUserId();
            this.currentUser = userDataSource.getUserById(currentUserId);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.setCurrentActivity(activity);
        if (!(this.getCurrentActivity() instanceof LoginActivity) && !(this.getCurrentActivity() instanceof RegisterActivity)) {
            isAppInForeground = true;
            if (getCurrentUser() != null) {
                offlineHandler.removeCallbacksAndMessages(null);
                getCurrentUser().setOnline(true);
                firebaseUserDataSource.updateOnlineField(getCurrentUser());
                Log.d("FirebaseUpdateUser", "update resumed");
            }
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        isAppInForeground = false;
        // Use a Handler to delay setting the user offline by 2 minutes
        offlineHandler.postDelayed(() -> {
            if (!isAppInForeground) {
                User currentUser = getCurrentUser();
                if (currentUser != null) {
                    currentUser.setOnline(false);
                    firebaseUserDataSource.updateOnlineField(currentUser);
                    Log.d("FirebaseUpdateUser", "User is offline now");
                }
            }
        }, OFFLINE_DELAY);
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
//            if(((ProfileActivity) activity).getButtonLogout().isPressed()) {
//                getCurrentUser().setOnline(false);
//                firebaseUserDataSource.updateOnlineField(getCurrentUser());
//            }
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public User getCurrentUser() {
        if (currentUser != null) {
            Log.d("currentUser", currentUser.getName());
        }
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) return FirebaseAuth.getInstance().getCurrentUser().getUid();
        else return null;
    }

    public void setUserOffline() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            currentUser.setOnline(false);
            firebaseUserDataSource.updateOnlineField(currentUser);
            Log.d("FirebaseUpdateUser", "User is offline now");
        }
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}

