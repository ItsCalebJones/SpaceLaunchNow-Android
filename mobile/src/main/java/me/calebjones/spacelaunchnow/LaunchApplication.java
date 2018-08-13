package me.calebjones.spacelaunchnow;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.michaelflisar.gdprdialog.GDPR;
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
import jonathanfinerty.once.Once;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.DataJobCreator;
import me.calebjones.spacelaunchnow.content.jobs.SyncJob;
import me.calebjones.spacelaunchnow.content.jobs.SyncWearJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateWearJob;
import me.calebjones.spacelaunchnow.content.notifications.NotificationHelper;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Products;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchDataModule;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
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
    private FirebaseMessaging firebaseMessaging;
    private BillingProcessor bp;


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
            Once.markDone("loadInitialData");
        }
        Once.markDone("appOpen");
    }


    private void setupAds() {
        GDPR.getInstance().init(this);
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
        DataClient.create(version, getString(R.string.sln_token));
        JobManager.create(context).addJobCreator(new DataJobCreator());
        startJobs();
    }


    private void setupRealm() {
        Timber.d("Realm.init()");
        Realm.init(this);

        Timber.d("Realm building config");
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(Constants.DB_SCHEMA_VERSION_2_6_0)
                .modules(Realm.getDefaultModule(), new LaunchDataModule())
                .deleteRealmIfMigrationNeeded()
                .build();

        Timber.d("Realm set Default");
        Realm.setDefaultConfiguration(config);
        Timber.d("Realm opened and closed successfully.");
        setupData(false);
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(context);
        if (isAvailable) {
            bp = new BillingProcessor(this, getResources().getString(R.string.rsa_key), new BillingProcessor.IBillingHandler() {
                @Override
                public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                    Timber.d("onProductPurchased");
                }

                @Override
                public void onPurchaseHistoryRestored() {
                    Timber.d("onPurchaseHistoryRestored");
                }

                @Override
                public void onBillingError(int errorCode, @Nullable Throwable error) {
                    Timber.d("onBillingError");
                }

                @Override
                public void onBillingInitialized() {
                    Timber.d("onBillingInitialized");
                    restorePurchases();
                }
            });
        }

    }

    private void restorePurchases() {
        bp.loadOwnedPurchasesFromGoogle();
        Timber.d("Purchase History Restored - Number of items purchased: %s", bp.listOwnedProducts().size());
        if (bp != null && bp.listOwnedProducts().size() > 0) {
            for (final String sku : bp.listOwnedProducts()) {
                Timber.v("Purchase History - SKU: %s", sku);
                Products product = SupporterHelper.getProduct(sku);
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(product));
                realm.close();
            }
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

        new Thread(() -> {
            // Initialize Fabric with the debug-disabled crashlytics.
            Crashlytics.setString("Timezone", String.valueOf(TimeZone.getDefault().getDisplayName()));
            Crashlytics.setString("Language", Locale.getDefault().getDisplayLanguage());
            Crashlytics.setBool("is24", DateFormat.is24HourFormat(context));
            Crashlytics.setBool("Network State", Utils.isNetworkAvailable(context));
            Crashlytics.setString("Network Info", Connectivity.getNetworkStatus(context));
            Crashlytics.setBool("Debug Logging", sharedPref.getBoolean("debug_logging", false));
        }).start();
    }


    private void startJobs() {
        new Thread(() -> {
            SyncJob.schedulePeriodicJob(context);
            SyncWearJob.scheduleJob();
            UpdateWearJob.scheduleJobNow();
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
