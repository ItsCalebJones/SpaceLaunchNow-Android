package me.calebjones.spacelaunchnow.common.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;

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
        mRealm = Realm.getDefaultInstance();
    }

    public void onDestroy(){
        if (mRealm != null) {
            mRealm.close();
        }
    }

}
