package me.calebjones.spacelaunchnow.content.services;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import me.calebjones.spacelaunchnow.content.data.DataManager;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.UpdateJob;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class LaunchDataService extends BaseService {

    public LaunchDataService() {
        super("LaunchDataService");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        listPreference = ListPreferences.getInstance(getApplicationContext());
        switchPreferences = SwitchPreferences.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public static void startActionSyncNotifiers(Context context) {
        Intent intent = new Intent(context, LaunchDataService.class);
        intent.setAction(Constants.SYNC_NOTIFIERS);
        context.startService(intent);
        Timber.v("Sending Sync Notifiers intent.");
    }

    public static void startActionUpdateNextLaunchMini(Context context) {
        Intent intent = new Intent(context, LaunchDataService.class);
        intent.setAction(Constants.ACTION_GET_NEXT_LAUNCH_MINI);
        context.startService(intent);
        Timber.v("Sending Update Next Launch intent.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Timber.d("LaunchDataService - Intent received:  %s ", intent.getAction());
            String action = intent.getAction();

            // Create a new empty instance of Realm
            DataManager dataManager = new DataManager(this);

            //Usually called on first detailLaunch
            if (Constants.ACTION_GET_ALL_DATA.equals(action)) {
                Timber.v("Intent action received: %s", action);
                if (this.sharedPref.getBoolean("background", true)) {
                    scheduleLaunchUpdates();
                }

                dataManager.getUpcomingLaunchesAll();
                dataManager.getLaunchesByDate("1950-01-01", Utils.getEndDate(1));

                Intent libraryIntent = new Intent(this, LibraryDataService.class);
                libraryIntent.setAction(Constants.ACTION_GET_ALL_LIBRARY_DATA);
                startService(libraryIntent);

            } else if (Constants.ACTION_UPDATE_LAUNCH.equals(action)) {
                int id = intent.getIntExtra("launchID", 0);
                if (id > 0) {
                    Timber.v("Updating detailLaunch id: %s", id);
                    dataManager.getLaunchById(id);
                }

                // Called from NextLaunchFragment
            } else if (Constants.ACTION_GET_UP_LAUNCHES_ALL.equals(action)) {

                Timber.v("Intent action received: %s", action);
                if (this.sharedPref.getBoolean("background", true)) {
                    scheduleLaunchUpdates();
                }

                dataManager.getUpcomingLaunchesAll();

                // Called from PrevLaunchFragment
            } else if (Constants.ACTION_GET_PREV_LAUNCHES.equals(action)) {

                Timber.v("Intent action received: %s", action);
                if (intent.getStringExtra("startDate") != null && intent.getStringExtra("endDate") != null) {
                    dataManager.getLaunchesByDate(intent.getStringExtra("startDate"), intent.getStringExtra("endDate"), 0);
                } else {
                    dataManager.getLaunchesByDate("1950-01-01", Utils.getEndDate(1), 0);
                }

            } else if (Constants.ACTION_GET_NEXT_LAUNCH_MINI.equals(action)) {

                Timber.v("Intent action received: %s", action);
                dataManager.getNextUpcomingLaunchesMini();

            } else if (Constants.ACTION_GET_UP_LAUNCHES.equals(action)) {

                Timber.v("Intent action received: %s", action);
                dataManager.getUpcomingLaunches();

            } else if (Constants.SYNC_NOTIFIERS.equals(action)) {

                Timber.v("Intent action received: %s", action);
                dataManager.getDataSaver().syncNotifiers();

            } else if (Constants.ACTION_GET_VEHICLES_DETAIL.equals(action)){
                Intent libraryIntent = new Intent(this, LibraryDataService.class);
                libraryIntent.setAction(Constants.ACTION_GET_VEHICLES_DETAIL);
                startService(libraryIntent);
            } else {
                Timber.v("Unknown action received: %s", action);
            }
            Timber.v("Finished!");
        }
    }

    public void scheduleLaunchUpdates() {
        Timber.d("LaunchDataService - scheduleLaunchUpdates");
        UpdateJob.scheduleJob(this);
    }
}
