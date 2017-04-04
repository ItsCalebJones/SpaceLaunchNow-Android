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
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.Launch;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.utils.Analytics;
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
                sendTestNotification();
                return true;
            }
        });
        setName("Notifications Fragment");
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
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(getActivity());
        NotificationManager mNotifyManager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Realm realm = Realm.getDefaultInstance();
        Launch launch = realm.where(Launch.class)
                .greaterThan("net", new Date())
                .findFirst();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        long longdate = launch.getNetstamp();
        longdate = longdate * 1000;
        final Date futureDate = new Date(longdate);

        Calendar future = DateToCalendar(futureDate);
        Calendar now = Calendar.getInstance();

        now.setTimeInMillis(System.currentTimeMillis());
        long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();
        int hours = (int) ((timeToFinish / (1000 * 60 * 60)) % 24);
        String launchDate;
        String expandedText;
        String launchName = launch.getName();
        String launchPad = launch.getLocation().getName();

        String ringtoneBox = sharedPref.getString("notifications_new_message_ringtone", "default ringtone");
        Uri alarmSound = Uri.parse(ringtoneBox);

        Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent appIntent = PendingIntent.getActivity(getActivity(), 0, mainActivityIntent, 0);
        expandedText = "Launch attempt in " + hours + " hours from " + launchPad;

        if (launch.getNet() != null) {
            //Get launch date
            if (sharedPref.getBoolean("local_time", true)) {
                SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd - hh:mm a zzz");
                df.toLocalizedPattern();
                Date date = launch.getNet();
                launchDate = df.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd - hh:mm a zzz");
                Date date = launch.getNet();
                launchDate = sdf.format(date);
            }
            mBuilder.setSubText(launchDate);
        }

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(BitmapFactory.decodeResource(getResources(),
                                R.drawable.nav_header));

        mBuilder.setContentTitle(launchName)
                .setContentText("Launch attempt in " + hours + " hours from " + launchPad)
                .setSmallIcon(R.drawable.ic_rocket_white)
                .setContentIntent(appIntent)
                .setContentText(expandedText)
                .extend(wearableExtender)
                .setSound(alarmSound)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                sharedPref.getBoolean("notifications_new_message_heads_up", true)) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN &&
                sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setVibrate(new long[]{750, 750});
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                && sharedPref.getBoolean("notifications_new_message_led", true)) {
            mBuilder.setLights(Color.GREEN, 3000, 3000);
        }

        if (sharedPref.getBoolean("notifications_new_message_webcast", false)) {
            if (launch.getVidURLs() != null && launch.getVidURLs().size() > 0) {
                mNotifyManager.notify(Constants.NOTIF_ID_HOUR, mBuilder.build());
            }
        } else {
            mNotifyManager.notify(Constants.NOTIF_ID_HOUR, mBuilder.build());
        }
    }
}
