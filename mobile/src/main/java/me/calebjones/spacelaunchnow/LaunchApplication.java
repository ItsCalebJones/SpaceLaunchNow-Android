package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.format.DateFormat;
import android.webkit.WebView;
import android.widget.ImageView;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.google.android.gms.ads.MobileAds;
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
import io.realm.RealmObjectSchema;
import io.realm.exceptions.RealmMigrationNeededException;
import jonathanfinerty.once.Once;
import me.calebjones.spacelaunchnow.content.data.DataRepositoryManager;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.DataJobCreator;
import me.calebjones.spacelaunchnow.content.jobs.SyncJob;
import me.calebjones.spacelaunchnow.content.jobs.SyncWearJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateJob;
import me.calebjones.spacelaunchnow.content.services.LibraryDataManager;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchDataModule;
import me.calebjones.spacelaunchnow.data.models.realm.Migration;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import me.calebjones.spacelaunchnow.utils.analytics.CrashlyticsTree;
import me.calebjones.spacelaunchnow.utils.Utils;
import okhttp3.OkHttpClient;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.data.models.Constants.DB_SCHEMA_VERSION_1_5_6;

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
                .core(new CrashlyticsCore.Builder().build())
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
            Timber.plant(new Timber.DebugTree(), new CrashlyticsTree(this));
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.ERROR);

            JSONObject tags = new JSONObject();
            try {
                tags.put("Production", false);
                tags.put("Debug", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OneSignal.sendTags(tags);
        } else {
            JSONObject tags = new JSONObject();
            try {
                tags.put("Production", true);
                tags.put("Debug", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OneSignal.sendTags(tags);
            Timber.plant(new CrashlyticsTree(this));
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
            version = "1.3";
        }

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(Constants.DB_SCHEMA_VERSION_1_8_1)
                .modules(Realm.getDefaultModule(), new LaunchDataModule())
                .migration(new Migration())
                .build();

        DataClient.create(version);
        LibraryDataManager libraryDataManager;
        JobManager.create(this).addJobCreator(new DataJobCreator());
        try {
            Realm.setDefaultConfiguration(config);
            Realm realm = Realm.getDefaultInstance();
            realm.close();
            libraryDataManager = new LibraryDataManager(this);
        } catch (RealmMigrationNeededException | NullPointerException e) {
            Timber.e(e);
            Realm.deleteRealm(config);
            Realm.setDefaultConfiguration(config);
            libraryDataManager = new LibraryDataManager(this);
            libraryDataManager.getAllData();
            Crashlytics.logException(e);
        }

        try {
            new WebView(getApplicationContext());
        } catch (Exception e) {
            Timber.e(e);
        }
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
                GlideApp.with(imageView.getContext()).load(uri).placeholder(placeholder).centerCrop().into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                GlideApp.with(imageView.getContext()).clear(imageView);
            }
        });

        try {
            checkSubscriptions();
        } catch (JSONException e) {
            Timber.e(e);
            Crashlytics.logException(e);
        }


        if (sharedPref.getBoolean("background", true)) {
            UpdateJob.scheduleJob(this);
        }
        SyncJob.schedulePeriodicJob(this);
        SyncWearJob.scheduleJob();

        DefaultRuleEngine.trackAppStart(this);

        MobileAds.initialize(this, "ca-app-pub-9824528399164059~9700152528");

        Once.initialise(this);

        if (Once.beenDone(Once.THIS_APP_INSTALL, "loadInitialData")) {
            DataRepositoryManager dataRepositoryManager = new DataRepositoryManager(this);
            dataRepositoryManager.syncBackground();
        } else {
            libraryDataManager.getAllData();
            Once.markDone("loadInitialData");
        }
    }

    private void checkSubscriptions() throws JSONException {
        //TODO reconsider the boolean for notificaitons_new_message
        if (sharedPref.getBoolean("notifications_new_message", true)) {
            OneSignal.setSubscription(true);
            JSONObject tags = new JSONObject();

            tags.put("Nasa", switchPreferences.getSwitchNasa());

            tags.put("ISRO", switchPreferences.getSwitchISRO());

            tags.put("Roscosmos", switchPreferences.getSwitchRoscosmos());

            tags.put("ULA", switchPreferences.getSwitchULA());

            tags.put("Arianespace", switchPreferences.getSwitchArianespace());

            tags.put("KSC", switchPreferences.getSwitchKSC());

            tags.put("Ples", switchPreferences.getSwitchPles());

            tags.put("Van", switchPreferences.getSwitchVan());

            tags.put("SpaceX", switchPreferences.getSwitchSpaceX());

            tags.put("CASC", switchPreferences.getSwitchCASC());

            tags.put("Cape", switchPreferences.getSwitchCape());

            tags.put("all", switchPreferences.getAllSwitch());

            //Allow background alarms
            tags.put("background", 1);

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
