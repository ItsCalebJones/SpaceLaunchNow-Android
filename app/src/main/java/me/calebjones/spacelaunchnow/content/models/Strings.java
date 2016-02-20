package me.calebjones.spacelaunchnow.content.models;


public final class Strings {
    public static final String ACTION_FAILURE_PREV_LAUNCHES = "FAILURE_PREV_LAUNCHES";
    public static final String ACTION_FAILURE_UP_LAUNCHES = "FAILURE_UP_LAUNCHES";
    public static final String ACTION_FAILURE_VEHICLE_DETAILS = "FAILURE_GET_ROCKETS";
    public static final String ACTION_FAILURE_MISSIONS = "FAILURE_GET_MISSION";
    public static final String ACTION_FAILURE_AGENCY = "FAILURE_GET_AGENCY";
    public static final String ACTION_FAILURE_VEHICLES = "FAILURE_GET_VEHICLES";

    public static final String ACTION_GET_PREV_LAUNCHES = "GET_PREV_LAUNCHES";
    public static final String ACTION_GET_UP_LAUNCHES = "GET_UP_LAUNCHES";
    public static final String ACTION_GET_VEHICLES_DETAIL = "GET_ROCKETS";
    public static final String ACTION_GET_ALL = "GET_ALL_UPDATES";
    public static final String ACTION_GET_AGENCY = "GET_AGENCY";
    public static final String ACTION_GET_VEHICLES = "GET_VEHICLES";

    public static final String ACTION_SUCCESS_VEHICLE_DETAILS = "SUCCESS_GET_ROCKETS";
    public static final String ACTION_SUCCESS_PREV_LAUNCHES = "SUCCESS_PREV_LAUNCHES";
    public static final String ACTION_SUCCESS_UP_LAUNCHES = "SUCCESS_UP_LAUNCHES";
    public static final String ACTION_SUCCESS_MISSIONS = "SUCCESS_GET_MISSIONS";
    public static final String ACTION_SUCCESS_AGENCY = "SUCCESS_GET_AGENCY";
    public static final String ACTION_SUCCESS_VEHICLES = "SUCCESS_GET_VEHICLES";

    public static final String ACTION_UPDATE_NEXT_LAUNCH = "UPDATE_NEXT_LAUNCHES";
    public static final String ACTION_UPDATE_UP_LAUNCHES = "UPDATE_UP_LAUNCHES";
    public static final String ACTION_UPDATE_PREV_LAUNCHES = "UPDATE_PREV_LAUNCHES";
    public static final String ACTION_UPDATE_AGENCY = "UPDATE_GET_AGENCY";
    public static final String ACTION_UPDATE_VEHICLES = "UPDATE_GET_VEHICLES";

    public static final String ACTION_CHECK_NEXT_LAUNCH_TIMER = "CHECK_NEXT_LAUNCH_TIMER";

    public static int NOTIF_ID = 568975;
    public static int NOTIF_ID_DAY = 568985;
    public static int NOTIF_ID_HOUR = 568995;

    public static String LAUNCH_URL = "https://launchlibrary.net/1.1.1/launch/next/1000";
    public static String MISSION_URL = "https://launchlibrary.net/1.1.1/mission?limit=1000&mode=verbose";
    public static String AGENCY_URL = "https://launchlibrary.net/1.1.1/agency?mode=verbose&limit=1000";
    public static String VEHICLE_URL = "https://launchlibrary.net/1.1.1/rocket?mode=verbose&limit=1000";
    public static String NEXT_URL = "https://launchlibrary.net/1.1.1/launch/next/1&mode=verbose";

    private Strings() {
    }
}
