package me.calebjones.spacelaunchnow.content.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.utils.CalendarUtil;

public class SwitchPreferences implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static SwitchPreferences INSTANCE;
    private static SharedPreferences SETTINGS;
    private static ListPreferences listPreferences;
    private SharedPreferences sharedPrefs;
    private Context appContext;
    SharedPreferences.Editor prefsEditor;

    public static String PREFS_NAME;
    public static String PREFS_NIGHT_MODE_STATUS;
    public static String PREFS_NIGHT_MODE_START;
    public static String PREFS_NIGHT_MODE_END;
    public static String PREFS_PREV_FILTERED;
    public static String PREFS_UP_FILTERED;
    public static String PREFS_PREV_VEHICLE_FILTERED_WHICH;
    public static String PREFS_UP_VEHICLE_FILTERED_WHICH;
    public static String PREFS_UP_AGENCY_FILTERED_WHICH;
    public static String PREFS_PREV_AGENCY_FILTERED_WHICH;
    public static String PREFS_UP_LOCATION_FILTERED_WHICH;
    public static String PREFS_PREV_LOCATION_FILTERED_WHICH;
    public static String PREFS_UP_COUNTRY_FILTERED_WHICH;
    public static String PREFS_PREV_COUNTRY_FILTERED_WHICH;
    public static String PREFS_FILTER_VEHICLE;
    public static String PREFS_FILTER_AGENCY;
    public static String PREFS_FILTER_COUNTRY;
    public static String PREFS_SWITCH_NASA;
    public static String PREFS_SWITCH_SPACEX;
    public static String PREFS_SWITCH_ROSCOSMOS;
    public static String PREFS_SWITCH_ULA;
    public static String PREFS_SWITCH_ARIANE;
    public static String PREFS_SWITCH_CASC;
    public static String PREFS_SWITCH_ISRO;
    public static String PREFS_SWITCH_PLES;
    public static String PREFS_SWITCH_VAN;
    public static String PREFS_SWITCH_CAPE;
    public static String PREFS_SWITCH_KSC;
    public static String PREFS_SWITCH_ALL;
    public static String PREFS_CALENDAR_STATUS;
    public static String PREFS_VERSION_CODE;


    static {

        PREFS_NAME = "SPACE_LAUNCH_NOW_SWITCH_PREFS";
        PREFS_VERSION_CODE = "VERSION_CODE";
        PREFS_NIGHT_MODE_STATUS = "NIGHT_MODE_STATUS";
        PREFS_NIGHT_MODE_START = "NIGHT_MODE_START";
        PREFS_NIGHT_MODE_END = "NIGHT_MODE_END";
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
        PREFS_CALENDAR_STATUS = "CALENDAR_STATUS";
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

    public boolean getNightMode() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        return this.sharedPrefs.getBoolean("theme", false);
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

    public void resetAllPrevFilters() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_PREV_LOCATION_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_PREV_AGENCY_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_PREV_VEHICLE_FILTERED_WHICH, "");
        this.prefsEditor.putString(PREFS_PREV_COUNTRY_FILTERED_WHICH, "");
        this.prefsEditor.apply();
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

    public void setPrevFiltered(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_PREV_FILTERED, value);
        this.prefsEditor.apply();
    }

    public boolean getPrevFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_PREV_FILTERED, false);
    }

    public void setUpFiltered(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_UP_FILTERED, value);
        this.prefsEditor.apply();
    }

    public boolean getUpFiltered() {
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
        if (key) {
            try {
                tags.put("Van", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("Van", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("KSC", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("KSC", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("Ples", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("Ples", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("Cape", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("Cape", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("Nasa", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("Nasa", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("SpaceX", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("SpaceX", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("Roscosmos", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("Roscosmos", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("ULA", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("ULA", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("Arianespace", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("Arianespace", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("CASC", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("CASC", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        if (key) {
            try {
                tags.put("ISRO", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tags.put("ISRO", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                tags.put("all", 0);
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
            tags.put("Nasa", 1);
            tags.put("ISRO", 1);
            tags.put("ROSCOSMOS", 1);
            tags.put("ULA", 1);
            tags.put("Arianespace", 1);
            tags.put("KSC", 1);
            tags.put("Ples", 1);
            tags.put("Van", 1);
            tags.put("SpaceX", 1);
            tags.put("all", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREFS_CALENDAR_STATUS) && !getCalendarStatus()) {
            CalendarUtil provider = new CalendarUtil();
            provider.deleteEvent(appContext, listPreferences.getNextLaunch());
        } else {
            //Get the list of launches
            Launch launch = listPreferences.getNextLaunch();
            CalendarUtil provider = new CalendarUtil();

            if (launch.getCalendarID() == null) {
                Integer id = provider.addEvent(appContext, launch);
                launch.setCalendarID(id);
                listPreferences.setNextLaunch(launch);
            } else {
                provider.updateEvent(appContext, launch);
            }
        }
    }
}
