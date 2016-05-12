package me.calebjones.spacelaunchnow.content.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.calebjones.spacelaunchnow.content.models.Agency;
import me.calebjones.spacelaunchnow.content.models.CalendarItem;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.RocketDetails;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import timber.log.Timber;

public class ListPreferences {

    private static ListPreferences INSTANCE;
    private static SharedPreferences SETTINGS;
    private SharedPreferences sharedPrefs;
    private SwitchPreferences switchPreferences;
    private Context appContext;
    SharedPreferences.Editor prefsEditor;

    public static String PREFS_LIST_PREVIOUS;
    public static String PREFS_LIST_UPCOMING;
    public static String PREFS_LIST_CALENDAR;
    public static String PREFS_LIST_PREVIOUS_FILTERED;
    public static String PREFS_LIST_UPCOMING_FILTERED;
    public static String PREFS_LIST_AGENCY;
    public static String PREFS_LIST_VEHICLES;
    public static String PREFS_MISSION_LIST_UPCOMING;
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
    public static String PREFS_LAST_UPCOMING_LAUNCH_UPDATE;
    public static String PREFS_LAST_PREVIOUS_LAUNCH_UPDATE;
    public static String PREFS_PREVIOUS_TITLE;
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
        PREFS_PREVIOUS_FIRST_BOOT = "IS_PREVIOUS_FIRST_BOOT";
        PREFS_UPCOMING_FIRST_BOOT = "IS_UPCOMING_FIRST_BOOT";
        PREFS_POSITION = "POSITION_VALUE";
        PREFS_LIST_UPCOMING = "LAUNCH_LIST_CALENDAR";
        PREFS_LIST_UPCOMING = "LAUNCH_LIST_UPCOMING";
        PREFS_LIST_PREVIOUS = "LAUNCH_LIST_PREVIOUS";
        PREFS_MISSION_LIST_UPCOMING = "MISSION_LIST";
        PREFS_POSITION_LIST_UPCOMING = "POSITION_LIST_UPCOMING";
        PREFS_POSITION_LIST_PREVIOUS = "POSITION_LIST_PREVIOUS";
        PREFS_PREVIOUS_FILTER_TEXT = "PREVIOUS_FILTER_TEXT";
        PREFS_UPCOMING_FILTER_TEXT = "UPCOMING_FILTER_TEXT";
        PREFS_UPCOMING_COUNT = "LAUNCH_LIST_COUNT";
        PREFS_LAST_UPCOMING_LAUNCH_UPDATE = "LAST_UPCOMING_LAUNCH_UPDATE";
        PREFS_LAST_PREVIOUS_LAUNCH_UPDATE = "LAST_PREVIOUS_LAUNCH_UPDATE";
        PREFS_PREVIOUS_TITLE = "CURRENT_YEAR_RANGE";
        PREFS_CURRENT_START_DATE = "CURRENT_START_DATE";
        PREFS_CURRENT_END_DATE = "CURRENT_END_DATE";
        PREFS_LAUNCH_LIST_NEXT = "LAUNCH_LIST_FAVS";
        PREFS_LIST_AGENCY = "LIST_AGENCY";
        PREFS_LIST_VEHICLES = "LIST_VEHICLES";
        PREFS_NEXT_LAUNCH = "NEXT_LAUNCH";
        PREFS_PREV_LAUNCH = "PREV_LAUNCH";
        PREFS_LIST_PREVIOUS_FILTERED = "LIST_PREVIOUS_FILTERED";
        PREFS_LIST_UPCOMING_FILTERED = "LIST_UPCOMING_FILTERED";
        PREFS_LAST_VEHICLE_UPDATE = "LAST_VEHICLE_UPDATE";
        PREFS_NEXT_LAUNCH_TIMESTAMP = "NEXT_LAUNCH_TIMESTAMP";
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

    public boolean getNightMode() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        return this.sharedPrefs.getBoolean("theme", false);
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

