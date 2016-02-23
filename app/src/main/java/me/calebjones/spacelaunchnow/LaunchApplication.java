package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;

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
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
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

            if (sharedPreference.getLastVehicleUpdate() > 0) {
                Timber.d("Time since last VehicleUpdate: %s", (System.currentTimeMillis() - sharedPreference.getLastVehicleUpdate()));
                if ((System.currentTimeMillis() - sharedPreference.getLastVehicleUpdate()) > 604800000) {
                    Intent rocketIntent = new Intent(this, VehicleDataService.class);
                    rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
                    this.startService(rocketIntent);
                }
                //Needed for users that will be upgrading
            } else {
                Intent rocketIntent = new Intent(this, VehicleDataService.class);
                rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
                this.startService(rocketIntent);
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
