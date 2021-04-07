package me.calebjones.spacelaunchnow.utils.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import timber.log.Timber;

public class CrashlyticsTree extends Timber.Tree {

    private SharedPreferences sharedPref;

    public CrashlyticsTree(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {


//        Track INFO level logs as custom Answers events.
//        if (priority == Log.INFO) {
//            Answers.getInstance().logCustom(new CustomEvent(getMessage));
//        }

        if (priority == Log.VERBOSE) {
            return;
        }

        if (!sharedPref.getBoolean("debug_logging", false) && priority == Log.DEBUG) {
            return;
        }
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.log(message);
    }
}