    //Set methods for storing data.
    public void setListCalendarItems(List<CalendarItem> items) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        this.prefsEditor.putString(PREFS_LIST_CALENDAR, gson.toJson(items));
        this.prefsEditor.apply();
    }


    //Set methods for storing data.
    public void setNextLaunches(List<Launch> launches) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        this.prefsEditor.putString(PREFS_LAUNCH_LIST_NEXT, gson.toJson(launches));
        this.prefsEditor.apply();
    }

    public void setNextLaunch(Launch launch) {
        if (launch.getMissions() != null & launch.getMissions().size() > 0) {
            launch.getMissions().get(0).setTypeName(getMissionTypeByID(launch.getMissions().get(0).getId()));
        }
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        this.prefsEditor.putString(PREFS_NEXT_LAUNCH, gson.toJson(launch));
        this.prefsEditor.apply();
    }

    public void setNextLaunchTimestamp(int timestamp) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putInt(PREFS_NEXT_LAUNCH_TIMESTAMP, timestamp);
        this.prefsEditor.apply();
    }

    public void setPrevLaunch(Launch launch) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        this.prefsEditor.putString(PREFS_PREV_LAUNCH, gson.toJson(launch));
        this.prefsEditor.apply();
    }

    public void setUpComingLaunches(List<Launch> launches) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LIST_UPCOMING, gson.toJson(launches));
        this.prefsEditor.apply();

