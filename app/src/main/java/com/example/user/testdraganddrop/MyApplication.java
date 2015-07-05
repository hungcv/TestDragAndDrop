package com.example.user.testdraganddrop;

import android.app.Application;

/**
 * Created by User on 7/4/2015.
 */
public class MyApplication extends Application {

    private static MyApplication singleton;

    public static MyApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
