package me.calebjones.spacelaunchnow.content.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

public class SwitchPreferences implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static SwitchPreferences INSTANCE;
    private static SharedPreferences SETTINGS;
    private static ListPreferences listPreferences;
    private SharedPreferences sharedPrefs;
    private Context appContext;
    SharedPreferences.Editor prefsEditor;

    private static String PREFS_NAME;
    private static String PREFS_NIGHT_MODE_STATUS;
    private static String PREFS_NIGHT_MODE_START;
    private static String PREFS_NIGHT_MODE_END;
    private static String PREFS_PREV_FILTERED_DATE;
    private static String PREFS_PREV_FILTERED;
    private static String PREFS_UP_FILTERED;
    private static String PREFS_PREV_VEHICLE_FILTERED_WHICH;
    private static String PREFS_UP_VEHICLE_FILTERED_WHICH;
    private static String PREFS_UP_AGENCY_FILTERED_WHICH;
    private static String PREFS_PREV_AGENCY_FILTERED_WHICH;
    private static String PREFS_UP_LOCATION_FILTERED_WHICH;
    private static String PREFS_PREV_LOCATION_FILTERED_WHICH;
    private static String PREFS_UP_COUNTRY_FILTERED_WHICH;
    private static String PREFS_PREV_COUNTRY_FILTERED_WHICH;
    private static String PREFS_PREV_VEHICLE_FILTERED_ARRAY;
    private static String PREFS_UP_VEHICLE_FILTERED_ARRAY;
    private static String PREFS_UP_AGENCY_FILTERED_ARRAY;
    private static String PREFS_PREV_AGENCY_FILTERED_ARRAY;
    private static String PREFS_UP_LOCATION_FILTERED_ARRAY;
    private static String PREFS_PREV_LOCATION_FILTERED_ARRAY;
    private static String PREFS_UP_COUNTRY_FILTERED_ARRAY;
    private static String PREFS_PREV_COUNTRY_FILTERED_ARRAY;
    private static String PREFS_FILTER_VEHICLE;
    private static String PREFS_FILTER_AGENCY;
    private static String PREFS_FILTER_COUNTRY;
    private static String PREFS_SWITCH_NASA;
    private static String PREFS_SWITCH_SPACEX;
    private static String PREFS_SWITCH_ROSCOSMOS;
    private static String PREFS_SWITCH_ULA;
    private static String PREFS_SWITCH_ARIANE;
    private static String PREFS_SWITCH_CASC;
    private static String PREFS_SWITCH_ISRO;
    private static String PREFS_SWITCH_PLES;
    private static String PREFS_SWITCH_VAN;
    private static String PREFS_SWITCH_CAPE;
    private static String PREFS_SWITCH_KSC;
    private static String PREFS_SWITCH_ALL;
    private static String PREFS_CALENDAR_STATUS;
    private static String PREFS_NO_GO_SWITCH;
    private static String PREFS_TBD_SWITCH;
    private static String PREFS_PERSIST_LAST_SWITCH;
    private static String PREFS_VERSION_CODE;


    static {

        PREFS_NAME = "SPACE_LAUNCH_NOW_SWITCH_PREFS";
        PREFS_VERSION_CODE = "VERSION_CODE";
        PREFS_NIGHT_MODE_STATUS = "NIGHT_MODE_STATUS";
        PREFS_NIGHT_MODE_START = "NIGHT_MODE_START";
        PREFS_NIGHT_MODE_END = "NIGHT_MODE_END";
        PREFS_PREV_FILTERED_DATE = "PREV_FILTERED_DATE";
        PREFS_PREV_FILTERED = "PREV_FILTERED";
        PREFS_UP_FILTERED = "UP_FILTERED";
        PREFS_FILTER_VEHICLE = "FILTER_VEHICLE";
        PREFS_FILTER_AGENCY = "FILTER_AGENCY";
        PREFS_FILTER_COUNTRY = "FILTER_COUNTRY";
        PREFS_SWITCH_NASA = "SWITCH_NASA";
        PREFS_SWITCH_SPACEX = "SWITCH_SPACEX";
        PREFS_SWITCH_ROSCOSMOS = "SWITCH_ROSCOSMOS";
        PREFS_SWITCH_ULA = "SWITCH_ULA";
        PREFS_SWITCH_ARIANE = "SWITCH_ARIANE";
        PREFS_SWITCH_CASC = "SWITCH_CASC";
        PREFS_SWITCH_ISRO = "SWITCH_ISRO";
        PREFS_SWITCH_CAPE = "SWITCH_CAPE";
        PREFS_SWITCH_VAN = "SWITCH_VAN";
        PREFS_SWITCH_KSC = "SWITCH_KSC";
        PREFS_SWITCH_PLES = "SWITCH_PLES";
        PREFS_SWITCH_ALL = "SWITCH_ALL";
        PREFS_PREV_VEHICLE_FILTERED_WHICH = "PREV_VEHICLE_FILTERED_WHICH";
        PREFS_PREV_AGENCY_FILTERED_WHICH = "PREV_AGENCY_FILTERED_WHICH";
        PREFS_PREV_LOCATION_FILTERED_WHICH = "PREV_LOCATION_FILTERED_WHICH";
        PREFS_PREV_COUNTRY_FILTERED_WHICH = "PREV_COUNTRY_FILTERED_WHICH";
        PREFS_UP_COUNTRY_FILTERED_WHICH = "UP_COUNTRY_FILTERED_WHICH";
        PREFS_UP_VEHICLE_FILTERED_WHICH = "UP_VEHICLE_FILTERED_WHICH";
        PREFS_UP_AGENCY_FILTERED_WHICH = "UP_AGENCY_FILTERED_WHICH";
        PREFS_UP_LOCATION_FILTERED_WHICH = "UP_LOCATION_FILTERED_WHICH";
        PREFS_PREV_VEHICLE_FILTERED_ARRAY = "PREV_VEHICLE_FILTERED_ARRAY";
        PREFS_PREV_AGENCY_FILTERED_ARRAY = "PREV_AGENCY_FILTERED_ARRAY";
        PREFS_PREV_LOCATION_FILTERED_ARRAY = "PREV_LOCATION_FILTERED_ARRAY";
        PREFS_PREV_COUNTRY_FILTERED_ARRAY = "PREV_COUNTRY_FILTERED_ARRAY";
        PREFS_UP_COUNTRY_FILTERED_ARRAY = "UP_COUNTRY_FILTERED_ARRAY";
        PREFS_UP_VEHICLE_FILTERED_ARRAY = "UP_VEHICLE_FILTERED_ARRAY";
        PREFS_UP_AGENCY_FILTERED_ARRAY = "UP_AGENCY_FILTERED_ARRAY";
        PREFS_UP_LOCATION_FILTERED_ARRAY = "UP_LOCATION_FILTERED_ARRAY";
        PREFS_CALENDAR_STATUS = "CALENDAR_STATUS";
        PREFS_NO_GO_SWITCH = "NO_GO_SWITCH";
        PREFS_TBD_SWITCH = "TBD_SWITCH";
        PREFS_PERSIST_LAST_SWITCH = "PERSIST_LAST_SWITCH";
        INSTANCE = null;
    }

    private SwitchPreferences(Context context) {
        this.sharedPrefs = null;
        this.prefsEditor = null;
        this.appContext = context.getApplicationContext();
        appContext.getSharedPreferences(PREFS_NAME, 0).registerOnSharedPreferenceChangeListener(this);
    }

    public static SwitchPreferences getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SwitchPreferences(context);
        }
        return INSTANCE;
    }

    public static void create(Context context) {
        INSTANCE = new SwitchPreferences(context);
    }

    public void setVersionCode(int value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putInt(PREFS_VERSION_CODE, value);
        this.prefsEditor.apply();
    }

    public int getVersionCode() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getInt(PREFS_VERSION_CODE, 0);
    }

    public boolean getCalendarStatus() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_CALENDAR_STATUS, false);
    }


    public void setCalendarStatus(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_CALENDAR_STATUS, value);
        this.prefsEditor.apply();
    }

    public boolean isDateFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_PREV_FILTERED_DATE, false);
    }


    public void setDateFiltered(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_PREV_FILTERED_DATE, value);
        this.prefsEditor.apply();
    }

    public boolean getNightMode() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        return this.sharedPrefs.getBoolean("theme", false);
    }

    public boolean getDayNightAutoMode() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        return this.sharedPrefs.getBoolean("theme_auto", false);
    }

    public void setNightModeStatus(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_NIGHT_MODE_STATUS, value);
        this.prefsEditor.apply();
    }

    public void setNightModeStart(String value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_NIGHT_MODE_START, value);
        this.prefsEditor.apply();
    }

    public void setNightModeEnd(String value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_NIGHT_MODE_END, value);
        this.prefsEditor.apply();
    }


    public void setUpVehicleFiltered(Integer[] value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_VEHICLE_FILTERED_WHICH, Arrays.toString(value));
        this.prefsEditor.apply();
    }

    public Integer[] getUpVehicleFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return toIntArray(this.sharedPrefs.getString(PREFS_UP_VEHICLE_FILTERED_WHICH, ""));
    }

    public void setUpAgencyFiltered(Integer[] value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_AGENCY_FILTERED_WHICH, Arrays.toString(value));
        this.prefsEditor.apply();
    }

    public Integer[] getUpAgencyFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return toIntArray(this.sharedPrefs.getString(PREFS_UP_AGENCY_FILTERED_WHICH, ""));
    }

    public void setUpLocationFiltered(Integer[] value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_LOCATION_FILTERED_WHICH, Arrays.toString(value));
        this.prefsEditor.apply();
    }

    public Integer[] getUpLocationFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return toIntArray(this.sharedPrefs.getString(PREFS_UP_LOCATION_FILTERED_WHICH, ""));
    }

    public void setUpCountryFiltered(Integer[] value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_COUNTRY_FILTERED_WHICH, Arrays.toString(value));
        this.prefsEditor.apply();
    }

    public Integer[] getUpCountryFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return toIntArray(this.sharedPrefs.getString(PREFS_UP_COUNTRY_FILTERED_WHICH, ""));
    }

    public void resetAllUpFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_LOCATION_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_UP_AGENCY_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_UP_VEHICLE_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_UP_COUNTRY_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_UP_LOCATION_FILTERED_ARRAY, "");
        this.prefsEditor.putString(PREFS_UP_AGENCY_FILTERED_ARRAY, "");
        this.prefsEditor.putString(PREFS_UP_VEHICLE_FILTERED_ARRAY, "");
        this.prefsEditor.putString(PREFS_UP_COUNTRY_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void resetCountryUpFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_COUNTRY_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void resetVehicleUpFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_VEHICLE_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void resetAgencyUpFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_AGENCY_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void resetLocationUpFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_UP_LOCATION_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void resetCountryPrevFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_COUNTRY_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void resetVehiclePrevFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_VEHICLE_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void resetAgencyPrevFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_AGENCY_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void resetLocationPrevFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_LOCATION_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
    }

    public void setPrevVehicleFiltered(Integer[] value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_VEHICLE_FILTERED_WHICH, Arrays.toString(value));
        this.prefsEditor.apply();
    }

    public Integer[] getPrevVehicleFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return toIntArray(this.sharedPrefs.getString(PREFS_PREV_VEHICLE_FILTERED_WHICH, ""));
    }

    public void setPrevAgencyFiltered(Integer[] value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_AGENCY_FILTERED_WHICH, Arrays.toString(value));
        this.prefsEditor.apply();
    }

    public Integer[] getPrevAgencyFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return toIntArray(this.sharedPrefs.getString(PREFS_PREV_AGENCY_FILTERED_WHICH, ""));
    }

    public void setPrevLocationFiltered(Integer[] value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_LOCATION_FILTERED_WHICH, Arrays.toString(value));
        this.prefsEditor.apply();
    }

    public Integer[] getPrevLocationFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return toIntArray(this.sharedPrefs.getString(PREFS_PREV_LOCATION_FILTERED_WHICH, ""));
    }

    public void setPrevCountryFiltered(Integer[] value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_COUNTRY_FILTERED_WHICH, Arrays.toString(value));
        this.prefsEditor.apply();
    }

    public Integer[] getPrevCountryFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return toIntArray(this.sharedPrefs.getString(PREFS_PREV_COUNTRY_FILTERED_WHICH, ""));
    }

    public void resetAllPrevFilters(Context context) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_LOCATION_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_PREV_AGENCY_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_PREV_VEHICLE_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_PREV_COUNTRY_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_PREV_LOCATION_FILTERED_ARRAY, "");
        this.prefsEditor.putString(PREFS_PREV_AGENCY_FILTERED_ARRAY, "");
        this.prefsEditor.putString(PREFS_PREV_VEHICLE_FILTERED_ARRAY, "");
        this.prefsEditor.putString(PREFS_PREV_COUNTRY_FILTERED_ARRAY, "");
        this.prefsEditor.apply();
        if (listPreferences == null) {
            listPreferences = ListPreferences.getInstance(context);
        }
        listPreferences.setStartDate("1900-01-01");
    }

    public static Integer[] toIntArray(String input) {
        if (input.length() > 2 && !input.equals("")) {
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

    public void setPrevFiltered(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_PREV_FILTERED, value);
        this.prefsEditor.apply();
    }

    public boolean isPrevFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_PREV_FILTERED, false);
    }

    public void setUpFiltered(boolean value) {
        if (!value) {
            if (listPreferences == null) {
                listPreferences = ListPreferences.getInstance(appContext);
            }
            listPreferences.resetUpTitle();
        }
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_UP_FILTERED, value);
        this.prefsEditor.apply();
    }

    public boolean isUpFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_UP_FILTERED, false);
    }

    //FILTERS AND SWITCHES

    //Agency Filters
    public String getFilterAgency() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_FILTER_AGENCY, "");
    }

    public void setFilterAgency(String key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_FILTER_AGENCY, key);
        this.prefsEditor.apply();
    }

    //Vehicle Filters
    public String getFilterVehicle() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_FILTER_VEHICLE, "");
    }

    public void setFilterVehicle(String key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_FILTER_VEHICLE, key);
        this.prefsEditor.apply();
    }

    //Country Filters
    public String getFilterCountry() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_FILTER_COUNTRY, "");
    }

    public void setFilterCountry(String key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_FILTER_COUNTRY, key);
        this.prefsEditor.apply();
    }

    //Nasa Switch
    public boolean getSwitchVan() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_VAN, true);
    }

    public void setSwitchVan(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_VAN, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();
        try {
            tags.put("Van", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //Nasa Switch
    public boolean getSwitchKSC() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_KSC, true);
    }

    public void setSwitchKSC(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_KSC, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();
        try {
            tags.put("KSC", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }


    //Nasa Switch
    public boolean getSwitchPles() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_PLES, true);
    }

    public void setSwitchPles(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_PLES, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();
        try {
            tags.put("Ples", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //Nasa Switch
    public boolean getSwitchCape() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_CAPE, true);
    }

    public void setSwitchCape(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_CAPE, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();
        try {
            tags.put("Cape", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //Nasa Switch
    public boolean getSwitchNasa() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_NASA, true);
    }

    public void setSwitchNasa(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_NASA, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();

        try {
            tags.put("Nasa", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //SpaceX Switch
    public boolean getSwitchSpaceX() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_SPACEX, true);
    }

    public void setSwitchSpaceX(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_SPACEX, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();
        try {
            tags.put("SpaceX", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //Roscosmos Switch
    public boolean getSwitchRoscosmos() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_ROSCOSMOS, true);
    }

    public void setSwitchRoscosmos(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_ROSCOSMOS, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();
        try {
            tags.put("Roscosmos", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //ULA Switch
    public boolean getSwitchULA() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_ULA, true);
    }

    public void setSwitchULA(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_ULA, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();

        try {
            tags.put("ULA", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //Arianespace Switch
    public boolean getSwitchArianespace() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_ARIANE, true);
    }

    public void setSwitchArianespace(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_ARIANE, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();
        try {
            tags.put("Arianespace", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //CASC Switch
    public boolean getSwitchCASC() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_CASC, true);
    }

    public void setSwitchCASC(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_CASC, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();

        try {
            tags.put("CASC", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    //ISRO Switch
    public boolean getSwitchISRO() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_ISRO, true);
    }

    public void setSwitchISRO(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_ISRO, key);
        this.prefsEditor.apply();
        JSONObject tags = new JSONObject();

        try {
            tags.put("ISRO", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OneSignal.sendTags(tags);
    }

    //All Switch
    public boolean getAllSwitch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_ALL, true);
    }

    public void setAllSwitch(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_ALL, key);
        this.prefsEditor.apply();
        if (key) {
            resetSwitches();
        } else {
            JSONObject tags = new JSONObject();
            try {
                tags.put("ALL-Filter", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OneSignal.sendTags(tags);
        }
    }

    public void resetSwitches() {
        setSwitchNasa(true);
        setSwitchISRO(true);
        setSwitchRoscosmos(true);
        setSwitchSpaceX(true);
        setSwitchULA(true);
        setSwitchArianespace(true);
        setSwitchCASC(true);
        setSwitchCape(true);
        setSwitchKSC(true);
        setSwitchPles(true);
        setSwitchVan(true);
        JSONObject tags = new JSONObject();
        try {
            tags.put("Nasa", true);
            tags.put("ISRO", true);
            tags.put("Roscosmos", true);
            tags.put("ULA", true);
            tags.put("Arianespace", true);
            tags.put("KSC", true);
            tags.put("Ples", true);
            tags.put("Van", true);
            tags.put("SpaceX", true);
            tags.put("Cape", true);
            tags.put("CASC", true);
            tags.put("ALL-Filter", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (listPreferences != null) {
            if (key.equals(PREFS_CALENDAR_STATUS) && !getCalendarStatus()) {
                //Delete Events
            } else {

            }
        }
    }

    //Previous Get/Set

    public void setPrevCountryFilteredArray(ArrayList<String> array) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        this.prefsEditor.putString(PREFS_PREV_COUNTRY_FILTERED_ARRAY, json);
        this.prefsEditor.apply();
    }

    public void setPrevAgencyFilterArray(ArrayList<String> array) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        this.prefsEditor.putString(PREFS_PREV_AGENCY_FILTERED_ARRAY, json);
        this.prefsEditor.apply();
    }

    public void setPrevLocationFilteredArray(ArrayList<String> array) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        this.prefsEditor.putString(PREFS_PREV_LOCATION_FILTERED_ARRAY, json);
        this.prefsEditor.apply();
    }

    public void setPrevVehicleFilteredArray(ArrayList<String> array) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        this.prefsEditor.putString(PREFS_PREV_VEHICLE_FILTERED_ARRAY, json);
        this.prefsEditor.apply();
    }

    public ArrayList<String> getPrevCountryFilteredArray() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFS_PREV_COUNTRY_FILTERED_ARRAY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<String> getPrevAgencyFilteredArray() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFS_PREV_AGENCY_FILTERED_ARRAY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<String> getPrevLocationFilteredArray() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFS_PREV_LOCATION_FILTERED_ARRAY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<String> getPrevVehicleFilteredArray() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFS_PREV_VEHICLE_FILTERED_ARRAY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    //Up Get/Set

    public void setUpCountryFilteredArray(ArrayList<String> array) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        this.prefsEditor.putString(PREFS_UP_COUNTRY_FILTERED_ARRAY, json);
        this.prefsEditor.apply();
    }

    public void setUpAgencyFilterArray(ArrayList<String> array) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        this.prefsEditor.putString(PREFS_UP_AGENCY_FILTERED_ARRAY, json);
        this.prefsEditor.apply();
    }

    public void setUpLocationFilteredArray(ArrayList<String> array) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        this.prefsEditor.putString(PREFS_UP_LOCATION_FILTERED_ARRAY, json);
        this.prefsEditor.apply();
    }

    public void setUpVehicleFilteredArray(ArrayList<String> array) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        this.prefsEditor.putString(PREFS_UP_VEHICLE_FILTERED_ARRAY, json);
        this.prefsEditor.apply();
    }

    public ArrayList<String> getUpCountryFilteredArray() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFS_UP_COUNTRY_FILTERED_ARRAY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<String> getUpAgencyFilteredArray() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFS_UP_AGENCY_FILTERED_ARRAY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<String> getUpLocationFilteredArray() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFS_UP_LOCATION_FILTERED_ARRAY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<String> getUpVehicleFilteredArray() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFS_UP_VEHICLE_FILTERED_ARRAY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public int getWidgetID() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getInt("WIDGET_ID", 0);
    }

    public void setWidgetID(int key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putInt("WIDGET_ID", key);
        this.prefsEditor.apply();
    }

    public boolean getNoGoSwitch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_NO_GO_SWITCH, true);
    }

    public void setNoGoSwitch(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_NO_GO_SWITCH, key);
        this.prefsEditor.apply();
    }

    public boolean getPersistSwitch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_PERSIST_LAST_SWITCH, true);
    }

    public void setPersistLastSwitch(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_PERSIST_LAST_SWITCH, key);
        this.prefsEditor.apply();
    }

    public boolean getTBDLaunchSwitch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_TBD_SWITCH, true);
    }

    public void setTBDLaunchSwitch(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_TBD_SWITCH, key);
        this.prefsEditor.apply();
    }
}
