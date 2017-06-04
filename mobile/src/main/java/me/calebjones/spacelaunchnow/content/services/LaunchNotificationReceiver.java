package me.calebjones.spacelaunchnow.content.services;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

public class LaunchNotificationReceiver extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read properties from result.

        // Return true to stop the notification from displaying.
        return true;
    }
}
