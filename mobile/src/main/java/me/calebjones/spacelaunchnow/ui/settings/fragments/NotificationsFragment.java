package me.calebjones.spacelaunchnow.ui.settings.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;

import com.onesignal.OneSignal;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.notifications.NotificationBuilder;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
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
        if (key.equals("notifications_new_message")){
            OneSignal.setSubscription(sharedPreferences.getBoolean(key, true));
        } else {
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
        long timeToFinish = now.getTimeInMillis() - future.getTimeInMillis();

        NotificationBuilder.notifyUser(context, launch, timeToFinish);
    }
}
