package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.content.SharedPreferences;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;

public abstract class BaseService extends IntentService {

    public SharedPreferences sharedPref;
    public ListPreferences listPreference;
    public SwitchPreferences switchPreferences;
    public Realm mRealm;

    public BaseService(String name) {
        super(name);
    }

    public void onCreate() {


        super.onCreate();
    }
}
