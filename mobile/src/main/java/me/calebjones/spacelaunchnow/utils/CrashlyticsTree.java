package me.calebjones.spacelaunchnow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import timber.log.Timber;

public class CrashlyticsTree extends Timber.Tree {

    private SharedPreferences sharedPref;

    public CrashlyticsTree(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        // Track INFO level logs as custom Answers events.
        if (priority == Log.INFO) {
            Answers.getInstance().logCustom(new CustomEvent(message));
        }

        if (priority == Log.VERBOSE) {
            return;
        }

        if (!sharedPref.getBoolean("debug_logging", false) && priority == Log.DEBUG) {
            return;
        }

        Crashlytics.log(message);

        if (t != null) {
            Crashlytics.logException(t);
        }

        if (priority > Log.WARN) {
            Crashlytics.logException(new Throwable(message));
        }

    }
}
