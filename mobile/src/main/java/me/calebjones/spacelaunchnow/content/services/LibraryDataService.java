package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.content.DataManager;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * This grabs rockets from LaunchLibrary
 */
public class LibraryDataService extends IntentService {

    private SharedPreferences sharedPref;
    private ListPreferences listPreference;

    private Realm mRealm;

    private Retrofit apiRetrofit;
    private Retrofit libraryRetrofit;

    public LibraryDataService() {
        super("LibraryDataService");
    }

    public void onCreate() {
        Timber.d("LibraryDataService - onCreate");

        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.listPreference = ListPreferences.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Create a new empty instance of Realm
        DataManager dataManager = new DataManager(this);

        if (intent != null) {
            Timber.d("LibraryDataService - Intent %s received!", intent.getAction());
            String action = intent.getAction();
            if (Constants.ACTION_GET_ALL_LIBRARY_DATA.equals(action)) {
                listPreference.setLastVehicleUpdate(System.currentTimeMillis());
                dataManager.getAllAgencies();
                dataManager.getAllMissions();
                dataManager.getAllLocations();
                dataManager.getAllPads();
                dataManager.getVehicles();
                dataManager.getRockets();
                dataManager.getRocketFamily();
            } else if (Constants.ACTION_GET_AGENCY.equals(action)) {
                dataManager.getAllAgencies();
            } else if (Constants.ACTION_GET_MISSION.equals(action)) {
                dataManager.getAllMissions();
            } else if (Constants.ACTION_GET_LOCATION.equals(action)) {
                dataManager.getAllLocations();
            } else if (Constants.ACTION_GET_PADS.equals(action)) {
                dataManager.getAllPads();
            } else if (Constants.ACTION_GET_VEHICLES_DETAIL.equals(action)) {
                listPreference.setLastVehicleUpdate(System.currentTimeMillis());
                dataManager.getVehicles();
                dataManager.getRockets();
                dataManager.getRocketFamily();
            } else if (Constants.ACTION_GET_VEHICLES.equals(action)) {
                dataManager.getRockets();
                dataManager.getRocketFamily();
            }
        }
    }
}
