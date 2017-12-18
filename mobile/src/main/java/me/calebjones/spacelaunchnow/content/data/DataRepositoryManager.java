package me.calebjones.spacelaunchnow.content.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import timber.log.Timber;

/**
 * This class is responsible for determining the freshness of the cache and requesting new data as needed.
 */

public class DataRepositoryManager {

    private Context context;

    public DataClientManager getDataClientManager() {
        return dataClientManager;
    }

    private DataClientManager dataClientManager;


    public DataRepositoryManager(Context context, DataClientManager dataClientManager) {
        this.context = context;
        this.dataClientManager = dataClientManager;
    }

    public DataRepositoryManager(Context context) {
        this.context = context;
        this.dataClientManager = new DataClientManager(context);
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
            dataClientManager.getNextUpcomingLaunchesMini();
        } else {
            checkFullSync();
        }
    }

    private void checkFullSync() {
        Realm realm = Realm.getDefaultInstance();
        Timber.i("Checking full sync...");
        checkUpcomingLaunches(realm);
        realm.close();
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
                dataClientManager.getNextUpcomingLaunches();
            }
        } else {
            Timber.d("No record - getting all launches");
            dataClientManager.getUpcomingLaunchesAll();
        }
    }

    public void cleanDB() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Launch> launches = realm.where(Launch.class).findAll();
        for (final Launch launch: launches){
            if (launch.getName() == null){
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        launch.deleteFromRealm();
                    }
                });
            }
        }

    }
}
