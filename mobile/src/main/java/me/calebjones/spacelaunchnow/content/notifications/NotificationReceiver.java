package me.calebjones.spacelaunchnow.content.notifications;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.data.models.Location;
import me.calebjones.spacelaunchnow.data.models.Rocket;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class NotificationReceiver extends NotificationExtenderService {

    private SwitchPreferences switchPreferences;

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        switchPreferences = SwitchPreferences.getInstance(getApplicationContext());
        // Read properties from result.
        Timber.i("Received Result - App in Focus: %s Payload: %s", receivedResult.isAppInFocus, receivedResult.payload);

        if (receivedResult.payload != null) {
            OSNotificationPayload payload = receivedResult.payload;
            JSONObject data = payload.additionalData;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss zzz");
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
                    Calendar future = Utils.DateToCalendar(launch.getNet());
                    Calendar now = Calendar.getInstance();
                    long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();
                    if (switchPreferences.getAllSwitch()) {
                        NotificationBuilder.notifyUser(getApplicationContext(), launch, timeToFinish);
                    } else {
                        // TODO filter out launches we dont care about here?
                        NotificationBuilder.notifyUser(getApplicationContext(), launch, timeToFinish);
                    }
                    return true;
                } else {
                    // Not a background payload, likely no additional data, show the notification
                    return false;
                }
            } catch (JSONException | ParseException e) {
                // Error parsing additional data,  trigger a sync.
                Timber.e(e);
                return false;
            }

            // Payload is null, likely no additional data, show the notification
        } else {
            return false;
        }
    }
}
