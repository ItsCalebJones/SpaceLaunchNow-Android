package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;

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
        mInstance = this;

        int cacheSize = 10 * 1024 * 1024;
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
            if (sharedPreference.getVehicles() == null || sharedPreference.getVehicles().size() == 0){
                Intent rocketIntent = new Intent(this, VehicleDataService.class);
                rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
                this.startService(rocketIntent);

            }
            if (sharedPreference.getLaunchesUpcoming() == null || sharedPreference.getLaunchesUpcoming().size() == 0){
                Intent launchUpIntent = new Intent(this, LaunchDataService.class);
                launchUpIntent.setAction(Strings.ACTION_GET_UP_LAUNCHES);
                this.startService(launchUpIntent);
            }
            if (sharedPreference.getLaunchesPrevious() == null || sharedPreference.getLaunchesPrevious().size() == 0){
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());

                String url = "https://launchlibrary.net/1.1/launch/1950-01-01/" + String.valueOf(formattedDate) + "?sort=desc&limit=1000";

                Intent launchPrevIntent = new Intent(this, LaunchDataService.class);
                launchPrevIntent.putExtra("URL", url);
                launchPrevIntent.setAction(Strings.ACTION_GET_PREV_LAUNCHES);
                this.startService(launchPrevIntent);
            }
            if (sharedPreference.getMissionList() == null || sharedPreference.getMissionList().size() == 0){
                this.startService(new Intent(this, MissionDataService.class));
            }
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
