package com.repos.src.controllers;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ayman Mahgoub on 2/20/16.
 */
public class ApplicationController extends Application {

    private static ApplicationController instance;

    public static ApplicationController getInstance() {
        return instance;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // any generic initialization code
        instance = this;
        RealmController.init(this);
    }
}
