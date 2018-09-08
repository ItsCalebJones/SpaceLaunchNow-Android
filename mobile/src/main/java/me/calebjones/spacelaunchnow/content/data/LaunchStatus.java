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

    public static int getLaunchStatusColor(Context context, Integer id) {
        switch (id) {
            case 1:
                //GO for launch
                return context.getResources().getColor(R.color.material_color_green_500);
            case 2:
                //NO GO for launch
                return context.getResources().getColor(R.color.material_color_red_500);
            case 3:
                //Success for launch
                return context.getResources().getColor(R.color.material_color_green_800);
            case 4:
                //Failure to launch
                return context.getResources().getColor((R.color.material_color_red_900));
            case 5:
                //Failure to launch
                return context.getResources().getColor((R.color.material_color_yellow_500));
            case 6:
                //Failure to launch
                return context.getResources().getColor(R.color.material_color_orange_500);
            case 7:
                //Failure to launch
                return context.getResources().getColor(R.color.material_color_amber_800);
            default:
                return context.getResources().getColor(R.color.material_color_purple_500);
        }
    }
}
