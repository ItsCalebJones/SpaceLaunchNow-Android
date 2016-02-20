package me.calebjones.spacelaunchnow.content.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.calebjones.spacelaunchnow.content.models.Agency;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.RocketDetails;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import timber.log.Timber;

public class SharedPreference {

    private static SharedPreference INSTANCE;
    private static SharedPreferences SETTINGS;
    private SharedPreferences sharedPrefs;
    private Context appContext;
    SharedPreferences.Editor prefsEditor;

    public static String PREFS_LIST_PREVIOUS;
    public static String PREFS_LIST_PREVIOUS_FILTERED;
    public static String PREFS_LIST_UPCOMING;
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
    public static String PREFS_NIGHT_MODE_STATUS;
    public static String PREFS_NIGHT_MODE_START;
    public static String PREFS_NIGHT_MODE_END;
    public static String PREFS_LAST_UPCOMING_LAUNCH_UPDATE;
    public static String PREFS_LAST_PREVIOUS_LAUNCH_UPDATE;
    public static String PREFS_PREVIOUS_TITLE;
    public static String PREFS_CURRENT_START_DATE;
    public static String PREFS_CURRENT_END_DATE;
    public static String PREFS_LAUNCH_LIST_NEXT;
    public static String PREFS_NEXT_LAUNCH;
    public static String PREFS_PREV_LAUNCH;
    public static String PREFS_PREV_FILTERED;
    public static String PREFS_DEBUG;
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
    public static String PREFS_SWITCH_CUSTOM;
    public static String PREFS_SWITCH_PLES;
    public static String PREFS_SWITCH_VAN;
    public static String PREFS_SWITCH_CAPE;
    public static String PREFS_SWITCH_KSC;
    public static String PREFS_CUSTOM_STRING;


    static {
        PREFS_NAME = "SPACE_LAUNCH_NOW_PREFS";
        PREFS_FIRST_BOOT = "IS_FIRST_BOOT";
        PREFS_PREVIOUS_FIRST_BOOT = "IS_PREVIOUS_FIRST_BOOT";
        PREFS_UPCOMING_FIRST_BOOT = "IS_UPCOMING_FIRST_BOOT";
        PREFS_POSITION = "POSITION_VALUE";
        PREFS_LIST_UPCOMING = "LAUNCH_LIST_UPCOMING";
        PREFS_LIST_PREVIOUS = "LAUNCH_LIST_PREVIOUS";
        PREFS_MISSION_LIST_UPCOMING = "MISSION_LIST";
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
        PREFS_LAUNCH_LIST_NEXT = "LAUNCH_LIST_FAVS";
        PREFS_LIST_AGENCY = "LIST_AGENCY";
        PREFS_LIST_VEHICLES = "LIST_VEHICLES";
        PREFS_NEXT_LAUNCH = "NEXT_LAUNCH";
        PREFS_PREV_LAUNCH = "PREV_LAUNCH";
        PREFS_PREV_FILTERED = "PREV_FILTERED";
        PREFS_FILTER_VEHICLE = "FILTER_VEHICLE";
        PREFS_FILTER_AGENCY = "FILTER_AGENCY";
        PREFS_FILTER_COUNTRY = "FILTER_COUNTRY";
        PREFS_LIST_PREVIOUS_FILTERED = "LIST_PREVIOUS_FILTERED";
        PREFS_SWITCH_NASA = "SWITCH_NASA";
        PREFS_SWITCH_SPACEX = "SWITCH_SPACEX";
        PREFS_SWITCH_ROSCOSMOS = "SWITCH_ROSCOSMOS";
        PREFS_SWITCH_ULA = "SWITCH_ULA";
        PREFS_SWITCH_ARIANE = "SWITCH_ARIANE";
        PREFS_SWITCH_CASC = "SWITCH_CASC";
        PREFS_SWITCH_ISRO = "SWITCH_ISRO";
        PREFS_SWITCH_CUSTOM = "SWITCH_CUSTOM";
        PREFS_CUSTOM_STRING = "CUSTOM_STRING";
        PREFS_SWITCH_CAPE = "SWITCH_CAPE";
        PREFS_SWITCH_VAN = "SWITCH_VAN";
        PREFS_SWITCH_KSC = "SWITCH_KSC";
        PREFS_SWITCH_PLES = "SWITCH_PLES";
        PREFS_DEBUG = "DEBUG";
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

        if (dark_theme) {
            return true;
        } else {
            return false;
        }
    }

