package me.calebjones.spacelaunchnow.common.content.calendar;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import com.pixplicity.easyprefs.library.Prefs;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.common.content.calendar.model.CalendarItem;
import me.calebjones.spacelaunchnow.common.base.BaseManager;
import me.calebjones.spacelaunchnow.common.content.util.FilterBuilder;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.main.CalendarEvent;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.common.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    public void deleteEvent(Long id) {
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
        launches = QueryBuilder.buildUpcomingSwitchQueryForCalendar(context, mRealm);

        RealmList<Launch> launchResults = new RealmList<>();

        int size = 5;
        if (launches.size() == 0){
            getLaunchesFromNetwork();
        } else if (launches.size() > size) {
            launchResults.addAll(launches.subList(0, size));
        } else {
            launchResults.addAll(launches);
        }


        Timber.d("Found some launches! Count: %s", launchResults.size());
        for (final Launch launch : launchResults) {
                syncCalendar(launch);
        }
    }

    private void getLaunchesFromNetwork() {

        String locationIds = null;
        String lspIds = null;

        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        if (!switchPreferences.getAllSwitch()) {
            lspIds = FilterBuilder.getLSPIds(context);
            locationIds = FilterBuilder.getLocationIds(context);
        }
        DataClient.getInstance().getNextUpcomingLaunches(10, 0, locationIds, lspIds, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    LaunchResponse launchResponse = response.body();

                    Timber.v("UpcomingLaunches Count: %s", launchResponse.getCount());
                    if (launchResponse.getLaunches() != null) {
                        Realm mRealm = Realm.getDefaultInstance();
                        mRealm.executeTransaction((Realm mRealm1) -> mRealm1.copyToRealmOrUpdate(launchResponse.getLaunches()));
                        mRealm.close();
                        RealmList<Launch> launchResults = new RealmList<>();
                        launches = QueryBuilder.buildUpcomingSwitchQueryForCalendar(context, mRealm);
                        if (launches.size() > 10) {
                            launchResults.addAll(launches.subList(0, 10));
                        } else if (launches.size() > 0)  {
                            launchResults.addAll(launches);
                            Timber.d("Found some launches! Count: %s", launchResults.size());
                            for (final Launch launch : launchResults) {
                                syncCalendar(launch);
                            }
                        }
                    }
                } else {
                    Timber.w("Response received: %s", response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                Timber.e(t);
            }
        });
    }

    private void syncCalendar(final Launch launch) {
        Timber.d("Syncing launch: %s %s", launch.getName(), launch);
        RealmResults<CalendarEvent> calendarEvents = mRealm.where(CalendarEvent.class).equalTo("launchId", launch.getId()).findAll();
        if (calendarEvents.size() > 1){
            for (CalendarEvent calendarEvent : calendarEvents){
                calendarUtil.deleteEvent(context, calendarEvent.getId());
            }

            createEvent(launch);
        } else if (calendarEvents.size() == 1) {
            if (!calendarUtil.updateEvent(context, launch, calendarEvents.first().getId())){
                createEvent(launch);
            }
        } else {
            createEvent(launch);
        }
    }

    private void createEvent(Launch launch) {
        Long id = calendarUtil.addEvent(context, launch);
        Timber.d("Created event %d for %s", id, launch.getName());
        mRealm.executeTransaction(realm -> {
            CalendarEvent calendarEvent = realm.createObject(CalendarEvent.class);
            calendarEvent.setId(id);
            calendarEvent.setLaunchId(launch.getId());
        });
    }

    private void handleActionDeleteAll() {
        RealmResults<CalendarEvent> calendarEvents = mRealm.where(CalendarEvent.class)
                .findAll();

        for (CalendarEvent calendarEvent : calendarEvents) {
            Timber.d("Deleting launch event %d for %s", calendarEvent.getId(), calendarEvent.getLaunchId());
            calendarUtil.deleteEvent(context, calendarEvent.getId());
            mRealm.executeTransaction(realm -> calendarEvent.deleteFromRealm());
        }
        calendarUtil.deleteAll(context);
    }
}
