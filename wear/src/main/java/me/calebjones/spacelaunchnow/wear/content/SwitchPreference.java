package me.calebjones.spacelaunchnow.wear.content;


import android.content.Context;
import android.content.SharedPreferences;


public class SwitchPreference {
    private SharedPreferences sharedPrefs;
    private Context appContext;
    private int complicationId;
    SharedPreferences.Editor prefsEditor;

    private static String PREFS_SWITCH_NASA;
    private static String PREFS_SWITCH_SPACEX;
    private static String PREFS_SWITCH_ROSCOSMOS;
    private static String PREFS_SWITCH_ULA;
    private static String PREFS_SWITCH_CNSA;
    private static String PREFS_SWITCH_ALL;
    private static String PREFS_CONFIGURED;
    private static String PREFS_NAME;

    static {
        PREFS_CONFIGURED = "COMPLICATION_CONFIGURED";
        PREFS_NAME = "SPACE_LAUNCH_NOW_WEAR_SWITCH_PREFS";
        PREFS_SWITCH_NASA = "SWITCH_NASA";
        PREFS_SWITCH_SPACEX = "SWITCH_SPACEX";
        PREFS_SWITCH_ROSCOSMOS = "SWITCH_ROSCOSMOS";
        PREFS_SWITCH_ULA = "SWITCH_ULA";
        PREFS_SWITCH_CNSA = "SWITCH_CNSA";
        PREFS_SWITCH_ALL = "SWITCH_ALL";
    }

    private SwitchPreference(Context context, int complicationId) {
        this.sharedPrefs = null;
        this.prefsEditor = null;
        this.appContext = context.getApplicationContext();
        this.complicationId = complicationId;
    }

    public static SwitchPreference getInstance(Context context, int complicationId) {
        return new SwitchPreference(context, complicationId);
    }

    public void setConfigured(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(complicationId + PREFS_CONFIGURED, key);
        this.prefsEditor.apply();
    }

    public boolean isConfigured() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(complicationId + PREFS_CONFIGURED, false);
    }

    //ULA Switch
    public boolean getSwitchULA() {
        return this.sharedPrefs.getBoolean(complicationId + PREFS_SWITCH_ULA, true);
    }


    public void setSwitchULA(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(complicationId + PREFS_SWITCH_ULA, key);
        this.prefsEditor.apply();
    }

    //Roscosmos Switch
    public boolean getSwitchRoscosmos() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(complicationId + PREFS_SWITCH_ROSCOSMOS, true);
    }

    public void setSwitchRoscosmos(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(complicationId + PREFS_SWITCH_ROSCOSMOS, key);
        this.prefsEditor.apply();
    }

    //SpaceX Switch
    public boolean getSwitchSpaceX() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(complicationId + PREFS_SWITCH_SPACEX, true);
    }

    public void setSwitchSpaceX(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(complicationId + PREFS_SWITCH_SPACEX, key);
        this.prefsEditor.apply();
    }

    //Nasa Switch
    public boolean getSwitchNasa() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(complicationId + PREFS_SWITCH_NASA, true);
    }

    public void setSwitchNasa(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(complicationId + PREFS_SWITCH_NASA, key);
        this.prefsEditor.apply();
    }

    //CASC Switch
    public boolean getSwitchCNSA() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(complicationId + PREFS_SWITCH_CNSA, true);
    }

    public void setSwitchCNSA(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(complicationId + PREFS_SWITCH_CNSA, key);
        this.prefsEditor.apply();
    }

    //All Switch
    public boolean getAllSwitch() {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        return this.sharedPrefs.getBoolean(complicationId + PREFS_SWITCH_ALL, true);
    }

    public void setAllSwitch(boolean key) {
        this.sharedPrefs = this.appContext.getSharedPreferences(PREFS_NAME, 0);
        this.prefsEditor = this.sharedPrefs.edit();
        this.prefsEditor.putBoolean(complicationId + PREFS_SWITCH_ALL, key);
        this.prefsEditor.apply();
        if (key) {
            resetSwitches();
        }
    }

    public void resetSwitches() {
        setSwitchNasa(true);
        setSwitchRoscosmos(true);
        setSwitchSpaceX(true);
        setSwitchULA(true);
    }
}
