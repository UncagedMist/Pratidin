package com.bihar.pratidin.Common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentSender;

import com.bihar.pratidin.AdsManager.AppOpenManager;
import com.bihar.pratidin.SplashActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

public class MyApplication extends Application {

    private static MyApplication instance;

    private boolean showAds = true;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

//        AudienceNetworkAds.initialize(this);

        if (showAds)    {
            AppOpenManager appOpenManager = new AppOpenManager(instance);
        }

    }


    public static MyApplication getInstance() {
        return instance;
    }

    public boolean isShowAds() {
        return showAds;
    }

    public void setShowAds(boolean showAds) {
        this.showAds = showAds;
    }
}

