package me.calebjones.spacelaunchnow.wear;

import android.app.Application;
import android.content.ContextWrapper;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.pixplicity.easyprefs.library.Prefs;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchDataModule;
import me.calebjones.spacelaunchnow.wear.content.job.WearJobCreator;
import timber.log.Timber;


public class WearApplication extends Application {
    private static final long DB_SCHEMA_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Fabric.with(this, new Crashlytics());
        Realm.init(this);

        // Get a Realm instance for this thread
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                                              .schemaVersion(DB_SCHEMA_VERSION)
                                              .modules(Realm.getDefaultModule(), new LaunchDataModule())
                                              .deleteRealmIfMigrationNeeded()
                                              .build());
        JobManager.create(this).addJobCreator(new WearJobCreator());

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
