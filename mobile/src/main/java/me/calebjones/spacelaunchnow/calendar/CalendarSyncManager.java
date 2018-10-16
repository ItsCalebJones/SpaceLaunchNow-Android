package me.calebjones.spacelaunchnow.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import com.pixplicity.easyprefs.library.Prefs;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.calendar.model.CalendarItem;
import me.calebjones.spacelaunchnow.content.services.BaseManager;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import timber.log.Timber;


public class CalendarSyncManager extends BaseManager {

    public static final String SYNC_EVENTS_ALL = "me.calebjones.spacelaunchnow.content.services.action.SYNC_EVENTS_ALL";
    public static final String DELETE_EVENTS_ALL = "me.calebjones.spacelaunchnow.content.services.action.DELETE_EVENTS_ALL";
    public static final String SYNC_EVENT = "me.calebjones.spacelaunchnow.content.services.action.SYNC_EVENT";
    public static final String DELETE_EVENT = "me.calebjones.spacelaunchnow.content.services.action.DELETE_EVENT";
    public static final String RESYNC_ALL = "me.calebjones.spacelaunchnow.content.services.action.RESYNC_ALL";
    public static final String EVENT_ID = "me.calebjones.spacelaunchnow.content.services.extra.EVENT_ID";
    public static final String LAUNCH_ID = "me.calebjones.spacelaunchnow.content.services.extra.LAUNCH_ID";

    private RealmResults<Launch> launches;
    private CalendarUtility calendarUtil;
    private CalendarItem calendarItem;

    public CalendarSyncManager(Context context) {
        super(context);
        calendarItem = mRealm.where(CalendarItem.class).findFirst();
        if (calendarItem != null) {
            calendarUtil = new CalendarUtility(calendarItem);
        } else {
            Prefs.putBoolean("calendar_sync_state", false);

            switchPreferences.setCalendarStatus(false);
        }
    }

    public void resyncCalendarItem(){
        calendarItem = mRealm.where(CalendarItem.class).findFirst();
        calendarUtil = new CalendarUtility(calendarItem);
    }

    public void syncAllEevnts() {
        if (calendarUtil != null) {
            handleActionSyncAll();
        }
    }

    public void deleteEvent() {
        if (calendarUtil != null) {
            Timber.v("Hello?");
            return;
        }
    }

    public void deleteAllEvents() {
        if (calendarUtil != null) {
            handleActionDeleteAll();
        }
    }

    public void resyncAllEvents() {
        if (calendarUtil != null) {
            handleActionDeleteAll();
            handleActionSyncAll();
        }
    }

    private void handleActionSyncAll() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (switchPreferences.getCalendarStatus()) {
            launches = QueryBuilder.buildSwitchQuery(context, mRealm);
        } else {
            launches = QueryBuilder.buildSwitchQuery(context, mRealm, true);
        }

        RealmList<Launch> launchResults = new RealmList<>();

        int size = 10;

        if (launches.size() > size) {
            launchResults.addAll(launches.subList(0, size));
        } else {
            launchResults.addAll(launches);
        }

        for (final Launch launch : launchResults) {
                syncCalendar(launch);
        }
        calendarUtil.deleteDuplicates(context, mRealm, "Space Launch Now", CalendarContract.Events.DESCRIPTION);
    }

    private void syncCalendar(final Launch launchRealm) {
        if (launchRealm.getEventID() != null) {
            boolean success = calendarUtil.updateEvent(context, launchRealm);
            if (!success) {
                Timber.e("Unable to update event %s, assuming deleted.", launchRealm.getName());
                final Integer id = calendarUtil.addEvent(context, launchRealm);
                if (id != null) {
                    mRealm.executeTransaction(realm -> launchRealm.setEventID(id));
                }
            }
        } else {
            final Integer id = calendarUtil.addEvent(context, launchRealm);
            if (id != null) {
                mRealm.executeTransaction(realm -> launchRealm.setEventID(id));
            }
        }
    }

    private void handleActionDeleteAll() {
        launches = mRealm.where(Launch.class)
                .greaterThan("eventID", 0)
                .findAll();

        for (final Launch launchRealm : launches) {
            int success = calendarUtil.deleteEvent(context, launchRealm);
            if (success > 0) {
                mRealm.executeTransaction(realm -> launchRealm.setEventID(null));
            }
        }
    }
}
