package me.calebjones.spacelaunchnow;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import me.calebjones.spacelaunchnow.content.database.SharedPreference;


public class LaunchApplication extends Application {

    private static LaunchApplication mInstance;
    public static final String TAG = "Space Launch Now";

    public OkHttpClient client = new OkHttpClient();
    private static SharedPreference sharedPreference;

    public static synchronized LaunchApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        int cacheSize = 50 * 1024 * 1024;
        Cache cache = new Cache(getCacheDir(), cacheSize);
        client.setCache(cache);

        SharedPreference.create(this);

        sharedPreference = SharedPreference.getInstance(this);
        sharedPreference.setNightModeStatus(false);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
