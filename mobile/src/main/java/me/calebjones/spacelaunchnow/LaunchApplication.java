package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.crashlytics.android.Crashlytics;
import com.karumi.dexter.Dexter;
import com.onesignal.OneSignal;
import com.squareup.leakcanary.LeakCanary;

import net.mediavrog.irr.DefaultRuleEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.TimeZone;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.MissionDataService;
import me.calebjones.spacelaunchnow.content.services.VehicleDataService;
import me.calebjones.spacelaunchnow.utils.Utils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import timber.log.Timber;


public class LaunchApplication extends Application {

    private static LaunchApplication mInstance;
    public static final String TAG = "Space Launch Now";

    public OkHttpClient client;
    private static ListPreferences sharedPreference;
    private SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;

    public static synchronized LaunchApplication getInstance() {
        return mInstance;
    }

    @NonNull
    public static LaunchApplication get(@NonNull Context anyContext) {
        return (LaunchApplication) anyContext.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //Init Crashlytics and gather device information.
        Fabric.with(this, new Crashlytics());
        Crashlytics.setString("Timezone", String.valueOf(TimeZone.getDefault().getDisplayName()));
        Crashlytics.setString("Language", Locale.getDefault().getDisplayLanguage());
        Crashlytics.setBool("is24", DateFormat.is24HourFormat(getApplicationContext()));
        Crashlytics.setBool("Network", Utils.isNetworkAvailable(this));

        LeakCanary.install(this);
        OneSignal.startInit(this).init();
        OneSignal.enableNotificationsWhenActive(true);
        OneSignal.enableInAppAlertNotification(true);

        Dexter.initialize(this);

        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        // Get a Realm instance for this thread
        Realm.setDefaultConfiguration(realmConfig);


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.ERROR);

            JSONObject tags = new JSONObject();
            try {
                tags.put("DEBUG", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OneSignal.sendTags(tags);
        } else {
            JSONObject tags = new JSONObject();
            try {
                tags.put("DEBUG", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OneSignal.sendTags(tags);
        }

        mInstance = this;

        ListPreferences.create(this);

        sharedPreference = ListPreferences.getInstance(this);
        switchPreferences = SwitchPreferences.getInstance(this);

        checkSubscriptions();

        DefaultRuleEngine.trackAppStart(this);

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

                } else if (Utils.getVersionCode(this) != switchPreferences.getVersionCode()) {

                    Intent rocketIntent = new Intent(this, VehicleDataService.class);
                    rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
                    this.startService(rocketIntent);
                }
            }
        }
    }

    private void checkSubscriptions() {
        if (sharedPref.getBoolean("notifications_launch_imminent_updates", true)) {
            OneSignal.setSubscription(true);
            JSONObject tags = new JSONObject();
            if (switchPreferences.getSwitchNasa()) {
                try {
                    tags.put("Nasa", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getSwitchISRO()) {
                try {
                    tags.put("ISRO", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getSwitchRoscosmos()) {
                try {
                    tags.put("ROSCOSMOS", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getSwitchULA()) {
                try {
                    tags.put("ULA", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getSwitchArianespace()) {
                try {
                    tags.put("Arianespace", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getSwitchKSC()) {
                try {
                    tags.put("KSC", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getSwitchPles()) {
                try {
                    tags.put("Ples", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getSwitchVan()) {
                try {
                    tags.put("Van", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getSwitchSpaceX()) {
                try {
                    tags.put("SpaceX", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (switchPreferences.getAllSwitch()) {
                try {
                    tags.put("all", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            OneSignal.sendTags(tags);
        } else {
            OneSignal.setSubscription(false);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
