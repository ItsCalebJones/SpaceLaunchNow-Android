package calebjones.me.spacelaunchnow.content.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Holder for future settings.
 */
public class SharedPrefs {

    private static SharedPrefs mInstance;

    private SharedPreferences sharedPreferences;

    private SharedPrefs(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void create(Context context) {
        mInstance = new SharedPrefs(context);
    }

    public static SharedPrefs getInstance() {
        return mInstance;
    }

}
