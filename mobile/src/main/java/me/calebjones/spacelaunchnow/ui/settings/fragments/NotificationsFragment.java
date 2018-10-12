package me.calebjones.spacelaunchnow.ui.settings.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.notifications.NotificationBuilder;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.content.notifications.NotificationHelper.CHANNEL_LAUNCH_REMINDER;
import static me.calebjones.spacelaunchnow.content.notifications.NotificationHelper.CHANNEL_LAUNCH_REMINDER_NAME;

public class NotificationsFragment extends BaseSettingFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreferences switchPreferences;
    private Context context;
    private FirebaseMessaging firebaseMessaging;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseMessaging = FirebaseMessaging.getInstance();
        addPreferencesFromResource(R.xml.notification_preferences);
        Preference testPreference =  findPreference("notifications_new_message_test");
        testPreference.setOnPreferenceClickListener(preference -> {
            Thread t = new Thread(this::sendTestNotification);
            t.start();
            return true;
        });
        setName("Notifications Fragment");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context = getContext();
        } else {
            context = getActivity();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Preference manageNotifications =  findPreference("manage_notification_channel");
            manageNotifications.setOnPreferenceClickListener(preference -> {
                Intent notificationSettings = new Intent();
                notificationSettings.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                //for Android 5-7
                notificationSettings.putExtra("app_package", context.getPackageName());
                notificationSettings.putExtra("app_uid", context.getApplicationInfo().uid);

                // for Android O
                notificationSettings.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

                startActivity(notificationSettings);
                return true;
            });
        }
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
        if (key.equals("notificationEnabled")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("notificationEnabled");
            } else {
                firebaseMessaging.unsubscribeFromTopic("notificationEnabled");
            }
        }

        if (key.equals("netstampChanged")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("netstampChanged");
            } else {
                firebaseMessaging.unsubscribeFromTopic("netstampChanged");
            }
        }

        if (key.equals("webcastOnly")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("webcastOnly");
            } else {
                firebaseMessaging.unsubscribeFromTopic("webcastOnly");
            }
        }

        if (key.equals("twentyFourHour")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("twentyFourHour");
            } else {
                firebaseMessaging.unsubscribeFromTopic("twentyFourHour");
            }
        }

        if (key.equals("oneHour")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("oneHour");
            } else {
                firebaseMessaging.unsubscribeFromTopic("oneHour");
            }
        }

        if (key.equals("tenMinutes")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("tenMinutes");
            } else {
                firebaseMessaging.unsubscribeFromTopic("tenMinutes");
            }
        }

        if (key.equals("oneMinute")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("oneMinute");
            } else {
                firebaseMessaging.unsubscribeFromTopic("oneMinute");
            }
        }

        if (key.equals("inFlight")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("inFlight");
            } else {
                firebaseMessaging.unsubscribeFromTopic("inFlight");
            }
        }

        if (key.equals("success")){
            if (Prefs.getBoolean(key, true)){
                firebaseMessaging.subscribeToTopic("success");
            } else {
                firebaseMessaging.unsubscribeFromTopic("success");
            }
        }
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
