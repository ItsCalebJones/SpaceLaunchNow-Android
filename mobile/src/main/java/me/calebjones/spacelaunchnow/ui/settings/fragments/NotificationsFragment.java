package me.calebjones.spacelaunchnow.ui.settings.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.util.NotificationBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.content.services.NextLaunchTracker.DateToCalendar;

public class NotificationsFragment extends BaseSettingFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreferences switchPreferences;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_preferences);
        Preference testPreference =  findPreference("notifications_new_message_test");
        testPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        sendTestNotification();
                    }
                });

                t.start();
                return true;
            }
        });
        setName("Notifications Fragment");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context = getContext();
        } else {
            context = getActivity();
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
        if (key.equals("notifications_launch_imminent_updates")) {
            OneSignal.setSubscription(sharedPreferences.getBoolean(key, true));
            Analytics.from(this).sendPreferenceEvent(key, sharedPreferences.getBoolean(key, true));
        }   else {
            Analytics.from(this).sendPreferenceEvent(key);
        }

    }

    private void sendTestNotification() {
        Realm realm = Realm.getDefaultInstance();
        Launch launch = realm.where(Launch.class)
                .greaterThanOrEqualTo("net", new Date())
                .findAllSorted("net", Sort.ASCENDING).first();

        Calendar future = DateToCalendar(new Date(launch.getNetstamp() * 1000));
        Calendar now = Calendar.getInstance();

        now.setTimeInMillis(System.currentTimeMillis());
        long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();

        NotificationBuilder.notifyUser(context, launch, timeToFinish);
    }
}
