package me.calebjones.spacelaunchnow.content.services;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.common.content.notifications.NotificationBuilder;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.EventType;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.data.models.main.Location;
import me.calebjones.spacelaunchnow.data.models.main.Pad;
import me.calebjones.spacelaunchnow.data.models.main.Rocket;
import me.calebjones.spacelaunchnow.data.networking.RetrofitBuilder;
import timber.log.Timber;

public class AppFireBaseMessagingService extends FirebaseMessagingService {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss zzz", Locale.ENGLISH);

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Timber.d("From: %s", remoteMessage.getFrom());

        // Check if getMessage contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Timber.d("Message data payload: %s",remoteMessage.getData());
            Map<String, String> params = remoteMessage.getData();
            JSONObject data = new JSONObject(params);

            try {
                String notificationType = data.getString("notification_type");
                checkNotificationType(notificationType, data);
            } catch (JSONException | ParseException e) {
                // Error parsing additional data
                Timber.e(e);
            }
        }

        // Check if getMessage contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Timber.d("Message Notification Body: %s", remoteMessage.getNotification().getBody());
        }

    }

    private void checkNotificationType(String notificationType, JSONObject data) throws JSONException, ParseException {

        boolean notificationEnabled = Prefs.getBoolean("notificationEnabled", true);
        boolean netstampChanged = Prefs.getBoolean("netstampChanged", true);
        boolean webcastLive = Prefs.getBoolean("notificationEnabled", true);
        boolean webcastOnly = Prefs.getBoolean("webcastOnly", false);
        boolean twentyFourHour = Prefs.getBoolean("twentyFourHour", true);
        boolean oneHour = Prefs.getBoolean("oneHour", true);
        boolean tenMinutes = Prefs.getBoolean("tenMinutes", true);
        boolean inFlight = Prefs.getBoolean("inFlight", true);
        boolean success = Prefs.getBoolean("success", true);
        boolean oneMinute = Prefs.getBoolean("oneMinute", true);
        boolean eventNotifications = Prefs.getBoolean("eventNotifications", true);

        if (notificationEnabled) {
            Context context = getApplicationContext();

            if (notificationType.contains("netstampChanged") && netstampChanged) {
                Timber.i("Netstamp Changed enabled.");
                NotificationBuilder.notifyUserLaunchScheduleChanged(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("inFlight") && inFlight) {
                NotificationBuilder.notifyUserInFlight(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("success") && success) {
                NotificationBuilder.notifyUserSuccess(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("failure") && success) {
                NotificationBuilder.notifyUserFailure(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("partial_failure") && success) {
                NotificationBuilder.notifyUserPartialFailure(context, getLaunchFromJSON(data));
            }

            if (BuildConfig.DEBUG && notificationType.contains("test")) {
                NotificationBuilder.notifyUserTest(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("twentyFourHour") && twentyFourHour) {
                NotificationBuilder.notifyUserTwentyFourHours(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("oneHour") && oneHour) {
                if (checkWebcast(data, webcastOnly)) return;
                NotificationBuilder.notifyUserOneHour(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("tenMinute") && tenMinutes) {
                if (checkWebcast(data, webcastOnly)) return;
                NotificationBuilder.notifyUserTenMinutes(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("oneMinute") && oneMinute) {
                if (checkWebcast(data, webcastOnly)) return;
                NotificationBuilder.notifyUserOneMinute(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("webcastLive") && webcastLive) {
                if (checkWebcast(data, webcastOnly)) return;
                NotificationBuilder.notifyUserLaunchWebcastLive(context, getLaunchFromJSON(data));
            }

            if (notificationType.contains("event_notification") && eventNotifications) {
                NotificationBuilder.notifyUserEventUpcoming(context, getEventFromJSON(data));
            }

            if (notificationType.contains("event_webcast") && eventNotifications) {
                if (checkWebcast(data, webcastOnly)) return;
                NotificationBuilder.notifyUserEventWebcastLive(context, getEventFromJSON(data));
            }
        }
    }

    private Launch getLaunchFromJSON(JSONObject data) throws JSONException, ParseException {
        Launch launch = new Launch();
        launch.setId(data.getString("launch_uuid"));
        launch.setNet(dateFormat.parse(data.getString("launch_net")));
        launch.setName(data.getString("launch_name"));

        LauncherConfig launcherConfig = new LauncherConfig();
        launcherConfig.setImageUrl(data.getString("launch_image"));
        Rocket rocket = new Rocket();
        rocket.setConfiguration(launcherConfig);
        launch.setRocket(rocket);

        Location location = new Location();
        location.setName(data.getString("launch_location"));
        Pad pad = new Pad();
        pad.setLocation(location);
        launch.setPad(pad);
        return launch;
    }

    private Event getEventFromJSON(JSONObject data) throws JSONException  {
        return RetrofitBuilder.getGson().fromJson(data.getString("event"), Event.class);
    }

    private boolean checkWebcast(JSONObject data, boolean webcastOnly) throws JSONException {
        if (webcastOnly && !data.getString("webcast").contains("true")) {
            Timber.i("Webcast is required to post notification - none available therefore skipping.");
            return true;
        }
        return false;
    }
}