    public void setDebugLaunch(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_DEBUG, value);
        this.prefsEditor.apply();
    }

    public boolean getDebugLaunch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_DEBUG, false);
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

    public void setNightModeStatus(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_NIGHT_MODE_STATUS, value);
        this.prefsEditor.apply();
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

    public void setFiltered(boolean value) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_PREV_FILTERED, value);
        this.prefsEditor.apply();
    }

    public boolean getFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_PREV_FILTERED, false);
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
    public void setNextLaunches(List<Launch> launches) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        this.prefsEditor.putString(PREFS_LAUNCH_LIST_NEXT, gson.toJson(launches));
        this.prefsEditor.apply();
    }

    public void setNextLaunches(Launch launch) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_NEXT_LAUNCH, gson.toJson(launch));
        this.prefsEditor.apply();
        List<Launch> list = getLaunchesUpcoming();
        if (list.size() > 0) {
            list.set(0, launch);
            setUpComingLaunches(list);
        }
    }

    public void setPrevLaunch(Launch launch) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_PREV_LAUNCH, gson.toJson(launch));
        this.prefsEditor.apply();
    }

    public void setUpComingLaunches(List<Launch> launches) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LIST_UPCOMING, gson.toJson(launches));
        this.prefsEditor.apply();
        List<Launch> goList = new ArrayList<>();
        for (int i = 0; i < launches.size(); i++) {
            if (launches.get(i).getStatus() == 1) {
                goList.add(launches.get(i));
            }
        }
        setNextLaunches(filterLaunches(goList));
    }

    public void setMissionList(List<Mission> missions) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_MISSION_LIST_UPCOMING, gson.toJson(missions));
        this.prefsEditor.apply();
    }

    public void setPreviousLaunches(List<Launch> launches) {
        Timber.d("SharedPreference - setPrevious list:  %s ", launches.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LIST_PREVIOUS, gson.toJson(launches));
        this.prefsEditor.apply();
    }

    public void setPreviousLaunchesFiltered(List<Launch> launches) {
        Timber.d("SharedPreference - setPreviousFiltered list:  %s ", launches.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_LIST_PREVIOUS_FILTERED, gson.toJson(launches));
        this.prefsEditor.apply();
    }


    public void setAgenciesList(List<Agency> agencies) {
        Timber.d("SharedPreference - setAgenciesList list:  %s ", agencies.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        this.prefsEditor.putString(PREFS_LIST_AGENCY, gson.toJson(agencies));
        this.prefsEditor.apply();
    }

    public void setVehiclesList(List<Rocket> rockets) {
        if (rockets != null) {

            Timber.d("SharedPreference - setVehiclesList list:  %s ", rockets.size());
            this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
            this.prefsEditor = this.sharedPrefs.edit();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeSpecialFloatingPointValues();
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
        Gson gson = new Gson();
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_NEXT_LAUNCH, null);

        Launch launch = gson.fromJson(jsonPreferences, Launch.class);

        return launch;
    }


    public List<Launch> getLaunchesUpcoming() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_UPCOMING)) {
            return null;
        }
        Gson gson = new Gson();
        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_UPCOMING, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public List<Launch> getLaunchesPrevious() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_PREVIOUS)) {
            return null;
        }
        Gson gson = new Gson();
        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_PREVIOUS, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        if (productFromShared != null) {
            Timber.v("getLaunchesPrevious - Size: %s", productFromShared.size());
        }
        return productFromShared;
    }

    public List<Launch> getLaunchesPreviousFiltered() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_PREVIOUS)) {
            return null;
        }
        Gson gson = new Gson();
        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_PREVIOUS_FILTERED, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        if (productFromShared != null) {
            Timber.v("getLaunchesPreviousFiltered - Size: %s", productFromShared.size());
        }
        return productFromShared;
    }

    public List<Rocket> getVehicles() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_VEHICLES)) {
            return null;
        }
        Gson gson = new Gson();
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
        Gson gson = new Gson();
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
                if (launchVehicle != null) {
                    rocket.setImageURL(launchVehicle.getImageURL());
                } else {
                    rocket.setImageURL("");
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
        Gson gson = new Gson();
        List<Agency> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_AGENCY, null);

        Type type = new TypeToken<List<Agency>>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public List<Launch> getNextLaunches() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LAUNCH_LIST_NEXT)) {
            return null;
        }
        Gson gson = new Gson();
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
        Gson gson = new Gson();
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
        Gson gson = new Gson();
        List<Launch> launchList;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LIST_PREVIOUS, null);

        Type type = new TypeToken<List<Launch>>() {
        }.getType();
        launchList = gson.fromJson(jsonPreferences, type);

        //Get Upcoming Launches List List
        Gson gsonUpcoming = new Gson();
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
        Gson gson = new Gson();
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

    public void setPrevFilter(int type, String key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);

        //Get Previous Launches List
        Gson gson = new Gson();
        List<Launch> launchList;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String previous = sharedPref.getString(PREFS_LIST_PREVIOUS, null);
        String previousFiltered = sharedPref.getString(PREFS_LIST_PREVIOUS_FILTERED, null);

        Type mType;
        mType = new TypeToken<List<Launch>>() {
        }.getType();

        List<Launch> newList = new ArrayList<>();

        if (!getFiltered()) {
            Timber.v("Not filtered, setting filtered to true and retrieving full list");
            removeFilteredList();
            setFiltered(true);
            launchList = gson.fromJson(previous, mType);
        } else {
            Timber.v("Filtered, retrieving filtered list.");
            launchList = gson.fromJson(previousFiltered, mType);
        }

        int size = launchList.size();
        Timber.v("setPrevFilter - List size is %s", size);


        switch (type) {
            //Agency
            case 0:
                this.prefsEditor.putString(PREFS_FILTER_AGENCY, key);
                for (int i = 0; i < size; i++) {
                    Launch launch = null;
                    int id = Integer.parseInt(key);
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
                    this.setPreviousLaunchesFiltered(newList);
                }
                break;
            //Vehicle
            case 1:
                this.prefsEditor.putString(PREFS_FILTER_VEHICLE, key);
                for (int i = 0; i < size; i++) {
                    if (launchList.get(i).getRocket() != null) {
                        if (launchList.get(i).getRocket().getName().contains(key)) {
                            newList.add(launchList.get(i));
                        }
                    }
                }
                this.setPreviousLaunchesFiltered(newList);
                break;
            //Country
            case 2:
                this.prefsEditor.putString(PREFS_FILTER_COUNTRY, key);

                //If its not 'Multi' then search by the Key
                if (!key.contains("Multi")) {
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
                                                .contains(key)) {
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
                this.setPreviousLaunchesFiltered(newList);
                break;
        }
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
    }

    //ISRO Switch
    public boolean getAllSwitch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(PREFS_SWITCH_CUSTOM, true);
    }

    public void setAllSwitch(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(PREFS_SWITCH_CUSTOM, key);
        this.prefsEditor.apply();
        if (key) {
            resetSwitches();
        }
    }

    //Custom Switch
    public String getCustomSearch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getString(PREFS_CUSTOM_STRING, "");
    }

    public void setCustomSearch(String key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putString(PREFS_CUSTOM_STRING, key);
        this.prefsEditor.apply();
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
    }

    public List<Launch> filterLaunches(List<Launch> launchesUpcoming) {
        List<Integer> agency = new ArrayList<>();
        List<String> location = new ArrayList<>();
        if (getAllSwitch()) {
            return launchesUpcoming;
        }
        List<Launch> list = new ArrayList<>();
        if (getSwitchNasa()) {
            agency.add(43);
        }
        if (getSwitchSpaceX()) {
            agency.add(121);
        }
        if (getSwitchRoscosmos()) {
            agency.add(63);
        }
        if (getSwitchArianespace()) {
            agency.add(115);
        }
        if (getSwitchCASC()) {
            agency.add(88);
        }
        if (getSwitchULA()) {
            agency.add(124);
        }
        if (getSwitchISRO()) {
            agency.add(31);
        }
        if (getSwitchVan()){
            location.add("Vandenberg");
        }
        if (getSwitchCape()){
            location.add("Cape");
        }
        if (getSwitchKSC()){
            location.add("Kennedy");
        }
        if (getSwitchPles()){
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
                for (int d = 0; d < location.size(); d++){
                    if(launch.getLocation().getName().toLowerCase().contains(location.get(d).toLowerCase())){
                        list.add(launch);
                        break;
                    }
                }
            }
        }
        return list;
    }
}
