package me.calebjones.spacelaunchnow.wear;

import android.app.Application;

import timber.log.Timber;


public class WearApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