//        List<Launch> goList = new ArrayList<>();
//        for (int i = 0; i < launches.size(); i++) {
//            if (launches.get(i).getStatus() == 1) {
//                goList.add(launches.get(i));
//            }
//        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext);
        int size = Integer.parseInt(sharedPref.getString("upcoming_value", "5"));
        launches = filterLaunches(launches);
        if (launches.size() > size) {
            launches = launches.subList(0, size);
        }
        setNextLaunches(launches);
    }

    public void setMissionList(List<Mission> missions) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_MISSION_LIST_UPCOMING, gson.toJson(missions));
        this.prefsEditor.apply();
    }

    public void setPreviousLaunches(List<Launch> launches) {
        Timber.d("ListPreferences - setPrevious list:  %s ", launches.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LIST_PREVIOUS, gson.toJson(launches));
        this.prefsEditor.apply();
    }

    public void setPreviousLaunchesFiltered(List<Launch> launches) {
        Timber.d("ListPreferences - setPreviousFiltered list:  %s ", launches.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LIST_PREVIOUS_FILTERED, gson.toJson(launches));
        this.prefsEditor.apply();
    }

    public void setUpcomingLaunchesFiltered(List<Launch> launches) {
        Timber.d("ListPreferences - setUpcomingFiltered list:  %s ", launches.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LIST_UPCOMING_FILTERED, gson.toJson(launches));
        this.prefsEditor.apply();
    }


    public void setAgenciesList(List<Agency> agencies) {
        Timber.d("ListPreferences - setAgenciesList list:  %s ", agencies.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        this.prefsEditor.putString(PREFS_LIST_AGENCY, gson.toJson(agencies));
        this.prefsEditor.apply();
    }

    public void setVehiclesList(List<Rocket> rockets) {
        if (rockets != null) {

            Timber.d("ListPreferences - setVehiclesList list:  %s ", rockets.size());
            this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
            this.prefsEditor = this.sharedPrefs.edit();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeSpecialFloatingPointValues();
            gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
            Gson gson = gsonBuilder.setPrettyPrinting().create();

            this.prefsEditor.putString(PREFS_LIST_VEHICLES, gson.toJson(rockets));
            this.prefsEditor.apply();
        }
    }

    //Remove Methods from lists of data.
    public void removeUpcomingLaunches() {
        setUpComingLaunches(new ArrayList());
    }

    public void removeMissionsList() {
        setMissionList(new ArrayList());
    }

    public void removeFilteredList() {
        setPreviousLaunchesFiltered(new ArrayList());
    }

    public void removeUpFilteredList() {
        setUpcomingLaunchesFiltered(new ArrayList());
    }

    public void removePreviousLaunches() {
        setPreviousLaunches(new ArrayList());
    }

    public void removeAgencies() {
        setAgenciesList(new ArrayList());
    }

    public void removeVehicles() {
        setVehiclesList(new ArrayList());
    }

    //Get Methods for various lists of data.
    public Launch getNextLaunch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_NEXT_LAUNCH)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_NEXT_LAUNCH, null);

        Launch launch = gson.fromJson(jsonPreferences, Launch.class);

        return launch;
    }

    public int getNextLaunchTimestamp() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return sharedPref.getInt(PREFS_NEXT_LAUNCH_TIMESTAMP, 0);
    }


    public List<Launch> getLaunchesUpcoming() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_UPCOMING)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_UPCOMING, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public List<Launch> getLaunchesPrevious() {
        long time = System.currentTimeMillis();
        Timber.v("Starting time: %s", System.currentTimeMillis() - time);

        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_PREVIOUS)) {
            return null;
        }
        Timber.v("getSharedPreference - Ellapsed Time: %s", System.currentTimeMillis() - time);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        Timber.v("GsonBuilder - Ellapsed Time: %s", System.currentTimeMillis() - time);

        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_PREVIOUS, null);
        Timber.v("getString - Ellapsed Time: %s", System.currentTimeMillis() - time);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);
        Timber.v("productFromShared - Ellapsed Time: %s", System.currentTimeMillis() - time);

        if (productFromShared != null) {
            Timber.v("getLaunchesPrevious - Size: %s", productFromShared.size());
            Timber.v("Show Size - Ellapsed Time: %s", System.currentTimeMillis() - time);
        }
        return productFromShared;
    }

    public List<Launch> getLaunchesPreviousFiltered() {
        long time = System.currentTimeMillis();
        Timber.v("Starting time: %s", System.currentTimeMillis() - time);

        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_PREVIOUS)) {
            return null;
        }
        Timber.v("getSharedPreference - Ellapsed Time: %s", System.currentTimeMillis() - time);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        Timber.v("GsonBuilder - Ellapsed Time: %s", System.currentTimeMillis() - time);

        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_PREVIOUS_FILTERED, null);
        Timber.v("getString - Ellapsed Time: %s", System.currentTimeMillis() - time);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);
        Timber.v("productFromShared - Ellapsed Time: %s", System.currentTimeMillis() - time);
        return productFromShared;
    }

    public List<Launch> getLaunchesUpcomingFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_UPCOMING)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_UPCOMING_FILTERED, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);
        return productFromShared;
    }

    public List<Rocket> getVehicles() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_VEHICLES)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Rocket> vehicleList;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_VEHICLES, null);

        Type type = new TypeToken<List<Rocket>>() {
        }.getType();
        vehicleList = gson.fromJson(jsonPreferences, type);

        return vehicleList;
    }

    //TODO clean this up 1) Set images and rocket details into SharedPrefernces list instead of a DB.
    public List<Rocket> getRocketsByFamily(String familyname) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_VEHICLES)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Rocket> fullVehicleList;
        List<Rocket> sortedVehicleList = new ArrayList();
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_VEHICLES, null);

        Type type = new TypeToken<List<Rocket>>() {
        }.getType();
        fullVehicleList = gson.fromJson(jsonPreferences, type);

        int size = fullVehicleList.size();
        String query;
        DatabaseManager databaseManager = new DatabaseManager(appContext);

        for (int i = 0; i < size; i++) {
            if (fullVehicleList.get(i).getFamily().getName().contains(familyname)) {
                if (fullVehicleList.get(i).getName().contains("Space Shuttle")) {
                    query = "Space Shuttle";
                } else {
                    query = fullVehicleList.get(i).getName();
                }
                RocketDetails launchVehicle = databaseManager.getLaunchVehicle(query);
                Rocket rocket = new Rocket();
                rocket = fullVehicleList.get(i);
                if (rocket.getImageURL().contains("placeholder")) {
                    if (launchVehicle != null) {
                        rocket.setImageURL(launchVehicle.getImageURL());
                    } else {
                        rocket.setImageURL("");
                    }
                }
                sortedVehicleList.add(rocket);
            }
        }
        Collections.sort(sortedVehicleList, new Comparator<Rocket>() {
            public int compare(Rocket emp1, Rocket emp2) {
                return emp1.getName().compareToIgnoreCase(emp2.getName());
            }
        });
        return sortedVehicleList;
    }

    public List<Agency> getAgencies() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_AGENCY)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Agency> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_AGENCY, null);

        Type type = new TypeToken<List<Agency>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public List<CalendarItem> getListCalendarItems() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_CALENDAR)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        List<CalendarItem> items;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_CALENDAR, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        items = gson.fromJson(jsonPreferences, type);

        return items;
    }

    public List<Launch> getNextLaunches() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LAUNCH_LIST_NEXT)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        List<Launch> favorites;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LAUNCH_LIST_NEXT, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        favorites = gson.fromJson(jsonPreferences, type);

        return favorites;
    }

    public List<Mission> getMissionList() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_UPCOMING)) {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Mission> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_MISSION_LIST_UPCOMING, null);

        Type type = new TypeToken<List<Mission>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        if (productFromShared != null) {
            Collections.sort(productFromShared, new Comparator<Mission>() {
                public int compare(Mission emp1, Mission emp2) {
                    return emp1.getName().compareToIgnoreCase(emp2.getName());
                }
            });

            return productFromShared;
        } else {
            return null;
        }
    }

    public String getMissionTypeByID(int id) {
        List<Mission> missionList = getMissionList();

        if (missionList != null && missionList.size() > 0) {
            int start = 0;
            int end = missionList.size() - 1;

            while (start <= end) {
                if (id == missionList.get(start).getId()) {
                    return missionList.get(start).getTypeName();
                } else if (id == missionList.get(end).getId()) {
                    return missionList.get(end).getTypeName();
                } else {
                    start++;
                    end--;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    //Get methods by ID
    public Launch getLaunchByID(Integer id) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_PREVIOUS)) {
            return null;
        }
        if (!this.sharedPrefs.contains(PREFS_LIST_UPCOMING)) {
            return null;
        }
        Launch launch = new Launch();

        //Get Previous Launches List
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Launch> launchList;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_PREVIOUS, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        launchList = gson.fromJson(jsonPreferences, type);

        Gson gsonUpcoming = gsonBuilder.setPrettyPrinting().create();
        List<Launch> launchListUpComing;
        SharedPreferences sharedPrefUpcoming = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferencesUpcoming = sharedPrefUpcoming.getString(PREFS_LIST_UPCOMING, null);

        Type typeUpcoming = new TypeToken<List<Launch>>() {
        }.getType();
        launchListUpComing = gsonUpcoming.fromJson(jsonPreferencesUpcoming, type);

        List<Launch> totalList = new ArrayList<>(launchList);
        totalList.addAll(launchListUpComing);

        int size = totalList.size();

        for (int i = 0; i < size; i++) {
            if (totalList.get(i).getId().equals(id)) {
                launch = totalList.get(i);
            }
        }
        return launch;
    }

    public Mission getMissionByID(Integer id) {
        Mission mission = new Mission();

        //Get Mission List
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Mission> missionList;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_MISSION_LIST_UPCOMING, null);

        Type type = new TypeToken<List<Mission>>() {
        }.getType();
        missionList = gson.fromJson(jsonPreferences, type);

        int size = missionList.size();

        for (int i = 0; i < size; i++) {
            if (missionList.get(i).getId().equals(id)) {
                mission = missionList.get(i);
            }
        }
        return mission;
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

    public void setStartDate(String startDate) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_CURRENT_START_DATE, startDate);
        this.prefsEditor.apply();
    }

    public String getStartDate() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_CURRENT_START_DATE, "1950-01-01");
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

    public void setPrevFilter(int type, ArrayList<String> key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);

        //Get Previous Launches List
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Launch> launchList;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String previous = sharedPref.getString(PREFS_LIST_PREVIOUS, null);
        String previousFiltered = sharedPref.getString(PREFS_LIST_PREVIOUS_FILTERED, null);

        Type mType;
        mType = new TypeToken<List<Launch>>() {
        }.getType();

        List<Launch> newList = new ArrayList<>();

        if (!this.switchPreferences.getPrevFiltered()) {
            Timber.v("Not filtered, setting filtered to true and retrieving full list");
            removeFilteredList();
            this.switchPreferences.setPrevFiltered(true);
            launchList = gson.fromJson(previous, mType);
        } else {
            Timber.v("Filtered, retrieving filtered list.");
            launchList = gson.fromJson(previousFiltered, mType);
        }

        int size = launchList.size();
        Timber.v("setPrevFilter - List size is %s", size);

        for (int x = 0; x < key.size(); x++) {
            switch (type) {
                //Agency
                case 0:
                    if (key.contains("NASA")) {
                        key.set(x, "44");
                    } else if (key.contains("SpaceX")) {
                        key.set(x, "121");
                    } else if (key.contains("ROSCOSMOS")) {
                        key.set(x, "63");
                    } else if (key.contains("ULA")) {
                        key.set(x, "124");
                    } else if (key.contains("Arianespace")) {
                        key.set(x, "115");
                    } else if (key.contains("CASC")) {
                        key.set(x, "88");
                    } else if (key.contains("ISRO")) {
                        key.set(x, "31");
                    }

                    for (int i = 0; i < size; i++) {
                        Launch launch = null;
                        int id = Integer.parseInt(key.get(x));
                        if (launchList.get(i).getRocket().getAgencies() != null) {
                            int agencySize = launchList.get(i).getRocket().getAgencies().size();
                            for (int a = 0; a < agencySize; a++) {
                                if (launchList.get(i).getRocket().getAgencies().get(a).getId() == id) {
                                    launch = launchList.get(i);
                                    Timber.v("Adding filtered item %s", launch.getRocket().getAgencies().get(a).getName());
                                }
                            }
                            if (launchList.get(i).getLocation().getPads() != null) {
                                int padSize = launchList.get(i).getLocation().getPads().size();
                                for (int a = 0; a < padSize; a++) {
                                    if (launchList.get(i).getLocation().getPads().get(a).getAgencies() != null) {
                                        int agencySizeLocation = launchList.get(i).getLocation().getPads().get(a).getAgencies().size();
                                        for (int b = 0; b < agencySizeLocation; b++) {
                                            if (launchList.get(i).getLocation().getPads().get(a).getAgencies().get(b).getId() == id) {
                                                launch = launchList.get(i);
                                                Timber.v("Adding filtered item %s", launch.getLocation().getPads().get(a).getAgencies().get(b).getName());
                                                b = agencySizeLocation;
                                                a = padSize;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (launch != null) {
                            newList.add(launch);
                        }
                    }
                    if (newList.size() != 0) {
                        Timber.v("Saving list - size is %s", newList.size());
                    }
                    break;
                //Vehicle
                case 1:
                    for (int i = 0; i < size; i++) {
                        if (launchList.get(i).getRocket() != null) {
                            if (launchList.get(i).getRocket().getName().contains(key.get(x))) {
                                newList.add(launchList.get(i));
                            }
                        }
                    }
                    break;
                //Country
                case 2:

                    //If its not 'Multi' then search by the Key
                    if (!key.get(x).contains("Multi")) {
                        for (int i = 0; i < size; i++) {
                            if (launchList.get(i).getLocation().getPads() != null) {
                                int padSize = launchList.get(i).getLocation().getPads().size();
                                for (int a = 0; a < padSize; a++) {
                                    if (launchList.get(i).getLocation().getPads().get(a)
                                            .getAgencies() != null) {
                                        int agencySize = launchList.get(i).getLocation()
                                                .getPads().get(a).getAgencies().size();
                                        for (int b = 0; b < agencySize; b++) {
                                            if (launchList.get(i).getLocation().getPads()
                                                    .get(a).getAgencies().get(b).getCountryCode()
                                                    .contains(key.get(x))) {
                                                newList.add(launchList.get(i));
                                                b = agencySize;
                                                a = padSize;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //Otherwise find launches where countrycode length is longer then six.
                    } else {
                        for (int i = 0; i < size; i++) {
                            if (launchList.get(i).getLocation().getPads() != null) {
                                int padSize = launchList.get(i).getLocation().getPads().size();
                                for (int a = 0; a < padSize; a++) {
                                    if (launchList.get(i).getLocation().getPads().get(a)
                                            .getAgencies() != null) {
                                        int agencySize = launchList.get(i).getLocation()
                                                .getPads().get(a).getAgencies().size();
                                        for (int b = 0; b < agencySize; b++) {
                                            if (launchList.get(i).getLocation().getPads()
                                                    .get(a).getAgencies().get(b).getCountryCode()
                                                    .length() > 6) {
                                                newList.add(launchList.get(i));
                                                b = agencySize;
                                                a = padSize;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 3:
                    if (key.get(x).contains("Canaveral")) {
                        key.set(x, "16");
                    } else if (key.get(x).contains("Kennedy")) {
                        key.set(x, "17");
                    } else if (key.get(x).contains("Vandenberg")) {
                        key.set(x, "18");
                    } else if (key.get(x).contains("Wallops")) {
                        key.set(x, "19");
                    } else if (key.get(x).contains("Jiuquan")) {
                        key.set(x, "1");
                    } else if (key.get(x).contains("Taiyuan")) {
                        key.set(x, "2");
                    } else if (key.get(x).contains("Xichang")) {
                        key.set(x, "86");
                    } else if (key.get(x).contains("Wechang")) {
                        key.set(x, "33");
                    } else if (key.get(x).contains("Kourou")) {
                        key.set(x, "3");
                    } else if (key.get(x).contains("Sriharikota")) {
                        key.set(x, "5");
                    } else if (key.get(x).contains("Kagoshima")) {
                        key.set(x, "8");
                    } else if (key.get(x).contains("Tanegashima")) {
                        key.set(x, "9");
                    } else if (key.get(x).contains("Baikonur")) {
                        key.set(x, "10");
                    } else if (key.get(x).contains("Plesetsk")) {
                        key.set(x, "11");
                    } else if (key.get(x).contains("Kapustin")) {
                        key.set(x, "12");
                    } else if (key.get(x).contains("Svobodney")) {
                        key.set(x, "13");
                    } else if (key.get(x).contains("Sea")) {
                        key.set(x, "15");
                    } else if (key.get(x).contains("Woomera")) {
                        key.set(x, "20");
                    } else if (key.get(x).contains("Kiatorete")) {
                        key.set(x, "24");
                    } else if (key.get(x).contains("Kodiak")) {
                        key.set(x, "32");
                    } else if (key.get(x).contains("Ohae")) {
                        key.set(x, "29");
                    } else {
                        Timber.v("Unable to find matching ID for key");
                        break;
                    }
                    for (int i = 0; i < size; i++) {
                        if (launchList.get(i).getLocation().getId().toString().equals(key.get(x))) {
                            newList.add(launchList.get(i));
                        }
                    }
                    break;
            }
        }
        Collections.sort(newList, new Comparator<Launch>() {
            public int compare(Launch m1, Launch m2) {
                return m2.getNetstamp() - m1.getNetstamp();
            }
        });
        this.setPreviousLaunchesFiltered(newList);
    }

    public void setUpFilter(int type, ArrayList<String> key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);

        //Get Previous Launches List
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        List<Launch> launchList;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String upcoming = sharedPref.getString(PREFS_LIST_UPCOMING, null);
        String upcomingFiltered = sharedPref.getString(PREFS_LIST_UPCOMING_FILTERED, null);

        Type mType;
        mType = new TypeToken<List<Launch>>() {
        }.getType();

        List<Launch> newList = new ArrayList<>();

        if (!this.switchPreferences.getUpFiltered()) {
            Timber.v("Not filtered, setting filtered to true and retrieving full list");
            removeUpFilteredList();
            this.switchPreferences.setUpFiltered(true);
            launchList = gson.fromJson(upcoming, mType);
        } else {
            Timber.v("Filtered, retrieving filtered list.");
            launchList = gson.fromJson(upcomingFiltered, mType);
        }

        int size = launchList.size();
        Timber.v("setUpFilter - List size is %s", size);

        for (int x = 0; x < key.size(); x++) {
            switch (type) {
                //Agency
                case 0:
                    if (key.contains("NASA")) {
                        key.set(x, "44");
                    } else if (key.contains("SpaceX")) {
                        key.set(x, "121");
                    } else if (key.contains("ROSCOSMOS")) {
                        key.set(x, "63");
                    } else if (key.contains("ULA")) {
                        key.set(x, "124");
                    } else if (key.contains("Arianespace")) {
                        key.set(x, "115");
                    } else if (key.contains("CASC")) {
                        key.set(x, "88");
                    } else if (key.contains("ISRO")) {
                        key.set(x, "31");
                    }

                    for (int i = 0; i < size; i++) {
                        Launch launch = null;
                        int id = Integer.parseInt(key.get(x));
                        if (launchList.get(i).getRocket().getAgencies() != null) {
                            int agencySize = launchList.get(i).getRocket().getAgencies().size();
                            for (int a = 0; a < agencySize; a++) {
                                if (launchList.get(i).getRocket().getAgencies().get(a).getId() == id) {
                                    launch = launchList.get(i);
                                    Timber.v("Adding filtered item %s", launch.getRocket().getAgencies().get(a).getName());
                                }
                            }
                            if (launchList.get(i).getLocation().getPads() != null) {
                                int padSize = launchList.get(i).getLocation().getPads().size();
                                for (int a = 0; a < padSize; a++) {
                                    if (launchList.get(i).getLocation().getPads().get(a).getAgencies() != null) {
                                        int agencySizeLocation = launchList.get(i).getLocation().getPads().get(a).getAgencies().size();
                                        for (int b = 0; b < agencySizeLocation; b++) {
                                            if (launchList.get(i).getLocation().getPads().get(a).getAgencies().get(b).getId() == id) {
                                                launch = launchList.get(i);
                                                Timber.v("Adding filtered item %s", launch.getLocation().getPads().get(a).getAgencies().get(b).getName());
                                                b = agencySizeLocation;
                                                a = padSize;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (launch != null) {
                            newList.add(launch);
                        }
                    }
                    if (newList.size() != 0) {
                        Timber.v("Saving list - size is %s", newList.size());
                    }
                    break;
                //Vehicle
                case 1:
                    if (key.get(x).contains("SLV")) {
                        key.set(x, "SLV");
                    } else if (key.get(x).contains("Long")) {
                        key.set(x, "Long");
                    }

                    for (int i = 0; i < size; i++) {
                        if (launchList.get(i).getRocket() != null) {
                            if (launchList.get(i).getRocket().getName().contains(key.get(x))) {
                                newList.add(launchList.get(i));
                            }
                        }
                    }
                    break;
                //Country
                case 2:
                    //If its not 'Multi' then search by the Key
                    if (!key.get(x).contains("Multi")) {
                        for (int i = 0; i < size; i++) {
                            if (launchList.get(i).getLocation().getPads() != null) {
                                int padSize = launchList.get(i).getLocation().getPads().size();
                                for (int a = 0; a < padSize; a++) {
                                    if (launchList.get(i).getLocation().getPads().get(a)
                                            .getAgencies() != null) {
                                        int agencySize = launchList.get(i).getLocation()
                                                .getPads().get(a).getAgencies().size();
                                        for (int b = 0; b < agencySize; b++) {
                                            if (launchList.get(i).getLocation().getPads()
                                                    .get(a).getAgencies().get(b).getCountryCode()
                                                    .contains(key.get(x))) {
                                                newList.add(launchList.get(i));
                                                b = agencySize;
                                                a = padSize;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //Otherwise find launches where countrycode length is longer then six.
                    } else {
                        for (int i = 0; i < size; i++) {
                            if (launchList.get(i).getLocation().getPads() != null) {
                                int padSize = launchList.get(i).getLocation().getPads().size();
                                for (int a = 0; a < padSize; a++) {
                                    if (launchList.get(i).getLocation().getPads().get(a)
                                            .getAgencies() != null) {
                                        int agencySize = launchList.get(i).getLocation()
                                                .getPads().get(a).getAgencies().size();
                                        for (int b = 0; b < agencySize; b++) {
                                            if (launchList.get(i).getLocation().getPads()
                                                    .get(a).getAgencies().get(b).getCountryCode()
                                                    .length() > 6) {
                                                newList.add(launchList.get(i));
                                                b = agencySize;
                                                a = padSize;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 3:
                    if (key.get(x).contains("Canaveral")) {
                        key.set(x, "16");
                    } else if (key.get(x).contains("Kennedy")) {
                        key.set(x, "17");
                    } else if (key.get(x).contains("Vandenberg")) {
                        key.set(x, "18");
                    } else if (key.get(x).contains("Wallops")) {
                        key.set(x, "19");
                    } else if (key.get(x).contains("Jiuquan")) {
                        key.set(x, "1");
                    } else if (key.get(x).contains("Taiyuan")) {
                        key.set(x, "2");
                    } else if (key.get(x).contains("Xichang")) {
                        key.set(x, "86");
                    } else if (key.get(x).contains("Wechang")) {
                        key.set(x, "33");
                    } else if (key.get(x).contains("Kourou")) {
                        key.set(x, "3");
                    } else if (key.get(x).contains("Sriharikota")) {
                        key.set(x, "5");
                    } else if (key.get(x).contains("Kagoshima")) {
                        key.set(x, "8");
                    } else if (key.get(x).contains("Tanegashima")) {
                        key.set(x, "9");
                    } else if (key.get(x).contains("Baikonur")) {
                        key.set(x, "10");
                    } else if (key.get(x).contains("Plesetsk")) {
                        key.set(x, "11");
                    } else if (key.get(x).contains("Kapustin")) {
                        key.set(x, "12");
                    } else if (key.get(x).contains("Svobodney")) {
                        key.set(x, "13");
                    } else if (key.get(x).contains("Sea")) {
                        key.set(x, "15");
                    } else if (key.get(x).contains("Woomera")) {
                        key.set(x, "20");
                    } else if (key.get(x).contains("Kiatorete")) {
                        key.set(x, "24");
                    } else if (key.get(x).contains("Kodiak")) {
                        key.set(x, "32");
                    } else if (key.get(x).contains("Ohae")) {
                        key.set(x, "29");
                    } else {
                        Timber.v("Unable to find matching ID for key");
                        break;
                    }
                    for (int i = 0; i < size; i++) {
                        if (launchList.get(i).getLocation().getId().toString().equals(key.get(x))) {
                            newList.add(launchList.get(i));
                        }
                    }
                    break;
            }
        }
        Collections.sort(newList, new Comparator<Launch>() {
            public int compare(Launch m1, Launch m2) {
                if (m1.getStartDate() == null || m2.getStartDate() == null)
                    return 1;
                return m1.getStartDate().compareTo(m2.getStartDate());
            }
        });
        this.setUpcomingLaunchesFiltered(newList);
    }

    public List<Launch> filterLaunches(List<Launch> launchesUpcoming) {
        List<Integer> agency = new ArrayList<>();
        List<String> location = new ArrayList<>();
        if (this.switchPreferences.getAllSwitch()) {
            return launchesUpcoming;
        }
        List<Launch> list = new ArrayList<>();
        if (this.switchPreferences.getSwitchNasa()) {
            agency.add(43);
        }
        if (this.switchPreferences.getSwitchSpaceX()) {
            agency.add(121);
        }
        if (this.switchPreferences.getSwitchRoscosmos()) {
            agency.add(63);
        }
        if (this.switchPreferences.getSwitchArianespace()) {
            agency.add(115);
        }
        if (this.switchPreferences.getSwitchCASC()) {
            agency.add(88);
        }
        if (this.switchPreferences.getSwitchULA()) {
            agency.add(124);
        }
        if (this.switchPreferences.getSwitchISRO()) {
            agency.add(31);
        }
        if (this.switchPreferences.getSwitchVan()) {
            location.add("Vandenberg");
        }
        if (this.switchPreferences.getSwitchCape()) {
            location.add("Cape");
        }
        if (this.switchPreferences.getSwitchKSC()) {
            location.add("Kennedy");
        }
        if (this.switchPreferences.getSwitchPles()) {
            location.add("Plesetek");
            location.add("Baikonur");
        }
        return searchForMatches(launchesUpcoming, list, agency, location);
    }

    private List<Launch> searchForMatches(List<Launch> launchesUpcoming, List<Launch> list, List<Integer> num, List<String> location) {
        int size = launchesUpcoming.size();
        for (int i = 0; i < size; i++) {
            Launch launch = launchesUpcoming.get(i);
            boolean found = false;
            if (launch.getRocket().getAgencies() != null) {
                int agencySize = launch.getRocket().getAgencies().size();
                for (int a = 0; a < agencySize; a++) {
                    if (num.contains(launch.getRocket().getAgencies().get(a).getId())) {
                        list.add(launch);
                        found = true;
                        break;
                    }
                }
            }
            if (!found && launch.getLocation().getPads().size() > 0) {
                int locationSize = launch.getLocation().getPads().size();
                for (int b = 0; b < locationSize; b++) {
                    int locationAgencySize = launch.getLocation().getPads().get(b).getAgencies().size();
                    for (int c = 0; c < locationAgencySize; c++) {
                        if (num.contains(launch.getLocation().getPads().get(b).getAgencies().get(c).getId())) {
                            list.add(launch);
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (!found && launch.getLocation() != null) {
                for (int d = 0; d < location.size(); d++) {
                    if (launch.getLocation().getName().toLowerCase().contains(location.get(d).toLowerCase())) {
                        list.add(launch);
                        break;
                    }
                }
            }
        }
        return list;
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
