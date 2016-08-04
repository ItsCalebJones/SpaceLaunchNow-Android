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

    public static void create(Context context) {
        INSTANCE = new ListPreferences(context);
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

    public long getLastVehicleUpdate() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getLong(PREFS_LAST_VEHICLE_UPDATE, 0);
    }

    public void setLastVehicleUpdate(long value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putLong(PREFS_LAST_VEHICLE_UPDATE, value);
        this.prefsEditor.apply();
    }

    public boolean isNightModeActive(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're in day time
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're at night!
                return true;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // We don't know what mode we're in, assume notnight
                return false;
            default:
                return false;
        }
    }

    public boolean isDayNightEnabled() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        return this.sharedPrefs.getBoolean("theme", false);
    }

    public boolean isDayNightAutoEnabled() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        return this.sharedPrefs.getBoolean("theme_auto", false);
    }

    public void setDebugLaunch(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_DEBUG, value);
        this.prefsEditor.apply();
    }

    public boolean isDebugEnabled() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_DEBUG, false);
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

    public class GsonDateDeSerializer implements JsonDeserializer<Date> {

        private SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US);
        private SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US);

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String j = json.getAsJsonPrimitive().getAsString();
                Date date = parseDate(j);
                return date;
            } catch (ParseException e) {
                Crashlytics.setString("Timezone", String.valueOf(TimeZone.getDefault()));
                Crashlytics.setString("Language", Locale.getDefault().getDisplayLanguage());
                Crashlytics.setBool("is24", DateFormat.is24HourFormat(appContext));
                Crashlytics.logException(new JsonParseException(e.getMessage(), e));
                return null;
            }
        }

        private Date parseDate(String dateString) throws ParseException {
            if (dateString != null && dateString.trim().length() > 0) {
                try {
                    return format1.parse(dateString);
                } catch (ParseException pe) {
                    return format2.parse(dateString);
                }
            } else {
                return null;
            }
        }

    }
}
