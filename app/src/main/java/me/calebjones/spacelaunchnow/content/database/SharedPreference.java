package me.calebjones.spacelaunchnow.content.database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;

import me.calebjones.spacelaunchnow.content.models.Launch;
import timber.log.Timber;

/**
 * Holder for future settings.
 */
public class SharedPreference {

    private static SharedPreference INSTANCE;
    private SharedPreferences sharedPrefs;
    private Context appContext;
    SharedPreferences.Editor prefsEditor;

    public static String PREFS_LAUNCH_LIST_PREVIOUS;
    public static String PREFS_LAUNCH_LIST_UPCOMING;
    public static String PREFS_NAME;
    public static String PREFS_FIRST_BOOT;
    public static String PREFS_POSITION;
    public static String PREFS_POSITION_LIST_PREVIOUS;
    public static String PREFS_POSITION_LIST_UPCOMING;
    public static String PREFS_PREVIOUS_FILTER_TEXT;
    public static String PREFS_PREVIOUS_FIRST_BOOT;
    public static String PREFS_UPCOMING_FIRST_BOOT;
    public static String PREFS_UPCOMING_COUNT;
    public static String PREFS_UPCOMING_FILTER_TEXT;
    public static String PREFS_NIGHT_MODE_STATUS;
    public static String PREFS_NIGHT_MODE_START;
    public static String PREFS_NIGHT_MODE_END;
    public static String PREFS_LAST_UPCOMING_LAUNCH_UPDATE;
    public static String PREFS_LAST_PREVIOUS_LAUNCH_UPDATE;
    public static String PREFS_PREVIOUS_TITLE;
    public static String PREFS_CURRENT_START_DATE;
    public static String PREFS_CURRENT_END_DATE;


    static {
        PREFS_NAME = "SPACE_LAUNCH_NOW_PREFS";
        PREFS_FIRST_BOOT = "IS_FIRST_BOOT";
        PREFS_PREVIOUS_FIRST_BOOT = "IS_PREVIOUS_FIRST_BOOT";
        PREFS_UPCOMING_FIRST_BOOT = "IS_UPCOMING_FIRST_BOOT";
        PREFS_POSITION = "POSITION_VALUE";
        PREFS_LAUNCH_LIST_UPCOMING = "LAUNCH_LIST_UPCOMING";
        PREFS_LAUNCH_LIST_PREVIOUS = "LAUNCH_LIST_PREVIOUS";
        PREFS_POSITION_LIST_UPCOMING = "POSITION_LIST_UPCOMING";
        PREFS_POSITION_LIST_PREVIOUS = "POSITION_LIST_PREVIOUS";
        PREFS_PREVIOUS_FILTER_TEXT = "PREVIOUS_FILTER_TEXT";
        PREFS_UPCOMING_FILTER_TEXT = "UPCOMING_FILTER_TEXT";
        PREFS_UPCOMING_COUNT = "LAUNCH_LIST_COUNT";
        PREFS_NIGHT_MODE_STATUS = "NIGHT_MODE_STATUS";
        PREFS_NIGHT_MODE_START = "NIGHT_MODE_START";
        PREFS_NIGHT_MODE_END = "NIGHT_MODE_END";
        PREFS_LAST_UPCOMING_LAUNCH_UPDATE = "LAST_UPCOMING_LAUNCH_UPDATE";
        PREFS_LAST_PREVIOUS_LAUNCH_UPDATE = "LAST_PREVIOUS_LAUNCH_UPDATE";
        PREFS_PREVIOUS_TITLE = "CURRENT_YEAR_RANGE";
        PREFS_CURRENT_START_DATE = "CURRENT_START_DATE";
        PREFS_CURRENT_END_DATE = "CURRENT_END_DATE";
        INSTANCE = null;
    }

    private SharedPreference(Context context) {
        this.sharedPrefs = null;
        this.prefsEditor = null;
        this.appContext = context.getApplicationContext();
    }

