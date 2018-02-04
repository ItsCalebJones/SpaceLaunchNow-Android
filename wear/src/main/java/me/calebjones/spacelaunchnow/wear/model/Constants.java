package me.calebjones.spacelaunchnow.wear.model;

public final class Constants {

    public static int AGENCY_ALL = 0;
    public static int AGENCY_NASA = 44;
    public static int AGENCY_SPACEX = 121;
    public static int AGENCY_ULA = 124;
    public static int AGENCY_CNSA = 88;
    public static int AGENCY_ROSCOSMOS = 63;

    // These constants are used by setUiState() to determine what information to display in the UI,
    // as this app reuses UI components for the various states of the app, which is dependent on
    // the state of the network.
    public static final int UI_STATE_REQUEST_NETWORK = 1;
    public static final int UI_STATE_REQUESTING_NETWORK = 2;
    public static final int UI_STATE_NETWORK_CONNECTED = 3;
    public static final int UI_STATE_CONNECTION_TIMEOUT = 4;

    public static final int NETWORK_CONNECTED = 10;
    public static final int NETWORK_UNAVAILABLE = 11;
    public static final int NETWORK_CONNECTED_SLOW = 12;

    public static final int BUTTON_REQUEST_HIGHBANDWIDTH = 100;
    public static final int BUTTON_ADD_WIFI = 101;


    private Constants() {

    }
}
