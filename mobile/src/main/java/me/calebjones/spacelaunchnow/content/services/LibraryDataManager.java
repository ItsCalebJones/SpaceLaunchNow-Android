package me.calebjones.spacelaunchnow.content.services;

import android.content.Context;

import me.calebjones.spacelaunchnow.content.data.DataClientManager;
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
        dataClientManager.getUpcomingLaunches();
    }


    public void updateNextLaunchMini() {
        Timber.v("Sending Update Next Launch intent.");
        dataClientManager.getNextUpcomingLaunchesMini();
    }


    public void getPreviousLaunches(){
        dataClientManager.getLaunchesByDate("1950-01-01", Utils.getEndDate(1));
    }

}
