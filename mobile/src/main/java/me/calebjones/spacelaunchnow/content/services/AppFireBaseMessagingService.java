package me.calebjones.spacelaunchnow.content.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.content.notifications.NotificationBuilder;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Location;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Rocket;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class AppFireBaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.d("From: %s", remoteMessage.getFrom());



        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Timber.d("Message data payload: %s",remoteMessage.getData());
            Map<String, String> params = remoteMessage.getData();
            JSONObject data = new JSONObject(params);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss zzz", Locale.ENGLISH);
            try {
                String background = data.getString("background");
                Launch launch = new Launch();
                launch.setId(Integer.valueOf(data.getString("launch_id")));
                launch.setNet(dateFormat.parse(data.getString("launch_net")));
                launch.setName(data.getString("launch_name"));

                Rocket rocket = new Rocket();
                rocket.setImageURL(data.getString("launch_image"));
                launch.setRocket(rocket);

                Location location = new Location();
                location.setName(data.getString("launch_location"));
                launch.setLocation(location);

                if (background.contains("true")) {
                    if (isNotificationEnabled(data.getString("notification_type"), data.getString("webcast").contains("true"))) {
                        Calendar future = Utils.DateToCalendar(launch.getNet());
                        Calendar now = Calendar.getInstance();
                        long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();

                        if (timeToFinish > 0) {
                            NotificationBuilder.notifyUser(getApplicationContext(), launch, timeToFinish, data.getString("notification_type"));
                        }
                    }
                }
            } catch (JSONException | ParseException e) {
                // Error parsing additional data
                Timber.e(e);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Timber.d("Message Notification Body: %s", remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private boolean isNotificationEnabled(String notificationType, boolean webcastAvailable) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationEnabled = prefs.getBoolean("notifications_new_message", true);
        boolean netstampChanged = prefs.getBoolean("notifications_launch_imminent_updates", true);
        boolean webcastOnly = prefs.getBoolean("notifications_new_message_webcast", false);
        boolean twentyFourHour = prefs.getBoolean("notifications_launch_day", true);
        boolean oneHour = prefs.getBoolean("notifications_launch_imminent", true);
        boolean tenMinutes = prefs.getBoolean("notifications_launch_minute", true);

        if (notificationEnabled) {

            if (notificationType.contains("netstampChanged") && netstampChanged) {
                Timber.i("Netstamp Changed enabled.");
                return true;
            }

            if (webcastOnly && !webcastAvailable) {
                Timber.i("Webcast is required for to post notification - none available therefore skipping.");
                return false;
            }

            if (notificationType.contains("twentyFourHour") && twentyFourHour) {
                return true;
            }

            if (notificationType.contains("oneHour") && oneHour) {
                return true;
            }

            if (notificationType.contains("tenMinute") && tenMinutes) {
                return true;
            }

            if (BuildConfig.DEBUG && notificationType.contains("test")) {
                return true;
            }
        }
        return false;
    }
}
