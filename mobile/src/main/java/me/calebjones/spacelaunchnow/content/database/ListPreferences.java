package me.calebjones.spacelaunchnow.content.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.data.models.Constants;
import timber.log.Timber;


public class ListPreferences {

    private static ListPreferences INSTANCE;
    private static SharedPreferences SETTINGS;
    private SharedPreferences sharedPrefs;
    private SwitchPreferences switchPreferences;
    private Context appContext;
    SharedPreferences.Editor prefsEditor;

    public static String PREFS_PREVIOUS_FIRST_BOOT;
    public static String PREFS_LIST_CALENDAR;
    public static String PREFS_NAME;
    public static String PREFS_FIRST_BOOT;
    public static String PREFS_PREVIOUS_TITLE;
    public static String PREFS_UP_TITLE;
    public static String PREFS_CURRENT_START_DATE;
    public static String PREFS_CURRENT_END_DATE;
    public static String PREFS_LAUNCH_LIST_NEXT;
    public static String PREFS_NEXT_LAUNCH_TIMESTAMP;
    public static String PREFS_NEXT_LAUNCH;
    public static String PREFS_PREV_LAUNCH;
    public static String PREFS_DEBUG;
    public static String PREFS_DEBUG_SUPPORT;
    public static String PREFS_LAST_VEHICLE_UPDATE;


    static {
        PREFS_NAME = "SPACE_LAUNCH_NOW_PREFS";
        PREFS_FIRST_BOOT = "IS_FIRST_BOOT";
        PREFS_PREVIOUS_TITLE = "CURRENT_YEAR_RANGE";
        PREFS_UP_TITLE = "UP_CURRENT_TITLE";
        PREFS_CURRENT_START_DATE = "CURRENT_START_DATE";
        PREFS_CURRENT_END_DATE = "CURRENT_END_DATE";
        PREFS_LAUNCH_LIST_NEXT = "LAUNCH_LIST_FAVS";
        PREFS_NEXT_LAUNCH = "NEXT_LAUNCH";
        PREFS_PREV_LAUNCH = "PREV_LAUNCH";
        PREFS_LAST_VEHICLE_UPDATE = "LAST_VEHICLE_UPDATE";
        PREFS_NEXT_LAUNCH_TIMESTAMP = "NEXT_LAUNCH_TIMESTAMP";
        PREFS_PREVIOUS_FIRST_BOOT = "IS_PREVIOUS_FIRST_BOOT";
        PREFS_DEBUG = "DEBUG";
        PREFS_DEBUG_SUPPORT = "DEBUG_SUPPORT";
        INSTANCE = null;
    }

    private ListPreferences(Context context) {
        this.sharedPrefs = null;
        this.prefsEditor = null;
        this.appContext = context.getApplicationContext();
        this.switchPreferences = SwitchPreferences.getInstance(this.appContext);
    }

    public static ListPreferences getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ListPreferences(context);
        }
        return INSTANCE;
    }

    public void setPreviousFirstBoot(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_PREVIOUS_FIRST_BOOT, value);
        this.prefsEditor.apply();
    }

    public boolean getPreviousFirstBoot() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_PREVIOUS_FIRST_BOOT, true);
    }

    public boolean isNightModeActive(Context context) {

        if (isDayNightAutoEnabled()) {
            int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            Timber.v("Configuration Key %s", currentNightMode);

            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    // Night mode is not active, we're in day time
                    Timber.v("Auto Theme: UI_MODE_NIGHT_NO");
                    return false;
                case Configuration.UI_MODE_NIGHT_YES:
                    // Night mode is active, we're at night!
                    Timber.v("Auto Theme: UI_MODE_NIGHT_YES");
                    return true;
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    // We don't know what mode we're in, assume notnight
                    Timber.v("Auto Theme: UI_MODE_NIGHT_UNDEFINED");
                    return false;
                default:
                    Timber.e("Auto Theme: Unknown");
                    return false;
            }
        } else if (isNightThemeEnabled()){
            Timber.v("Theme: UI_MODE_NIGHT_YES");
            return true;
        } else {
            Timber.v("Theme: UI_MODE_NIGHT_NO");
            return false;
        }
    }

    public boolean isNightThemeEnabled() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        return this.sharedPrefs.getBoolean("theme", false);
    }

    public boolean isDayNightAutoEnabled() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        return this.sharedPrefs.getBoolean("theme_auto", false);
    }
    
    public void setDebugSupporter(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_DEBUG_SUPPORT, value);
        this.prefsEditor.apply();
    }

    public boolean isDebugSupporterEnabled() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_DEBUG_SUPPORT, false);
    }

    public void setNetworkEndpoint(String value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_DEBUG, value);
        this.prefsEditor.apply();
    }

    public String getNetworkEndpoint() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_DEBUG, Constants.API_BASE_URL);
    }

    public void setFirstBoot(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_FIRST_BOOT, value);
        this.prefsEditor.apply();
    }

    public boolean getFirstBoot() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_FIRST_BOOT, true);
    }

    public static Integer[] toIntArray(String input) {
        if (input.length() > 0) {
            String beforeSplit = input.replaceAll("\\[|\\]|\\s", "");
            String[] split = beforeSplit.split("\\,");
            Integer[] result = new Integer[split.length];
            for (int i = 0; i < split.length; i++) {
                result[i] = Integer.parseInt(split[i]);
            }
            return result;
        }
        return null;
    }

    //Methods for saving and restoring
    public void setPreviousTitle(String currentYearRange) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREVIOUS_TITLE, currentYearRange);
        this.prefsEditor.apply();
    }

    public String getPreviousTitle() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_PREVIOUS_TITLE, "Space Launch Now");
    }

    public void resetPreviousTitle() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREVIOUS_TITLE, "Space Launch Now");
        this.prefsEditor.apply();
    }

    //Methods for saving and restoring
    public void setUpTitle(String currentYearRange) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_TITLE, currentYearRange);
        this.prefsEditor.apply();
    }

    public String getUpTitle() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_UP_TITLE, "Space Launch Now");
    }

    public void resetUpTitle() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_TITLE, "Space Launch Now");
        this.prefsEditor.apply();
    }

    public void setStartDate(String startDate) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_CURRENT_START_DATE, startDate);
        this.prefsEditor.apply();
    }

    public String getStartDate() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_CURRENT_START_DATE, "1900-01-01");
    }

    public void setEndDate(String endDate) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_CURRENT_END_DATE, endDate);
        this.prefsEditor.apply();
    }

    public String getEndDate() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_CURRENT_END_DATE, "2016-05-04");
    }



    public void isFresh(boolean bool) {
        Timber.v("Changing isFresh: %s", bool);
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean("isFresh", bool);
        this.prefsEditor.apply();
    }

    public boolean isFresh() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean("isFresh", false);
    }
}
