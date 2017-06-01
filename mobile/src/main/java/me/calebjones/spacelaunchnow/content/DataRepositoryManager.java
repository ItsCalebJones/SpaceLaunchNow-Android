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
        checkUpcomingLaunches(realm);
        checkMissions(realm);
        checkVehicles(realm);
        checkLibraryData(realm);
        realm.close();
    }

    private void checkLibraryData(Realm realm) {
        UpdateRecord record = realm.where(UpdateRecord.class).equalTo("type", Constants.ACTION_GET_UP_LAUNCHES).findFirst();
        if (record != null) {
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long daysMaxUpdate = TimeUnit.DAYS.toMillis(30);
            if (timeSinceUpdate > daysMaxUpdate) {
                Intent rocketIntent = new Intent(context, LibraryDataService.class);
                rocketIntent.setAction(Constants.ACTION_GET_ALL_LIBRARY_DATA);
                context.startService(rocketIntent);
            }
        } else {
            Intent rocketIntent = new Intent(context, LibraryDataService.class);
            rocketIntent.setAction(Constants.ACTION_GET_ALL_LIBRARY_DATA);
            context.startService(rocketIntent);
        }
    }

    private void checkUpcomingLaunches(Realm realm) {
        UpdateRecord record = realm.where(UpdateRecord.class).equalTo("type", Constants.ACTION_GET_UP_LAUNCHES).findFirst();
        if (record != null) {
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long daysMaxUpdate = TimeUnit.DAYS.toMillis(7);
            long daysMinUpdate = TimeUnit.DAYS.toMillis(1);

            if (timeSinceUpdate > daysMaxUpdate) {
                dataManager.getUpcomingLaunchesAll();
            } else if (timeSinceUpdate > daysMinUpdate) {
                dataManager.getNextUpcomingLaunches();
            }
        } else {
            dataManager.getUpcomingLaunchesAll();
        }
    }

    private void checkMissions(Realm realm) {
        UpdateRecord record = realm.where(UpdateRecord.class).equalTo("type", Constants.ACTION_GET_MISSION).findFirst();
        if (record != null) {
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long daysMaxUpdate = TimeUnit.DAYS.toMillis(30);
            if (timeSinceUpdate > daysMaxUpdate) {
                Intent missionIntent = new Intent(context, LibraryDataService.class);
                missionIntent.setAction(Constants.ACTION_GET_MISSION);
                context.startService(missionIntent);
            }
        } else {
            Intent missionIntent = new Intent(context, LibraryDataService.class);
            missionIntent.setAction(Constants.ACTION_GET_MISSION);
            context.startService(missionIntent);
        }
    }

    private void checkVehicles(Realm realm) {
        UpdateRecord record = realm.where(UpdateRecord.class).equalTo("type", Constants.ACTION_GET_VEHICLES_DETAIL).findFirst();
        if (record != null) {
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long daysMaxUpdate = TimeUnit.DAYS.toMillis(30);
            if (timeSinceUpdate > daysMaxUpdate) {
                Intent rocketIntent = new Intent(context, LibraryDataService.class);
                rocketIntent.setAction(Constants.ACTION_GET_VEHICLES_DETAIL);
                context.startService(rocketIntent);
            }
        } else {
            Intent rocketIntent = new Intent(context, LibraryDataService.class);
            rocketIntent.setAction(Constants.ACTION_GET_VEHICLES_DETAIL);
            context.startService(rocketIntent);
        }
    }
}
