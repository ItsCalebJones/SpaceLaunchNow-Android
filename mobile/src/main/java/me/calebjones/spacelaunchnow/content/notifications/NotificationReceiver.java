package me.calebjones.spacelaunchnow.content.notifications;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Location;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Rocket;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class NotificationReceiver extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read properties from result.
        Timber.i("Received Result - App in Focus: %s Payload: %s", receivedResult.isAppInFocus, receivedResult.payload);

        if (receivedResult.payload != null) {
            OSNotificationPayload payload = receivedResult.payload;
            JSONObject data = payload.additionalData;
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
                    return true;
                } else {
                    // Not a background payload, likely no additional data, show the notification
                    return false;
                }
            } catch (JSONException | ParseException e) {
                // Error parsing additional data
                Timber.e(e);
                return false;
            }

            // Payload is null, likely no additional data, show the notification
        } else {
            return false;
        }
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
