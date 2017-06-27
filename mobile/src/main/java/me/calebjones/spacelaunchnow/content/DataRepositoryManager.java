package me.calebjones.spacelaunchnow.content;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.content.services.LibraryDataService;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import timber.log.Timber;

/**
 * This class is responsible for determining the freshness of the cache and requesting new data as needed.
 */

public class DataRepositoryManager {

    private Context context;

    public DataManager getDataManager() {
        return dataManager;
    }

    private DataManager dataManager;

    public DataRepositoryManager(Context context, DataManager dataManager) {
        this.context = context;
        this.dataManager = dataManager;
    }

    public DataRepositoryManager(Context context) {
        this.context = context;
        this.dataManager = new DataManager(context);
    }

    public void syncBackground() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);


        boolean wifiOnly = sharedPref.getBoolean("wifi_only", false);
        boolean dataSaver = sharedPref.getBoolean("data_saver", false);
        boolean wifiConnected = Connectivity.isConnectedWifi(context);
        Timber.i("Running syncBackground - Wifi Required: %s DataSaver: %s Wifi Connected: %s",
                wifiOnly, dataSaver, wifiConnected);

        if (wifiOnly && wifiConnected) {
            checkFullSync();
        } else if (dataSaver && !wifiConnected) {
            dataManager.getNextUpcomingLaunchesMini();
        } else {
            checkFullSync();
        }
    }

    private void checkFullSync() {
        Realm realm = Realm.getDefaultInstance();
        Timber.i("Checking full sync...");
        checkUpcomingLaunches(realm);
        checkMissions(realm);
        checkVehicles(realm);
        checkLibraryData(realm);
        realm.close();
    }

    private void checkLibraryData(Realm realm) {
        UpdateRecord record = realm.where(UpdateRecord.class)
                .equalTo("type", Constants.ACTION_GET_UP_LAUNCHES_ALL)
                .findFirst();
        if (record != null) {
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long daysMaxUpdate = TimeUnit.DAYS.toMillis(30);
            Timber.d("Time since last library sync %s", timeSinceUpdate);
            if (timeSinceUpdate > daysMaxUpdate) {
                Timber.d("%s greater then %s - updating library data.", timeSinceUpdate, daysMaxUpdate);
                Intent rocketIntent = new Intent(context, LibraryDataService.class);
                rocketIntent.setAction(Constants.ACTION_GET_ALL_LIBRARY_DATA);
                context.startService(rocketIntent);
            }
        } else {
            Timber.d("No record - checking library data.");
            Intent rocketIntent = new Intent(context, LibraryDataService.class);
            rocketIntent.setAction(Constants.ACTION_GET_ALL_LIBRARY_DATA);
            context.startService(rocketIntent);
        }
    }

    private void checkUpcomingLaunches(Realm realm) {
        UpdateRecord record = realm.where(UpdateRecord.class).equalTo("type", Constants.ACTION_GET_UP_LAUNCHES_ALL).findFirst();
        if (record != null) {
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long daysMaxUpdate = TimeUnit.DAYS.toMillis(1);
            Timber.d("Time since last upcoming launches sync %s", timeSinceUpdate);
            if (timeSinceUpdate > daysMaxUpdate) {
                Timber.d("%s greater then %s - updating library data.", timeSinceUpdate, daysMaxUpdate);
                dataManager.getNextUpcomingLaunches();
            }
        } else {
            Timber.d("No record - getting all launches");
            dataManager.getUpcomingLaunchesAll();
        }
    }

    //TODO move from LibraryDataService to DataManager
    private void checkMissions(Realm realm) {
        UpdateRecord record = realm.where(UpdateRecord.class).equalTo("type", Constants.ACTION_GET_MISSION).findFirst();
        if (record != null) {
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long daysMaxUpdate = TimeUnit.DAYS.toMillis(30);
            Timber.d("Time since last mission sync %s", timeSinceUpdate);
            if (timeSinceUpdate > daysMaxUpdate) {
                Timber.d("%s greater then %s - updating mission data.", timeSinceUpdate, daysMaxUpdate);
                Intent missionIntent = new Intent(context, LibraryDataService.class);
                missionIntent.setAction(Constants.ACTION_GET_MISSION);
                context.startService(missionIntent);
            }
        } else {
            Timber.d("No record - getting all missions.");
            Intent missionIntent = new Intent(context, LibraryDataService.class);
            missionIntent.setAction(Constants.ACTION_GET_MISSION);
            context.startService(missionIntent);
        }
    }

    //TODO move from LibraryDataService to DataManager
    private void checkVehicles(Realm realm) {
        UpdateRecord record = realm.where(UpdateRecord.class).equalTo("type", Constants.ACTION_GET_VEHICLES_DETAIL).findFirst();
        if (record != null) {
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long daysMaxUpdate = TimeUnit.DAYS.toMillis(30);
            Timber.d("Time since last vehicle sync %s", timeSinceUpdate);
            if (timeSinceUpdate > daysMaxUpdate) {
                Timber.d("%s greater then %s - updating vehicle data.", timeSinceUpdate, daysMaxUpdate);
                Intent rocketIntent = new Intent(context, LibraryDataService.class);
                rocketIntent.setAction(Constants.ACTION_GET_VEHICLES_DETAIL);
                context.startService(rocketIntent);
            }
        } else {
            Timber.d("No record - getting all vehicles.");
            Intent rocketIntent = new Intent(context, LibraryDataService.class);
            rocketIntent.setAction(Constants.ACTION_GET_VEHICLES_DETAIL);
            context.startService(rocketIntent);
        }
    }
}
