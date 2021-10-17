package me.calebjones.spacelaunchnow.common;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.michaelflisar.gdprdialog.GDPR;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.pixplicity.easyprefs.library.Prefs;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetUi;

import org.solovyev.android.checkout.Billing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import jonathanfinerty.once.Once;
import me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper;
import me.calebjones.spacelaunchnow.common.content.worker.CalendarSyncWorker;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.common.prefs.ThemeHelper;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.common.utils.Connectivity;
import me.calebjones.spacelaunchnow.common.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.common.utils.analytics.CrashlyticsTree;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchDataModule;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import timber.log.Timber;

public class LaunchApplication extends Application {

    public static final String TAG = "Space Launch Now";
    private static ListPreferences sharedPreference;
    private SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;
    private Context context;
    private FirebaseMessaging firebaseMessaging;
    private static LaunchApplication sInstance;

    public static final String[] INAPP_SKUS = new String[]{
            SupporterHelper.SKU_2021_BRONZE,
            SupporterHelper.SKU_2021_SILVER,
            SupporterHelper.SKU_2021_GOLD,
            SupporterHelper.SKU_2021_METAL,
            SupporterHelper.SKU_2021_PLATINUM};

    public static final List<String> LIST_INAPP_SKUS = new ArrayList<>(Arrays.asList(
                SupporterHelper.SKU_2021_BRONZE,
                SupporterHelper.SKU_2021_SILVER,
                SupporterHelper.SKU_2021_GOLD,
                SupporterHelper.SKU_2021_METAL,
                SupporterHelper.SKU_2021_PLATINUM));


    private Billing mBilling;



    public LaunchApplication() {
        sInstance = this;
    }

    public static LaunchApplication get() {
        return sInstance;
    }

    public Billing getBilling() {
        return mBilling;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        firebaseMessaging = FirebaseMessaging.getInstance();
        setTheme();
        setupAndCheckOnce();
        setupAds();
        setupPreferences();
        setupCrashlytics();
        setupNotification();
        setupRealm();
        setupForecast();
        setupWebView();
        setupDrawableLoader();
        setupNotificationChannels();
        setupTwitter();
        setupBilling();
    }

    private void setupBilling(){
        mBilling = new Billing(this, new Billing.DefaultConfiguration() {

            @Nonnull
            @Override
            public String getPublicKey() {
                return getString(R.string.rsa_key);
            }
        });
    }

