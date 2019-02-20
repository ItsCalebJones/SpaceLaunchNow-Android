package me.calebjones.spacelaunchnow.common.ui.settings.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.Preference;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Date;

import io.realm.Realm;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.common.content.notifications.NotificationBuilder;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_LAUNCH_REMINDER;
import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_LAUNCH_REMINDER_NAME;

public class NotificationsFragment extends BaseSettingsFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context context;
    private FirebaseMessaging firebaseMessaging;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseMessaging = FirebaseMessaging.getInstance();

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
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

    }

    private void sendTestNotification() {
        Realm realm = Realm.getDefaultInstance();
        Launch launch = realm.where(Launch.class)
                .greaterThanOrEqualTo("net", new Date())
                .sort("net", Sort.ASCENDING).findFirst();

        if (launch != null) {
            NotificationBuilder.buildNotification(context, launch, launch.getName(), String.format("This is a test notification! (Channel - %s)", CHANNEL_LAUNCH_REMINDER_NAME), CHANNEL_LAUNCH_REMINDER);
        }
        realm.close();
    }
}
