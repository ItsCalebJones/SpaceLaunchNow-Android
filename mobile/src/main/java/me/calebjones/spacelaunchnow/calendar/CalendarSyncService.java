package me.calebjones.spacelaunchnow.calendar;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.calendar.model.CalendarItem;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.services.BaseService;
import timber.log.Timber;


public class CalendarSyncService extends BaseService {

    public static final String SYNC_EVENTS_ALL = "me.calebjones.spacelaunchnow.content.services.action.SYNC_EVENTS_ALL";
    public static final String DELETE_EVENTS_ALL = "me.calebjones.spacelaunchnow.content.services.action.DELETE_EVENTS_ALL";
    public static final String SYNC_EVENT = "me.calebjones.spacelaunchnow.content.services.action.SYNC_EVENT";
    public static final String DELETE_EVENT = "me.calebjones.spacelaunchnow.content.services.action.DELETE_EVENT";
    public static final String RESYNC_ALL = "me.calebjones.spacelaunchnow.content.services.action.RESYNC_ALL";
    public static final String EVENT_ID = "me.calebjones.spacelaunchnow.content.services.extra.EVENT_ID";
    public static final String LAUNCH_ID = "me.calebjones.spacelaunchnow.content.services.extra.LAUNCH_ID";

    private RealmResults<LaunchRealm> launchRealms;
    private CalendarUtility calendarUtil;
    private CalendarItem calendarItem;

    public CalendarSyncService() {
        super("CalendarSyncService");
    }

    public static void startActionSyncEvent(Context context, LaunchRealm launch, long id) {
        Intent intent = new Intent(context, CalendarSyncService.class);
        intent.setAction(SYNC_EVENT);
        intent.putExtra(LAUNCH_ID, launch.getId());
        intent.putExtra(EVENT_ID, id);
        context.startService(intent);
        Timber.v("Sending Sync intent.");
    }

    public static void startActionSyncAll(Context context) {
        Intent intent = new Intent(context, CalendarSyncService.class);
        intent.setAction(SYNC_EVENTS_ALL);
        context.startService(intent);
        Timber.v("Sending SyncAll intent.");
    }

    public static void startActionDelete(Context context) {
        Intent intent = new Intent(context, CalendarSyncService.class);
        intent.setAction(DELETE_EVENT);
        context.startService(intent);
        Timber.v("Sending Delete intent.");
    }

    public static void startActionDeleteAll(Context context) {
        Intent intent = new Intent(context, CalendarSyncService.class);
        intent.setAction(DELETE_EVENTS_ALL);
        context.startService(intent);
        Timber.v("Sending DeleteAll intent.");
    }

    public static void startActionResync(Context context) {
        Intent intent = new Intent(context, CalendarSyncService.class);
        intent.setAction(RESYNC_ALL);
        context.startService(intent);
        Timber.v("Sending ResyncAll intent.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.v("onHandleIntent received: ", intent.getAction());
        mRealm = Realm.getDefaultInstance();
        calendarUtil = new CalendarUtility(mRealm.where(CalendarItem.class).findFirst());
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
            } else if (RESYNC_ALL.equals(action)){
                handleActionDeleteAll();
                handleActionSyncAll();
            }
        }
        mRealm.close();
    }

    private void handleActionSyncAll() {
        launchRealms = mRealm.where(LaunchRealm.class)
                .greaterThanOrEqualTo("net", new Date())
                .findAllSorted("net", Sort.ASCENDING);

        RealmList<LaunchRealm> launchResults = new RealmList<>();

        if (launchRealms.size() > 5) {
            launchResults.addAll(launchRealms.subList(0, 5));
        } else {
            launchResults.addAll(launchRealms);
        }

        for (LaunchRealm launchRealm: launchResults){
            if (launchRealm.getEventID() != null){
                boolean success = calendarUtil.updateEvent(this, launchRealm);
                if (!success) {
                    Timber.v("Unable to update event %s, assuming deleted.", launchRealm.getName());
                    Integer id = calendarUtil.addEvent(this, launchRealm);
                    if (id != null) {
                        mRealm.beginTransaction();
                        launchRealm.setEventID(id);
                        mRealm.commitTransaction();
                    }
                }
            } else {
                Integer id = calendarUtil.addEvent(this, launchRealm);
                if (id != null) {
                    mRealm.beginTransaction();
                    launchRealm.setEventID(id);
                    mRealm.commitTransaction();
                }
            }
        }
        calendarUtil.deleteDuplicates(this, mRealm, "Space Launch Now", CalendarContract.Events.DESCRIPTION);
    }

    private void handleActionDeleteAll() {
        launchRealms = mRealm.where(LaunchRealm.class)
                .greaterThan("eventID", 0)
                .findAll();

        for (LaunchRealm launchRealm: launchRealms) {
            int success = calendarUtil.deleteEvent(this, launchRealm);
            if (success > 0){
                mRealm.beginTransaction();
                launchRealm.setEventID(null);
                mRealm.commitTransaction();
            }
        }
    }

    private void handleActionDeleteEvent(int launchId, long eventId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionSyncEvent(int launchId, long eventId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
