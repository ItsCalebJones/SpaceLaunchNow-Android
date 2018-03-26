package me.calebjones.spacelaunchnow.content.data;

import android.content.Context;

import me.calebjones.spacelaunchnow.R;


public class LaunchStatus {

    public static String getLaunchStatusTitle(Context context, int statusId) {
        switch (statusId) {
            case 1:
                //GO for launch
                return context.getResources().getString(R.string.status_go);
            case 2:
                //NO GO for launch
                return context.getResources().getString(R.string.status_nogo);
            case 3:
                //Success for launch
                return context.getResources().getString(R.string.status_success);
            case 4:
                //Failure to launch
                return context.getResources().getString(R.string.status_failure);
            case 5:
                //Failure to launch
                return context.getResources().getString(R.string.status_hold);
            case 6:
                //Failure to launch
                return context.getResources().getString(R.string.status_in_flight);
            case 7:
                //Failure to launch
                return context.getResources().getString(R.string.status_partial_failure);
            default:
                return "Unknown Launch Status";
        }
    }
}
