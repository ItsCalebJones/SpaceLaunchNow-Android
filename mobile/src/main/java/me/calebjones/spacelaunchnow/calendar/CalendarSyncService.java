package me.calebjones.spacelaunchnow.calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.calendar.model.CalendarItem;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.content.services.BaseService;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import timber.log.Timber;


public class CalendarSyncService extends BaseService {

    public static final String SYNC_EVENTS_ALL = "me.calebjones.spacelaunchnow.content.services.action.SYNC_EVENTS_ALL";
    public static final String DELETE_EVENTS_ALL = "me.calebjones.spacelaunchnow.content.services.action.DELETE_EVENTS_ALL";
    public static final String SYNC_EVENT = "me.calebjones.spacelaunchnow.content.services.action.SYNC_EVENT";
    public static final String DELETE_EVENT = "me.calebjones.spacelaunchnow.content.services.action.DELETE_EVENT";
    public static final String RESYNC_ALL = "me.calebjones.spacelaunchnow.content.services.action.RESYNC_ALL";
    public static final String EVENT_ID = "me.calebjones.spacelaunchnow.content.services.extra.EVENT_ID";
    public static final String LAUNCH_ID = "me.calebjones.spacelaunchnow.content.services.extra.LAUNCH_ID";

    private RealmResults<Launch> launchRealms;
    private CalendarUtility calendarUtil;
    private CalendarItem calendarItem;
    private SharedPreferences sharedPreferences;
    private SwitchPreferences switchPreferences;

    public CalendarSyncService() {
        super("CalendarSyncService");
    }

    public static void startActionSyncEvent(Context context, Launch launch, long id) {
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
        Timber.v("onHandleIntent received: %s", intent.getAction());
        switchPreferences = SwitchPreferences.getInstance(this);
        mRealm = Realm.getDefaultInstance();
        CalendarItem calendarItem = mRealm.where(CalendarItem.class).findFirst();
        if (calendarItem != null) {
            calendarUtil = new CalendarUtility(calendarItem);
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
                } else if (RESYNC_ALL.equals(action)) {
                    handleActionDeleteAll();
                    handleActionSyncAll();
                }
            }
            mRealm.close();
        } else {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("calendar_sync_state", false);
            editor.apply();

            switchPreferences.setCalendarStatus(false);
        }
    }

    private void handleActionSyncAll() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (switchPreferences.getCalendarStatus()) {
            launchRealms = QueryBuilder.buildSwitchQuery(this, mRealm);
        } else {
            launchRealms = QueryBuilder.buildSwitchQuery(this, mRealm, true);
        }

        RealmList<Launch> launchResults = new RealmList<>();

        int size = Integer.parseInt(sharedPref.getString("calendar_count", "5"));

        if (launchRealms.size() > size) {
            launchResults.addAll(launchRealms.subList(0, size));
        } else {
            launchResults.addAll(launchRealms);
        }

        for (Launch launchRealm: launchResults){
            if (launchRealm.isUserToggledCalendar()){
                if (launchRealm.syncCalendar()){
                    syncCalendar(launchRealm);
                }
            } else {
                syncCalendar(launchRealm);
            }
        }
        calendarUtil.deleteDuplicates(this, mRealm, "Space Launch Now", CalendarContract.Events.DESCRIPTION);
    }

    private void syncCalendar(final Launch launchRealm){
        if (launchRealm.getEventID() != null){
            boolean success = calendarUtil.updateEvent(this, launchRealm);
            if (!success) {
                Timber.v("Unable to update event %s, assuming deleted.", launchRealm.getName());
                final Integer id = calendarUtil.addEvent(this, launchRealm);
                if (id != null) {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            launchRealm.setEventID(id);
                            launchRealm.setSyncCalendar(true);
                        }
                    });
                }
            }
        } else {
            final Integer id = calendarUtil.addEvent(this, launchRealm);
            if (id != null) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        launchRealm.setEventID(id);
                        launchRealm.setSyncCalendar(true);
                    }
                });
            }
        }
    }

    private void handleActionDeleteAll() {
        launchRealms = mRealm.where(Launch.class)
                .greaterThan("eventID", 0)
                .findAll();

        for (final Launch launchRealm: launchRealms) {
            int success = calendarUtil.deleteEvent(this, launchRealm);
            if (success > 0){
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        launchRealm.setEventID(null);
                        if (!launchRealm.isUserToggledCalendar()) {
                            launchRealm.setSyncCalendar(false);
                        }
                    }
                });
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
