package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.onesignal.OneSignal;

import java.util.Locale;
import java.util.TimeZone;

import net.mediavrog.irr.DefaultRuleEngine;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.DataJobCreator;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.LibraryDataService;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchDataModule;
import me.calebjones.spacelaunchnow.data.models.realm.Migration;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.utils.Analytics;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import okhttp3.OkHttpClient;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.data.models.Constants.DB_SCHEMA_VERSION;

public class LaunchApplication extends Application implements Analytics.Provider {

    public static final String TAG = "Space Launch Now";
    public OkHttpClient client;
    private static LaunchApplication mInstance;
    private static ListPreferences sharedPreference;
    private SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;
    protected volatile Analytics mAnalytics;

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
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        * Init Crashlytics and gather additional device information.
        * Always leave this at the top so it catches any init failures.
        * Version 1.3.0-Beta had a bug where starting a service crashed before Crashlytics picked it up.
        */
        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                              .disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);

        // Initialize Fabric with the debug-disabled crashlytics.
        Crashlytics.setString("Timezone", String.valueOf(TimeZone.getDefault().getDisplayName()));
        Crashlytics.setString("Language", Locale.getDefault().getDisplayLanguage());
        Crashlytics.setBool("is24", DateFormat.is24HourFormat(getApplicationContext()));
        Crashlytics.setBool("Network State", Utils.isNetworkAvailable(this));

        if (Connectivity.getNetworkInfo(this) != null) {
            Crashlytics.setString("Network Info", Connectivity.getNetworkInfo(this).toString());
        }

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();

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

        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(getResources().getString(R.string.forecast_io_key))
                        .setCacheDirectory(getCacheDir())
                        .build();
        ForecastClient.create(configuration);

        mInstance = this;

        ListPreferences.create(this);

        sharedPreference = ListPreferences.getInstance(this);
        switchPreferences = SwitchPreferences.getInstance(this);


        String version;

        if (sharedPreference.isDebugEnabled()) {
            version = "dev";
        } else {
            version = "1.2.1";
        }
        DataClient.create(version);

        RealmConfiguration realmConfig;
        Realm.init(this);
        realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(DB_SCHEMA_VERSION)
                .modules(Realm.getDefaultModule(), new LaunchDataModule())
                .migration(new Migration())
                .build();

        // Get a Realm instance for this thread
        Realm.setDefaultConfiguration(realmConfig);

        if (sharedPreference.isNightThemeEnabled()) {
            if (sharedPreference.isDayNightAutoEnabled()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).centerCrop().into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }
        });

        checkSubscriptions();

        JobManager.create(this).addJobCreator(new DataJobCreator());

        DefaultRuleEngine.trackAppStart(this);

        if (!sharedPreference.getFirstBoot()) {
            //Module changes, requires migration.
            Timber.v("Stored Version Code: %s", switchPreferences.getVersionCode());
            if (switchPreferences.getVersionCode() <= DB_SCHEMA_VERSION) {
                Intent intent = new Intent(this, LaunchDataService.class);
                intent.setAction(Constants.ACTION_GET_ALL_DATA);
                this.startService(intent);
            } else {
                if (Connectivity.isConnectedWifi(this)) {
                    Intent nextIntent = new Intent(this, LaunchDataService.class);
                    nextIntent.setAction(Constants.ACTION_GET_UP_LAUNCHES);
                    this.startService(nextIntent);
                } else {
                    Intent nextIntent = new Intent(this, LaunchDataService.class);
                    nextIntent.setAction(Constants.ACTION_UPDATE_NEXT_LAUNCH);
                    this.startService(nextIntent);
                }

                if (sharedPreference.getLastVehicleUpdate() > 0) {
                    Timber.d("Time since last VehicleUpdate: %s", (System.currentTimeMillis() - sharedPreference.getLastVehicleUpdate()));
                    if ((System.currentTimeMillis() - sharedPreference.getLastVehicleUpdate()) > 1209600000) {
                        Intent rocketIntent = new Intent(this, LibraryDataService.class);
                        rocketIntent.setAction(Constants.ACTION_GET_VEHICLES_DETAIL);
                        this.startService(rocketIntent);

                    } else if (Utils.getVersionCode(this) != switchPreferences.getVersionCode()) {

                        Intent rocketIntent = new Intent(this, LibraryDataService.class);
                        rocketIntent.setAction(Constants.ACTION_GET_VEHICLES_DETAIL);
                        this.startService(rocketIntent);
                    }
                }
            }
        } else {
            Intent intent = new Intent(this, LaunchDataService.class);
            intent.setAction(Constants.ACTION_GET_ALL_DATA);
            startService(intent);
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

    @Override
    public Analytics getAnalytics() {
        Analytics analytics = mAnalytics;
        if (analytics == null) {
            synchronized (this) {
                analytics = mAnalytics;
                if (analytics == null) {
                    mAnalytics = analytics = new Analytics();
                }
            }
        }
        return analytics;
    }
}
