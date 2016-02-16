package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.MissionDataService;
import me.calebjones.spacelaunchnow.content.services.VehicleDataService;
import timber.log.Timber;


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
        Fabric.with(this, new Crashlytics());
        LeakCanary.install(this);
        mInstance = this;

        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(getCacheDir(), cacheSize);
        client.setCache(cache);

        SharedPreference.create(this);

        sharedPreference = SharedPreference.getInstance(this);
        sharedPreference.setNightModeStatus(false);

        if (!sharedPreference.getFirstBoot()) {
            Intent nextIntent = new Intent(this, LaunchDataService.class);
            nextIntent.setAction(Strings.ACTION_UPDATE_NEXT_LAUNCH);
            this.startService(nextIntent);
        }


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
