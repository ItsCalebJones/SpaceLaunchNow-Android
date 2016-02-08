package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import io.fabric.sdk.android.Fabric;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
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

        checkFirstBoot();
    }

    public void checkFirstBoot() {
        if (sharedPreference.getFirstBoot()) {
            //TODO check last sync
            refreshLaunches();
        } else {
            sharedPreference.syncFavorites();
        }
    }

    private void refreshLaunches() {
        Intent update_upcoming_launches = new Intent(this, LaunchDataService.class);
        update_upcoming_launches.setAction(Strings.ACTION_GET_UP_LAUNCHES);
        this.startService(update_upcoming_launches);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
