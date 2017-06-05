package me.calebjones.spacelaunchnow.content.services;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class LaunchNotificationReceiver extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read properties from result.
        Timber.i("Received Result - App in Focus: %s Payload: %s", receivedResult.isAppInFocus, receivedResult.payload);

        if (receivedResult.payload != null) {
            OSNotificationPayload payload = receivedResult.payload;
            JSONObject data = payload.additionalData;
            try {
                String launchid = data.getString("launchid");
            } catch (JSONException e) {
                Timber.e(e);
                return false;
            }
        }

        // Return true to stop the notification from displaying.
        return true;
    }
}
