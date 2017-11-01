package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;

public abstract class BaseManager{

    public SharedPreferences sharedPref;
    public ListPreferences listPreference;
    public SwitchPreferences switchPreferences;
    public Realm mRealm;
    public Context context;

    public BaseManager(Context context) {
        this.context = context;
        this.listPreference = ListPreferences.getInstance(context);
        this.switchPreferences = SwitchPreferences.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

}
