package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.Locale;
import java.util.TimeZone;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import jonathanfinerty.once.Once;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.DataJobCreator;
import me.calebjones.spacelaunchnow.content.jobs.SyncJob;
import me.calebjones.spacelaunchnow.content.jobs.SyncWearJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateWearJob;
import me.calebjones.spacelaunchnow.content.notifications.NotificationHelper;
import me.calebjones.spacelaunchnow.content.services.LibraryDataManager;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchDataModule;
import me.calebjones.spacelaunchnow.data.models.realm.Migration;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.analytics.CrashlyticsTree;
import timber.log.Timber;

public class LaunchApplication extends Application {

    public static final String TAG = "Space Launch Now";
    private static ListPreferences sharedPreference;
    private SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;
    protected volatile Analytics mAnalytics;
    private Context context;
    private LibraryDataManager libraryDataManager;
    private FirebaseMessaging firebaseMessaging;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        firebaseMessaging = FirebaseMessaging.getInstance();
        setupAds();
        setupPreferences();
        setupCrashlytics();
        setupNotification();
        setupRealm();
        setupForecast();
        setupWebView();
        setupTheme();
        setupDrawableLoader();
        checkSubscriptions();
        setupAndCheckOnce();
        setupNotificationChannels();
        setupTwitter();
    }

    private void setupTwitter() {
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.consumer_key), getString(R.string.consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        new Thread() {
            @Override
            public void run() {
                TweetUi.getInstance();
            }
        }.start();
    }

    private void setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new NotificationHelper(context);
        }
    }

    private void setupAndCheckOnce() {
        Once.initialise(this);

        if (!Once.beenDone(Once.THIS_APP_INSTALL, "loadInitialData")) {
            libraryDataManager.getFirstLaunchData();
            Once.markDone("loadInitialData");
        }
        Once.markDone("appOpen");
    }


    private void setupAds() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MobileAds.initialize(context, "ca-app-pub-9824528399164059~9700152528");
            }
        }).start();
    }


    private void setupDrawableLoader() {
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
    }


    private void setupTheme() {
        if (sharedPreference.isNightThemeEnabled()) {
            if (sharedPreference.isDayNightAutoEnabled()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    private void setupWebView() {
        try {
            new WebView(context);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private void setupData(final boolean update) {
        final String version;
        if (sharedPreference.isDebugEnabled()) {
            version = "dev";
        } else {
            version = "1.3";
        }
        DataClient.create(version);
        libraryDataManager = new LibraryDataManager(context);
        JobManager.create(context).addJobCreator(new DataJobCreator());
        startJobs();
        if (update) {
            libraryDataManager.getFirstLaunchData();
        }
    }


    private void setupRealm() {
        Timber.d("Realm.init()");
        Realm.init(this);

        Timber.d("Realm building config");
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(Constants.DB_SCHEMA_VERSION_2_3_1)
                .modules(Realm.getDefaultModule(), new LaunchDataModule())
                .migration(new Migration())
                .build();


        try {
            Timber.d("Realm set Default");
            Realm.setDefaultConfiguration(config);
            Realm realm = Realm.getDefaultInstance();
            realm.close();
            Timber.d("Realm opened and closed successfully.");
            setupData(false);
        } catch (RealmMigrationNeededException | NullPointerException e) {
            Timber.d("Realm Migration Exception");
            Timber.e(e);
            Realm.deleteRealm(config);
            Realm.setDefaultConfiguration(config);
            setupData(true);
            Crashlytics.logException(e);
        }

    }


    private void setupPreferences() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreference = ListPreferences.getInstance(this);
        switchPreferences = SwitchPreferences.getInstance(this);
    }


    private void setupForecast() {
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(getResources().getString(R.string.forecast_io_key))
                        .setCacheDirectory(getCacheDir())
                        .build();
        ForecastClient.create(configuration);
    }


    private void setupNotification() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree(), new CrashlyticsTree(context));
            firebaseMessaging.subscribeToTopic("debug");
            firebaseMessaging.unsubscribeFromTopic("production");

        } else {
            Timber.plant(new CrashlyticsTree(context));
            firebaseMessaging.subscribeToTopic("production");
            firebaseMessaging.unsubscribeFromTopic("debug");
        }
    }


    private void setupCrashlytics() {
        /*
         * Init Crashlytics and gather additional device information.
         * Always leave this at the top so it catches any init failures.
         * Version 1.3.0-Beta had a bug where starting a service crashed before Crashlytics picked it up.
         */
        // Set up Crashlytics, disabled for debug builds

        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().build())
                .build();
        Fabric.with(context, crashlyticsKit);
        Analytics.create(this);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // Initialize Fabric with the debug-disabled crashlytics.
                Crashlytics.setString("Timezone", String.valueOf(TimeZone.getDefault().getDisplayName()));
                Crashlytics.setString("Language", Locale.getDefault().getDisplayLanguage());
                Crashlytics.setBool("is24", DateFormat.is24HourFormat(context));
                Crashlytics.setBool("Network State", Utils.isNetworkAvailable(context));
                Crashlytics.setString("Network Info", Connectivity.getNetworkStatus(context));
                Crashlytics.setBool("Debug Logging", sharedPref.getBoolean("debug_logging", false));
            }
        }).start();
    }


    private void startJobs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getBoolean("background", true)) {
                    UpdateJob.scheduleJob(context);
                }
                SyncJob.schedulePeriodicJob(context);
                SyncWearJob.scheduleJob();
                UpdateWearJob.scheduleJobNow();
            }
        }).start();
    }

    private void checkSubscriptions() {
        if (sharedPref.getBoolean("notifications_new_message", true)) {

            if (switchPreferences.getSwitchNasa()) {
                firebaseMessaging.subscribeToTopic("nasa");
            } else {
                firebaseMessaging.unsubscribeFromTopic("nasa");
            }

            if (switchPreferences.getSwitchISRO()) {
                firebaseMessaging.subscribeToTopic("isro");
            } else {
                firebaseMessaging.unsubscribeFromTopic("isro");
            }

            if (switchPreferences.getSwitchRoscosmos()) {
                firebaseMessaging.subscribeToTopic("roscosmos");
            } else {
                firebaseMessaging.unsubscribeFromTopic("roscosmos");
            }

            if (switchPreferences.getSwitchULA()) {
                firebaseMessaging.subscribeToTopic("ula");
            } else {
                firebaseMessaging.unsubscribeFromTopic("ula");
            }

            if (switchPreferences.getSwitchArianespace()) {
                firebaseMessaging.subscribeToTopic("arianespace");
            } else {
                firebaseMessaging.unsubscribeFromTopic("arianespace");
            }

            if (switchPreferences.getSwitchKSC()) {
                firebaseMessaging.subscribeToTopic("ksc");
            } else {
                firebaseMessaging.unsubscribeFromTopic("ksc");
            }

            if (switchPreferences.getSwitchPles()) {
                firebaseMessaging.subscribeToTopic("ples");
            } else {
                firebaseMessaging.unsubscribeFromTopic("ples");
            }

            if (switchPreferences.getSwitchVan()) {
                firebaseMessaging.subscribeToTopic("van");
            } else {
                firebaseMessaging.unsubscribeFromTopic("van");
            }

            if (switchPreferences.getSwitchSpaceX()) {
                firebaseMessaging.subscribeToTopic("spacex");
            } else {
                firebaseMessaging.unsubscribeFromTopic("spacex");
            }

            if (switchPreferences.getSwitchCASC()) {
                firebaseMessaging.subscribeToTopic("casc");
            } else {
                firebaseMessaging.unsubscribeFromTopic("casc");
            }

            if (switchPreferences.getSwitchCape()) {
                firebaseMessaging.subscribeToTopic("cape");
            } else {
                firebaseMessaging.unsubscribeFromTopic("cape");
            }

            if (switchPreferences.getAllSwitch()) {
                firebaseMessaging.subscribeToTopic("all");
            } else {
                firebaseMessaging.unsubscribeFromTopic("all");
            }

            firebaseMessaging.subscribeToTopic("notifications_new_message");
        } else {
            firebaseMessaging.unsubscribeFromTopic("notifications_new_message");
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
