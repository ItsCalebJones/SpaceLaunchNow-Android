package me.calebjones.spacelaunchnow.content.database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    public static String PREFS_LAUNCH_LIST_FAVS;
    public static String PREFS_NEXT_LAUNCH;


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
        PREFS_LAUNCH_LIST_FAVS = "LAUNCH_LIST_FAVS";
        PREFS_LIST_AGENCY = "LIST_AGENCY";
        PREFS_LIST_VEHICLES = "LIST_VEHICLES";
        PREFS_NEXT_LAUNCH = "NEXT_LAUNCH";
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


    public void addFavLaunch(Launch launch) {
        List<Launch> rocketLaunches = getFavoriteLaunches();
        List<Launch> upcomingLaunches = getLaunchesUpcoming();
        if (rocketLaunches == null) {
            rocketLaunches = new ArrayList();
        }
        rocketLaunches.add(launch);

        int size = upcomingLaunches.size();

        for (int i = 0; i < size; i++) {
            if (upcomingLaunches.get(i).getId().equals(launch.getId())) {
                launch.setFavorite(true);
                upcomingLaunches.set(i, launch);
                setUpComingLaunches(upcomingLaunches);
            }
        }
        setFavLaunch(rocketLaunches);
    }

    //Set methods for storing data.
    public void setFavLaunch(List<Launch> launches) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        this.prefsEditor.putString(PREFS_LAUNCH_LIST_FAVS, gson.toJson(launches));
        this.prefsEditor.apply();
    }

    public void setNextLaunch(Launch launch){
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        this.prefsEditor.putString(PREFS_NEXT_LAUNCH, gson.toJson(launch));
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
        Timber.d("SharedPreference - setVehiclesList list:  %s ", rockets.size());
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        this.prefsEditor.putString(PREFS_LIST_VEHICLES, gson.toJson(rockets));
        this.prefsEditor.apply();
    }

    //Remove Methods from lists of data.
    public void removeUpcomingLaunches() {
        setUpComingLaunches(new ArrayList());
    }

    public void removeMissionsList() {
        setMissionList(new ArrayList());
    }

    public void removeFavLaunchAll() {
        setFavLaunch(new ArrayList());
    }

    public void removeFavLaunch(Launch launch) {
        List<Launch> launchList = getFavoriteLaunches();
        List<Launch> newList = new ArrayList<>();
        int size = launchList.size();
        for (int i = 0; i < size; i++) {
            if (!launchList.get(i).getId().equals(launch.getId())) {
                newList.add(launchList.get(i));
            }
        }
        setFavLaunch(newList);
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
    public Launch getNextLaunch(){
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LIST_UPCOMING)) {
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

        Type type = new TypeToken<List<Launch>>() {}.getType();
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

        Type type = new TypeToken<List<Launch>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

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

        Type type = new TypeToken<List<Rocket>>() {}.getType();
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

        Type type = new TypeToken<List<Rocket>>() {}.getType();
        fullVehicleList = gson.fromJson(jsonPreferences, type);

        int size = fullVehicleList.size();
        String query;
        DatabaseManager databaseManager = new DatabaseManager(appContext);

        for (int i = 0; i < size; i++) {
            if (fullVehicleList.get(i).getFamily().getName().contains(familyname)) {
                if (fullVehicleList.get(i).getName().contains("Space Shuttle")){
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
        Collections.sort(sortedVehicleList, new Comparator<Rocket>(){
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

        Type type = new TypeToken<List<Agency>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public List<Launch> getFavoriteLaunches() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        if (!this.sharedPrefs.contains(PREFS_LAUNCH_LIST_FAVS)) {
            return null;
        }
        Gson gson = new Gson();
        List<Launch> productFromShared;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_LAUNCH_LIST_FAVS, null);
        Type type = new TypeToken<List<Launch>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
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

        Type type = new TypeToken<List<Mission>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        Collections.sort(productFromShared, new Comparator<Mission>(){
            public int compare(Mission emp1, Mission emp2) {
                return emp1.getName().compareToIgnoreCase(emp2.getName());
            }
        });

        return productFromShared;
    }

    //Get methods by ID
    public Launch getLaunchByID(Integer id){
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

        Type type = new TypeToken<List<Launch>>() {}.getType();
        launchList = gson.fromJson(jsonPreferences, type);

        //Get Upcoming Launches List List
        Gson gsonUpcoming = new Gson();
        List<Launch> launchListUpComing;
        SharedPreferences sharedPrefUpcoming = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferencesUpcoming = sharedPrefUpcoming.getString(PREFS_LIST_UPCOMING, null);

        Type typeUpcoming = new TypeToken<List<Launch>>() {}.getType();
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

    public Mission getMissionByID(Integer id){
        Mission mission = new Mission();

        //Get Mission List
        Gson gson = new Gson();
        List<Mission> missionList;
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        String jsonPreferences = sharedPref.getString(PREFS_MISSION_LIST_UPCOMING, null);

        Type type = new TypeToken<List<Mission>>() {}.getType();
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

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String currentTime) throws ParseException {
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
    }
}
