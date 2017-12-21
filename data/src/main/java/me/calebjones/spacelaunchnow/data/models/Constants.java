package me.calebjones.spacelaunchnow.data.models;


public final class Constants {
    public static final String ACTION_GET_PREV_LAUNCHES = "GET_PREV_LAUNCHES";
    public static final String ACTION_GET_UP_LAUNCHES_ALL = "GET_UP_LAUNCHES_ALL";
    public static final String ACTION_GET_VEHICLES_DETAIL = "GET_ROCKETS";
    public static final String ACTION_GET_VEHICLES_FAMILY = "GET_ROCKET_FAMILY";
    public static final String ACTION_GET_ALL_DATA = "GET_ALL";
    public static final String ACTION_GET_MISSION = "GET_ALL_MISSIONS";
    public static final String ACTION_GET_AGENCY = "GET_AGENCY";
    public static final String ACTION_GET_LOCATION = "GET_LOCATION";
    public static final String ACTION_GET_ALL_LIBRARY_DATA = "GET_ALL_LIBRARY_DATA";
    public static final String ACTION_GET_VEHICLES = "GET_VEHICLES";
    public static final String ACTION_GET_PADS = "GET_PADS";
    public static final String ACTION_GET_UP_LAUNCHES = "GET_UP_LAUNCHES";
    public static final String ACTION_GET_NEXT_LAUNCH_MINI = "GET_NEXT_LAUNCH_MINI";
    public static final String ACTION_GET_NEXT_LAUNCHES = "GET_NEXT_LAUNCHES";
    public static final String ACTION_GET_UP_LAUNCHES_BY_ID = "GET_UP_LAUNCHES_BY_ID";
    public static final String ACTION_UPDATE_BACKGROUND = "UPDATE_UP_LAUNCHES_BACKGROUND";
    public static final String ACTION_UPDATE_LAUNCH = "UPDATE_LAUNCH";
    public static final String ACTION_CHECK_NEXT_LAUNCH_TIMER = "CHECK_NEXT_LAUNCH_TIMER";
    public static final String SYNC_NOTIFIERS = "SYNC_LAUNCH_NOTIFIERS";
    public static final String ACTION_GET_UP_LAUNCHES_MINI = "GET_UP_LAUNCHES_MINI";
    public static final String ACTION_UPDATE_LAUNCH_CARD = "UPDATE_LAUNCH_CARD";
    public static final String ACTION_UPDATE_WORD_TIMER = "UPDATE_WORD_TIMER";
    public static int NOTIF_ID = 568975;
    public static int NOTIF_ID_DAY = 568985;
    public static int NOTIF_ID_HOUR = 568995;

    public static int DB_SCHEMA_VERSION_1_5_5 = 188;
    public static int DB_SCHEMA_VERSION_1_5_6 = 189;
    public static int DB_SCHEMA_VERSION_1_8_0 = 320;
    public static int DB_SCHEMA_VERSION_1_8_1 = 322;
    public static int DB_SCHEMA_VERSION_1_8_2 = 323;
    public static int DB_SCHEMA_VERSION_2_0_0 = 324;

    public static String FORECAST_IO_BASE_URL = "https://api.forecast.io/";
    public static String API_BASE_URL = "https://api.spacelaunchnow.me/";
    public static String LIBRARY_BASE_URL = "https://launchlibrary.net/";
    public static String DEBUG_BASE_URL = "https://launchlibrary.net/";

    //These values are +1'd at runtime.
    public static final int DEFAULT_BLUR = 0;
    public static final int DEFAULT_RADIUS = 24;
    public static final int DEFAULT_DIM = 39;
    public static final int DEFAULT_GREY = 79;


    public static final String NAME_KEY = "me.calebjones.spacelaunchnow.wear.nextname";
    public static final String TIME_KEY = "me.calebjones.spacelaunchnow.wear.nexttime";
    public static final String DATE_KEY = "me.calebjones.spacelaunchnow.wear.nextdate";
    public static final String HOUR_KEY = "me.calebjones.spacelaunchnow.wear.hourmode";
    public static final String DYNAMIC_KEY = "me.calebjones.spacelaunchnow.wear.textdynamic";
    public static final String BACKGROUND_KEY = "me.calebjones.spacelaunchnow.wear.background";

    private Constants() {
    }
}
