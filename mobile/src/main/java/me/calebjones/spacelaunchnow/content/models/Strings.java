package me.calebjones.spacelaunchnow.content.models;


public final class Strings {
    public static final String ACTION_SUCCESS_VEHICLE_DETAILS = "SUCCESS_GET_ROCKETS";
    public static final String ACTION_SUCCESS_PREV_LAUNCHES = "SUCCESS_PREV_LAUNCHES";
    public static final String ACTION_SUCCESS_UP_LAUNCHES = "SUCCESS_UP_LAUNCHES";
    public static final String ACTION_SUCCESS_MISSIONS = "SUCCESS_GET_MISSIONS";
    public static final String ACTION_SUCCESS_VEHICLES = "SUCCESS_GET_VEHICLES";
    public static final String ACTION_SUCCESS_AGENCY = "SUCCESS_GET_AGENCY";
    public static final String ACTION_SUCCESS_LOCATION = "SUCCESS_GET_LOCATION";
    public static final String ACTION_SUCCESS_PADS = "SUCCESS_GET_PADS";

    public static final String ACTION_FAILURE_PREV_LAUNCHES = "FAILURE_PREV_LAUNCHES";
    public static final String ACTION_FAILURE_UP_LAUNCHES = "FAILURE_UP_LAUNCHES";
    public static final String ACTION_FAILURE_VEHICLE_DETAILS = "FAILURE_GET_ROCKETS";
    public static final String ACTION_FAILURE_MISSIONS = "FAILURE_GET_MISSION";
    public static final String ACTION_FAILURE_AGENCY = "FAILURE_GET_AGENCY";
    public static final String ACTION_FAILURE_LOCATION = "FAILURE_GET_LOCATION";
    public static final String ACTION_FAILURE_VEHICLES = "FAILURE_GET_VEHICLES";
    public static final String ACTION_FAILURE_PADS = "FAILURE_GET_PADS";

    public static final String ACTION_GET_PREV_LAUNCHES = "GET_PREV_LAUNCHES";
    public static final String ACTION_GET_UP_LAUNCHES = "GET_UP_LAUNCHES";
    public static final String ACTION_GET_VEHICLES_DETAIL = "GET_ROCKETS";
    public static final String ACTION_GET_ALL_WIFI = "GET_ALL";
    public static final String ACTION_GET_ALL_NO_WIFI = "GET_ALL_NO_WIFI";
    public static final String ACTION_GET_MISSION = "GET_ALL_MISSIONS";
    public static final String ACTION_GET_AGENCY = "GET_AGENCY";
    public static final String ACTION_GET_LOCATION = "GET_LOCATION";
    public static final String ACTION_GET_VEHICLES = "GET_VEHICLES";

    public static final String ACTION_GET_PADS = "GET_PADS";
    public static final String ACTION_UPDATE_NEXT_LAUNCH = "UPDATE_NEXT_LAUNCH";
    public static final String ACTION_UPDATE_STORED_LAUNCH = "UPDATE_STORED_LAUNCH";
    public static final String ACTION_UPDATE_UP_LAUNCHES = "UPDATE_UP_LAUNCHES";
    public static final String ACTION_UPDATE_PREV_LAUNCHES = "UPDATE_PREV_LAUNCHES";

    public static final String ACTION_UPDATE_AGENCY = "UPDATE_GET_AGENCY";
    public static final String ACTION_UPDATE_VEHICLES = "UPDATE_GET_VEHICLES";
    public static final String ACTION_CHECK_NEXT_LAUNCH_TIMER = "CHECK_NEXT_LAUNCH_TIMER";
    public static final String ACTION_PROGRESS = "ACTION_PROGRESS_COUNTER";


    public static int NOTIF_ID = 568975;
    public static int NOTIF_ID_DAY = 568985;
    public static int NOTIF_ID_HOUR = 568995;

    public static String LAUNCH_URL = "https://launchlibrary.net/1.2/launch/next/1000&mode=verbose";
    public static String MISSION_URL = "https://launchlibrary.net/1.2/mission?limit=1000&mode=verbose";
    public static String AGENCY_URL = "https://launchlibrary.net/1.2/agency?mode=verbose&limit=1000";
    public static String VEHICLE_URL = "https://launchlibrary.net/1.2/rocket?mode=verbose&limit=1000";
    public static String NEXT_URL = "https://launchlibrary.net/1.2/launch?next=10&mode=verbose";
    public static String NEXT_URL_BY_ID = "https://launchlibrary.net/1.2/launch/%s?mode=verbose";

    public static String DEBUG_LAUNCH_URL = "https://launchlibrary.net/dev/launch/next/1000&mode=verbose";
    public static String DEBUG_MISSION_URL = "https://launchlibrary.net/dev/mission?limit=1000&mode=verbose";
    public static String DEBUG_AGENCY_URL = "https://launchlibrary.net/dev/agency?mode=verbose&limit=1000";
    public static String DEBUG_VEHICLE_URL = "https://launchlibrary.net/dev/rocket?mode=verbose&limit=1000";
    public static String DEBUG_NEXT_URL = "https://launchlibrary.net/dev/launch?next=10&mode=verbose";
    public static String DEBUG_NEXT_URL_BY_ID = "https://launchlibrary.net/dev/launch/%s?mode=verbose";

    public static String API_BASE_URL = "http://calebjones.me/app/";
    public static String LIBRARY_BASE_URL = "https://launchlibrary.net/";
    public static String DEBUG_BASE_URL = "https://launchlibrary.net/";

    private Strings() {
    }
}