    private void setTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String themePref = prefs.getString(getString(R.string.theme_pref_key),
                ThemeHelper.DEFAULT_MODE);
        ThemeHelper.applyTheme(themePref);
    }

    private void setupTwitter() {
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.consumer_key), getString(R.string.consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        new Thread(TweetUi::getInstance).start();
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
        MobileAds.initialize(this, initializationStatus -> {
            if (BuildConfig.DEBUG) {
                List<String> testDeviceIds = Arrays.asList("950298D46A76B633187461C832E37ADC");
                RequestConfiguration configuration =
                        new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
                MobileAds.setRequestConfiguration(configuration);
            }
        });

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


    private void setupWebView() {
        try {
            new WebView(context);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private void setupData(final boolean update) {
        DataClient.create(getString(R.string.sln_token), sharedPreference.getNetworkEndpoint());
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

        // Initialize Fabric with the debug-disabled crashlytics.
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("Timezone", String.valueOf(TimeZone.getDefault().getDisplayName()));
        crashlytics.setCustomKey("Language", Locale.getDefault().getDisplayLanguage());
        crashlytics.setCustomKey("is24", DateFormat.is24HourFormat(context));
        crashlytics.setCustomKey("Network Info", Connectivity.getNetworkStatus(context));
        crashlytics.setCustomKey("Debug Logging", sharedPref.getBoolean("debug_logging", false));
        crashlytics.setCustomKey("Supporter", SupporterHelper.isSupporter());
        crashlytics.setCustomKey("Calendar Sync", switchPreferences.getCalendarStatus());
        crashlytics.setCustomKey("Notifications", sharedPref.getBoolean("notifications_new_message", true));

    }

    private void setupPreferences() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreference = ListPreferences.getInstance(this);
        switchPreferences = SwitchPreferences.getInstance(this);
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    private void setupForecast() {
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(getResources().getString(R.string.forecast_io_key))
                        .setCacheDirectory(getCacheDir())
                        .build();
        ForecastClient.create(configuration);
    }

    private void setupNotification() {
        if (!Once.beenDone(Once.THIS_APP_VERSION, "syncNotifications")) {
            Once.markDone("syncNotifications");
            firebaseMessaging.unsubscribeFromTopic("debug");
            firebaseMessaging.unsubscribeFromTopic("production");
            firebaseMessaging.unsubscribeFromTopic("debug_v2");
            firebaseMessaging.unsubscribeFromTopic("prod_v2");

            if (BuildConfig.DEBUG) {
                Timber.plant(new Timber.DebugTree(), new CrashlyticsTree(context));
                firebaseMessaging.subscribeToTopic("debug_v3");
                firebaseMessaging.unsubscribeFromTopic("prod_v3");

            } else {
                Timber.plant(new CrashlyticsTree(context));
                firebaseMessaging.subscribeToTopic("prod_v3");
                firebaseMessaging.unsubscribeFromTopic("debug_v3");
            }

            boolean notificationEnabled = Prefs.getBoolean("notificationEnabled", true);
            boolean netstampChanged = Prefs.getBoolean("netstampChanged", false);
            boolean webcastOnly = Prefs.getBoolean("webcastOnly", true);
            boolean twentyFourHour = Prefs.getBoolean("twentyFourHour", false);
            boolean oneHour = Prefs.getBoolean("oneHour", false);
            boolean tenMinutes = Prefs.getBoolean("tenMinutes", true);
            boolean oneMinute = Prefs.getBoolean("oneMinute", false);
            boolean inFlight = Prefs.getBoolean("inFlight", false);
            boolean success = Prefs.getBoolean("success", false);
            boolean events = Prefs.getBoolean("eventNotifications", true);

            boolean all = switchPreferences.getAllSwitch();
            boolean ples = switchPreferences.getSwitchRussia();
            boolean ksc = switchPreferences.getSwitchKSC();
            boolean van = switchPreferences.getSwitchVan();
            boolean isro = switchPreferences.getSwitchISRO();
            boolean casc = switchPreferences.getSwitchCASC();
            boolean ariane = switchPreferences.getSwitchArianespace();
            boolean ula = switchPreferences.getSwitchULA();
            boolean roscosmos = switchPreferences.getSwitchRoscosmos();
            boolean spacex = switchPreferences.getSwitchSpaceX();
            boolean nasa = switchPreferences.getSwitchNasa();
            boolean blueOrigin = switchPreferences.getSwitchBO();
            boolean rocketLab = switchPreferences.getSwitchRL();
            boolean frenchGuiana = switchPreferences.getSwitchFG();
            boolean japan = switchPreferences.getSwitchJapan();
            boolean wallops = switchPreferences.getSwitchWallops();
            boolean northrop = switchPreferences.getSwitchNorthrop();
            boolean newZealand = switchPreferences.getSwitchNZ();
            boolean texas = switchPreferences.getSwitchTexas();
            boolean kodiak = switchPreferences.getSwitchKodiak();
            boolean other = switchPreferences.getSwitchOtherLocations();
            boolean strictMatching = switchPreferences.getSwitchStrictMatching();


            if (all) {
                firebaseMessaging.subscribeToTopic("all");
            } else {
                firebaseMessaging.unsubscribeFromTopic("all");
                firebaseMessaging.unsubscribeFromTopic("not_strict");
                firebaseMessaging.unsubscribeFromTopic("strict");
            }

            if (events) {
                firebaseMessaging.subscribeToTopic("events");
                firebaseMessaging.subscribeToTopic("featured_news");
            } else {
                firebaseMessaging.unsubscribeFromTopic("events");
                firebaseMessaging.unsubscribeFromTopic("featured_news");
            }

            if (!all) {
                if (strictMatching) {
                    firebaseMessaging.subscribeToTopic("strict");
                    firebaseMessaging.unsubscribeFromTopic("not_strict");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("strict");
                    firebaseMessaging.subscribeToTopic("not_strict");
                }
            }

            if (texas) {
                firebaseMessaging.subscribeToTopic("texas");
            } else {
                firebaseMessaging.unsubscribeFromTopic("texas");
            }

            if (kodiak) {
                firebaseMessaging.subscribeToTopic("kodiak");
            } else {
                firebaseMessaging.unsubscribeFromTopic("kodiak");
            }

            if (ksc) {
                firebaseMessaging.subscribeToTopic("ksc");
            } else {
                firebaseMessaging.unsubscribeFromTopic("ksc");
            }

            if (ples) {
                firebaseMessaging.subscribeToTopic("russia");
            } else {
                firebaseMessaging.unsubscribeFromTopic("russia");
            }

            if (van) {
                firebaseMessaging.subscribeToTopic("van");
            } else {
                firebaseMessaging.unsubscribeFromTopic("van");
            }

            if (isro) {
                firebaseMessaging.subscribeToTopic("isro");
            } else {
                firebaseMessaging.unsubscribeFromTopic("isro");
            }

            if (casc) {
                firebaseMessaging.subscribeToTopic("china");
            } else {
                firebaseMessaging.unsubscribeFromTopic("china");
            }

            if (ariane) {
                firebaseMessaging.subscribeToTopic("arianespace");
            } else {
                firebaseMessaging.unsubscribeFromTopic("arianespace");
            }

            if (blueOrigin) {
                firebaseMessaging.subscribeToTopic("blueOrigin");
            } else {
                firebaseMessaging.unsubscribeFromTopic("blueOrigin");
            }

            if (rocketLab) {
                firebaseMessaging.subscribeToTopic("rocketLab");
            } else {
                firebaseMessaging.unsubscribeFromTopic("rocketLab");
            }

            if (ula) {
                firebaseMessaging.subscribeToTopic("ula");
            } else {
                firebaseMessaging.unsubscribeFromTopic("ula");
            }

            if (roscosmos) {
                firebaseMessaging.subscribeToTopic("roscosmos");
            } else {
                firebaseMessaging.unsubscribeFromTopic("roscosmos");
            }

            if (spacex) {
                firebaseMessaging.subscribeToTopic("spacex");
            } else {
                firebaseMessaging.unsubscribeFromTopic("spacex");
            }

            if (nasa) {
                firebaseMessaging.subscribeToTopic("nasa");
            } else {
                firebaseMessaging.unsubscribeFromTopic("nasa");
            }

            if (frenchGuiana) {
                firebaseMessaging.subscribeToTopic("frenchGuiana");
            } else {
                firebaseMessaging.unsubscribeFromTopic("frenchGuiana");
            }

            if (japan) {
                firebaseMessaging.subscribeToTopic("japan");
            } else {
                firebaseMessaging.unsubscribeFromTopic("japan");
            }

            if (wallops) {
                firebaseMessaging.subscribeToTopic("wallops");
            } else {
                firebaseMessaging.unsubscribeFromTopic("wallops");
            }

            if (northrop) {
                firebaseMessaging.subscribeToTopic("northrop");
            } else {
                firebaseMessaging.unsubscribeFromTopic("northrop");
            }

            if (newZealand) {
                firebaseMessaging.subscribeToTopic("newZealand");
            } else {
                firebaseMessaging.unsubscribeFromTopic("newZealand");
            }

            if (other) {
                firebaseMessaging.subscribeToTopic("other");
            } else {
                firebaseMessaging.unsubscribeFromTopic("other");
            }

            if (notificationEnabled) {
                firebaseMessaging.subscribeToTopic("notificationEnabled");
                firebaseMessaging.subscribeToTopic("webcastLive");
                firebaseMessaging.subscribeToTopic("custom");
            } else {
                firebaseMessaging.unsubscribeFromTopic("notificationEnabled");
                firebaseMessaging.unsubscribeFromTopic("webcastLive");
                firebaseMessaging.unsubscribeFromTopic("custom");
            }

            if (netstampChanged) {
                firebaseMessaging.subscribeToTopic("netstampChanged");

            } else {
                firebaseMessaging.unsubscribeFromTopic("netstampChanged");
            }

            if (webcastOnly) {
                firebaseMessaging.subscribeToTopic("webcastOnly");
            } else {
                firebaseMessaging.unsubscribeFromTopic("webcastOnly");
            }

            if (twentyFourHour) {
                firebaseMessaging.subscribeToTopic("twentyFourHour");
            } else {
                firebaseMessaging.unsubscribeFromTopic("twentyFourHour");
            }

            if (oneHour) {
                firebaseMessaging.subscribeToTopic("oneHour");
            } else {
                firebaseMessaging.unsubscribeFromTopic("oneHour");
            }

            if (tenMinutes) {
                firebaseMessaging.subscribeToTopic("tenMinutes");
            } else {
                firebaseMessaging.unsubscribeFromTopic("tenMinutes");
            }

            if (inFlight) {
                firebaseMessaging.subscribeToTopic("inFlight");
            } else {
                firebaseMessaging.unsubscribeFromTopic("inFlight");
            }

            if (success) {
                firebaseMessaging.subscribeToTopic("success");
            } else {
                firebaseMessaging.unsubscribeFromTopic("success");
            }

            if (oneMinute) {
                firebaseMessaging.subscribeToTopic("oneMinute");
            } else {
                firebaseMessaging.unsubscribeFromTopic("oneMinute");
            }
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String token = instanceIdResult.getToken();
            Timber.v("Here is your FCM token: %s", token);
            // send it to server
        });

    }


    private void setupCrashlytics() {
        Analytics.create(this);
    }

    private void startJobs() {
        CalendarSyncWorker.scheduleWorker();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