    public static SharedPreference getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SharedPreference(context);
        }
        return INSTANCE;
    }

    public static void create(Context context) {
        INSTANCE = new SharedPreference(context);
    }

    public boolean getNightMode() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);

        boolean dark_theme = this.sharedPrefs.getBoolean("theme", false);
        boolean auto_theme = this.sharedPrefs.getBoolean("auto_theme", false);

        Calendar now = Calendar.getInstance();

        String startTime = INSTANCE.getNightModeStart() + ":00";
        String endTime = INSTANCE.getNightModeEnd() + ":00";
        String currentH, currentM, currentS;

        //Format Values to something more extensible.
        if (now.get(Calendar.HOUR_OF_DAY) < 10){
            currentH = "0" + now.get(Calendar.HOUR_OF_DAY);
        } else {
            currentH = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
        }

        if (now.get(Calendar.MINUTE) < 10){
            currentM = "0" + now.get(Calendar.MINUTE);
        } else {
            currentM = String.valueOf(now.get(Calendar.MINUTE));
        }

        if (now.get(Calendar.SECOND) < 10){
            currentS = "0" + now.get(Calendar.SECOND);
        } else {
            currentS = String.valueOf(now.get(Calendar.SECOND));
        }

        String currentTime = currentH + ":" + currentM + ":" + currentS;

        if (dark_theme) {
            if (auto_theme) {
                try {
                    if(isTimeBetweenTwoTime(startTime, endTime, currentTime)){
                        if(!INSTANCE.getNightModeStatus()){
                            INSTANCE.setNightModeStatus(true);
                            Toast.makeText(appContext, "Auto-Theme switching to night mode!",Toast.LENGTH_LONG).show();
                            Intent i = appContext.getPackageManager()
                                    .getLaunchIntentForPackage( appContext.getPackageName() );
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            appContext.startActivity(i);
                        }
                        return true;
                    } else {
                        if(INSTANCE.getNightModeStatus()){
                            INSTANCE.setNightModeStatus(false);
                            Toast.makeText(appContext, "Auto-Theme switching to day mode!",Toast.LENGTH_LONG).show();
                            Intent i = appContext.getPackageManager()
                                    .getLaunchIntentForPackage( appContext.getPackageName() );
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            appContext.startActivity(i);
                        }
                        return false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void setNightModeStart(String value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_NIGHT_MODE_START, value);
        this.prefsEditor.apply();
    }

    public String getNightModeStart() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_NIGHT_MODE_START, "22:00");
    }

    public void setNightModeEnd(String value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_NIGHT_MODE_END, value);
        this.prefsEditor.apply();
    }

    public String getNightModeEnd() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_NIGHT_MODE_END, "7:00");
    }

    public void setNightModeStatus(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_NIGHT_MODE_STATUS, value);
        this.prefsEditor.apply();
    }

    public boolean getNightModeStatus() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_NIGHT_MODE_STATUS, false);
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

    public void setUpcomingFirstBoot(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_UPCOMING_FIRST_BOOT, value);
        this.prefsEditor.apply();
    }

    public boolean getUpcomingFirstBoot() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_UPCOMING_FIRST_BOOT, true);
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

    public void setUpComingLaunches(List<Launch> launches) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LAUNCH_LIST_UPCOMING, gson.toJson(launches));
        this.prefsEditor.apply();
    }

    public void removeUpcomingLaunches() {
        setUpComingLaunches(new ArrayList());
    }

    public void setPreviousLaunches(List<Launch> launches) {
        Timber.d("SharedPreference - setPrevious list:  %s ", launches.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LAUNCH_LIST_PREVIOUS, gson.toJson(launches));
        this.prefsEditor.apply();
    }

    public void removePreviousLaunches() {
        setPreviousLaunches(new ArrayList());
    }

    public void saveCurrentPosition(int pos) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putInt(PREFS_POSITION, pos);
        this.prefsEditor.apply();
    }

    public int getCurrentPosition() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getInt(PREFS_POSITION, 0);
    }

    public List<Launch> getLaunchesUpcoming() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LAUNCH_LIST_UPCOMING)) {
            return null;
        }
        Gson gson = new Gson();
        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LAUNCH_LIST_UPCOMING, null);

        Type type = new TypeToken<List<Launch>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public List<Launch> getLaunchesPrevious() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LAUNCH_LIST_PREVIOUS)) {
            return null;
        }
        Gson gson = new Gson();
        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LAUNCH_LIST_PREVIOUS, null);

        Type type = new TypeToken<List<Launch>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public void setPreviousFilterText(String filterText) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREVIOUS_FILTER_TEXT, filterText);
        this.prefsEditor.apply();
    }

    public void setUpcomingFilterText(String filterText) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UPCOMING_FILTER_TEXT, filterText);
        this.prefsEditor.apply();
    }

    public String getPreviousFilterText() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_PREVIOUS_FILTER_TEXT, "");
    }

    public String getUpcomingFilterText() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_UPCOMING_FILTER_TEXT, "");
    }

    public void setLastUpcomingLaunchUpdate(String last_update) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_LAST_UPCOMING_LAUNCH_UPDATE, last_update);
        this.prefsEditor.apply();
    }

    public String getLastUpcomingLaunchUpdate() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_LAST_UPCOMING_LAUNCH_UPDATE, "");
    }

    public void setLastPreviousLaunchUpdate(String last_update) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_LAST_PREVIOUS_LAUNCH_UPDATE, last_update);
        this.prefsEditor.apply();
    }

    public String getLastPreviousLaunchUpdate() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_LAST_PREVIOUS_LAUNCH_UPDATE, "");
    }

    public void setPreviousTitle(String currentYearRange) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREVIOUS_TITLE, currentYearRange);
        this.prefsEditor.apply();
    }

    public String getPreviousTitle() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_PREVIOUS_TITLE, "Previous Launches");
    }

    public void resetPreviousTitle() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREVIOUS_TITLE, "Previous Launches");
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
        return this.sharedPrefs.getString(PREFS_CURRENT_END_DATE, "2016-01-01");
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String currentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid = false;
            //Start Time
            java.util.Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0)
                    && actualTime.before(calendar2.getTime())) {
                valid = true;
            }
            Timber.d("Dark Theme - Within time: %s", valid);
            return valid;
        } else {
            throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
        }

    }
}
