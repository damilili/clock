package com.hoody.clockapplication.app;

import android.content.Context;
import android.util.Log;

public class Application extends android.app.Application {
    private static final String TAG = "Application";
    public static Context Instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Instance = null;
        Log.d(TAG, "onTerminate() called");
    }
}
