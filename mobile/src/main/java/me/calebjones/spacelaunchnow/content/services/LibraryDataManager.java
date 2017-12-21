package me.calebjones.spacelaunchnow.content.services;

import android.content.Context;

import me.calebjones.spacelaunchnow.content.data.DataClientManager;
import me.calebjones.spacelaunchnow.content.jobs.LibraryDataJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateJob;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

/**
 * This grabs rockets from LaunchLibrary
 */
public class LibraryDataManager extends BaseManager {

    private DataClientManager dataClientManager;

    public boolean isRunning(){
        return dataClientManager.isRunning();
    }

    public LibraryDataManager(Context context) {
        super(context);
        dataClientManager = new DataClientManager(context);
    }

    public void getFirstLaunchData(){
        if (sharedPref.getBoolean("background", true)) {
            scheduleLaunchUpdates();
        }

        dataClientManager.getUpcomingLaunchesAll();
//        dataClientManager.getLaunchesByDate("1950-01-01", Utils.getEndDate(1));
        dataClientManager.getAllMissions();
    }

    public void getAllLibraryData(){

    }

    public void  getAgencies(){
        dataClientManager.getAllAgencies();
    }

    public void getMissions(){
        dataClientManager.getAllMissions();
    }

    public void getPads(){
        dataClientManager.getAllPads();
    }

    public void getVehicleDetails(){
        dataClientManager.getVehicles(null);
        dataClientManager.getRockets();
        dataClientManager.getRocketFamily();
    }

    public void getVehicles(){
        dataClientManager.getRockets();
        dataClientManager.getRocketFamily();
    }


    public void updateNextLaunchMini() {
        Timber.v("Sending Update Next Launch intent.");
        dataClientManager.getNextUpcomingLaunchesMini();
    }

    public void updateLaunchById(int id){
        if (id > 0) {
            Timber.v("Updating detailLaunch id: %s", id);
            dataClientManager.getLaunchById(id);
        } else {
            Timber.v("Unable to update launch with id: %s", id);
        }
    }

    public void getUpcomingLaunches(){
        dataClientManager.getUpcomingLaunches();
    }

    public void getAllUpcomingLaunches(){
        if (sharedPref.getBoolean("background", true)) {
            scheduleLaunchUpdates();
        }

        dataClientManager.getUpcomingLaunchesAll();
    }

    public void getPreviousLaunches(String startDate, String endDate){
        if (startDate != null && endDate != null) {
            dataClientManager.getLaunchesByDate(startDate, endDate);
        } else {
            getPreviousLaunches();
        }
    }

    public void getPreviousLaunches(){
        dataClientManager.getLaunchesByDate("1950-01-01", Utils.getEndDate(1));
    }

    public void scheduleLaunchUpdates() {
        Timber.d("scheduleLaunchUpdates");
        UpdateJob.scheduleJob(context);
//        LibraryDataJob.scheduleJob();
    }
}
