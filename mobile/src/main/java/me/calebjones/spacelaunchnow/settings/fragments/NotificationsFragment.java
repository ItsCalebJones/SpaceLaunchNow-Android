package me.calebjones.spacelaunchnow.settings.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.onesignal.OneSignal;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import timber.log.Timber;

public class NotificationsFragment extends BaseSettingFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreferences switchPreferences;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_preferences);
    }

    @Override
    public void onResume() {
        Timber.v("onResume - setting OnSharedPreferenceChangeListener");
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.v("onPause - removing OnSharedPreferenceChangeListener");
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.i("Notifications preference %s changed.", key);
        if (key.equals("notifications_launch_imminent_updates")) {
            OneSignal.setSubscription(sharedPreferences.getBoolean(key, false));
        }
    }
}
