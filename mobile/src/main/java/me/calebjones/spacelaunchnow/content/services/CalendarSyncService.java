package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import me.calebjones.spacelaunchnow.content.models.legacy.Launch;

public class CalendarSyncService extends IntentService {

    private static final String SYNC_EVENTS_ALL = "me.calebjones.spacelaunchnow.content.services.action.SYNC_EVENTS_ALL";
    private static final String DELETE_EVENTS_ALL = "me.calebjones.spacelaunchnow.content.services.action.DELETE_EVENTS_ALL";
    private static final String SYNC_EVENT = "me.calebjones.spacelaunchnow.content.services.action.SYNC_EVENT";
    private static final String DELETE_EVENT = "me.calebjones.spacelaunchnow.content.services.action.DELETE_EVENT";

    private static final String EVENT_ID = "me.calebjones.spacelaunchnow.content.services.extra.EVENT_ID";
    private static final String LAUNCH_ID = "me.calebjones.spacelaunchnow.content.services.extra.LAUNCH_ID";



    public CalendarSyncService() {
        super("CalendarSyncService");
    }

    public static void startActionSync(Context context) {
        Intent intent = new Intent(context, CalendarSyncService.class);
        intent.setAction(SYNC_EVENT);
        context.startService(intent);
    }

    public static void startActionDelete(Context context) {
        Intent intent = new Intent(context, CalendarSyncService.class);
        intent.setAction(DELETE_EVENT);
        context.startService(intent);
    }

    public static void startActionSyncEvent(Context context, Launch launch, long id) {
        Intent intent = new Intent(context, CalendarSyncService.class);
        intent.setAction(SYNC_EVENT);
        intent.putExtra(LAUNCH_ID, launch.getId());
        intent.putExtra(EVENT_ID, id);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (SYNC_EVENTS_ALL.equals(action)) {
                handleActionSyncAll();
            } else if (DELETE_EVENTS_ALL.equals(action)) {

                handleActionDeleteAll();
            } else if (SYNC_EVENT.equals(action)) {
                final int param1 = intent.getIntExtra(LAUNCH_ID, 0);
                final long param2 = intent.getLongExtra(EVENT_ID, 0);
                handleActionDeleteEvent(param1, param2);
            }
        }
        onDestroy();
    }

    private void handleActionSyncAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionDeleteAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionDeleteEvent(int launchId, long eventId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionSyncEvent(int launchId, long eventId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
