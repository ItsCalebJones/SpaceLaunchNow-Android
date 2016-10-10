package me.calebjones.spacelaunchnow.content.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.interfaces.LibraryRequestInterface;
import me.calebjones.spacelaunchnow.content.jobs.UpdateJob;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchNotification;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.models.realm.UpdateRecord;
import me.calebjones.spacelaunchnow.content.responses.launchlibrary.LaunchResponse;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class LaunchDataService extends BaseService {

    public LaunchDataService() {
        super("LaunchDataService");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        listPreference = ListPreferences.getInstance(getApplicationContext());
        switchPreferences = SwitchPreferences.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public static void startActionSyncNotifiers(Context context) {
        Intent intent = new Intent(context, LaunchDataService.class);
        intent.setAction(Strings.SYNC_NOTIFIERS);
        context.startService(intent);
        Timber.v("Sending Delete intent.");
    }

    public static void startActionUpdateNextLaunch(Context context) {
        Intent intent = new Intent(context, LaunchDataService.class);
        intent.setAction(Strings.ACTION_UPDATE_NEXT_LAUNCH);
        context.startService(intent);
        Timber.v("Sending Delete intent.");
    }

    public static void startActionBackground(Context context) {
        Intent intent = new Intent(context, LaunchDataService.class);
        intent.setAction(Strings.ACTION_UPDATE_BACKGROUND);
        context.startService(intent);
        Timber.v("Sending Delete intent.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Timber.d("LaunchDataService - Intent received:  %s ", intent.getAction());
            String action = intent.getAction();

            // Create a new empty instance of Realm
            mRealm = Realm.getDefaultInstance();

            if (BuildConfig.DEBUG) {

                Date now = new Date();
                int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));

                NotificationCompat.Builder mBuilder = new NotificationCompat
                        .Builder(getApplicationContext());
                NotificationManager mNotifyManager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                String msg = "Launch Data - Intent received - " + action;
                mBuilder.setContentTitle("Scheduling Update - ")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSmallIcon(R.drawable.ic_rocket_white)
                        .setContentText(msg);
                mNotifyManager.notify(id, mBuilder.build());
            }

            //Usually called on first launch
            if (Strings.ACTION_GET_ALL_WIFI.equals(action)) {
                Timber.v("Intent action received: %s", action);
                if (this.sharedPref.getBoolean("background", true)) {
                    scheduleLaunchUpdates();
                }

                if (getUpcomingLaunches(this)) {
                    if (getLaunchesByDate("1950-01-01", Utils.getEndDate(this))) {
                        Intent rocketIntent = new Intent(getApplicationContext(), VehicleDataService.class);
                        rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
                        startService(rocketIntent);

                        startService(new Intent(this, MissionDataService.class));
                        this.startService(new Intent(this, NextLaunchTracker.class));
                    }
                }
            } else if (Strings.ACTION_UPDATE_LAUNCH.equals(action)) {
                int id = intent.getIntExtra("launchID", 0);
                if (id > 0) {
                    Timber.v("Updating launch id: %s", id);
                    getLaunchById(id, this);
                }
                syncNotifiers(this);

                // Called from NextLaunchFragment
            } else if (Strings.ACTION_GET_UP_LAUNCHES.equals(action)) {

                Timber.v("Intent action received: %s", action);
                if (this.sharedPref.getBoolean("background", true)) {
                    scheduleLaunchUpdates();
                }
                getUpcomingLaunches(this);
                syncNotifiers(this);
                this.startService(new Intent(this, NextLaunchTracker.class));

                // Called from PrevLaunchFragment
            } else if (Strings.ACTION_GET_PREV_LAUNCHES.equals(action)) {

                Timber.v("Intent action received: %s", action);
                if (intent.getStringExtra("startDate") != null && intent.getStringExtra("endDate") != null) {
                    getLaunchesByDate(intent.getStringExtra("startDate"), intent.getStringExtra("endDate"));
                } else {
                    getLaunchesByDate("1950-01-01", Utils.getEndDate(this));
                }

            } else if (Strings.ACTION_UPDATE_NEXT_LAUNCH.equals(action)) {

                Timber.v("Intent action received: %s", action);
                getNextLaunches(this);
                syncNotifiers(this);

                this.startService(new Intent(this, NextLaunchTracker.class));

            } else if (Strings.SYNC_NOTIFIERS.equals(action)) {

                Timber.v("Intent action received: %s", action);
                syncNotifiers(this);

            } else if (Strings.ACTION_UPDATE_BACKGROUND.equals(action)) {

                Timber.v("Intent action received: %s", action);
                syncBackground(this);

            }
        } else {

            Timber.e("LaunchDataService - onHandleIntent: ERROR - Unknown Intent");

        }
        Timber.v("Finished!");
        mRealm.close();
    }

    private void syncBackground(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean wifiOnly = sharedPref.getBoolean("wifi_only", false);
        boolean dataSaver = sharedPref.getBoolean("data_saver", false);
        boolean wifiConnected = Connectivity.isConnectedWifi(context);

        if (wifiOnly) {
            if (wifiConnected) {
                getUpcomingLaunches(context);
                syncNotifiers(context);

                //TODO check time since last full sync and respond

            }
        } else if (dataSaver && !wifiConnected) {
            getNextLaunches(context);

            //TODO check time since last full sync and respond

        } else {
            getUpcomingLaunches(context);

            //TODO check time since last full sync and respond

        }
    }

    private static void syncNotifiers(Context context) {
        RealmResults<LaunchRealm> launchRealms;
        Date date = new Date();

        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        Realm mRealm = Realm.getDefaultInstance();

        if (switchPreferences.getAllSwitch()) {
            launchRealms = mRealm.where(LaunchRealm.class)
                    .greaterThanOrEqualTo("net", date)
                    .findAllSorted("net", Sort.ASCENDING);
        } else {
            launchRealms = QueryBuilder.buildSwitchQuery(context, mRealm);
        }

        for (final LaunchRealm launchrealm : launchRealms) {
            if (!launchrealm.isUserToggledNotifiable() && !launchrealm.isNotifiable()) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        launchrealm.setNotifiable(true);
                    }
                });
            }
        }
    }

    private boolean getLaunchesByDate(String startDate, String endDate) {
        LibraryRequestInterface request = retrofit.create(LibraryRequestInterface.class);
        Call<LaunchResponse> call;
        Response<LaunchResponse> launchResponse;
        RealmList<LaunchRealm> items = new RealmList<>();
        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugLaunchesByDate(startDate, endDate, offset);
                } else {
                    call = request.getLaunchesByDate(startDate, endDate, offset);
                }
                launchResponse = call.execute();
                if (launchResponse.isSuccessful()) {
                    total = launchResponse.body().getTotal();
                    count = launchResponse.body().getCount();
                    offset = offset + count;
                    Timber.v("LaunchesByDate Count: %s", offset);
                    Collections.addAll(items, launchResponse.body().getLaunches());
                } else {
                    throw new IOException(launchResponse.errorBody().string());
                }
            }
            for (LaunchRealm item : items) {
                item.getLocation().setPrimaryID();
            }
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(items);
            mRealm.commitTransaction();

            Timber.v("Success!");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
            this.sendBroadcast(broadcastIntent);
            return true;

        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Strings.ACTION_FAILURE_PREV_LAUNCHES);
            this.sendBroadcast(broadcastIntent);
            return false;
        }
    }

    private static boolean getUpcomingLaunches(Context context) {
        LibraryRequestInterface request = getRetrofit().create(LibraryRequestInterface.class);
        Call<LaunchResponse> call;
        Response<LaunchResponse> launchResponse;
        RealmList<LaunchRealm> items = new RealmList<>();

        Realm mRealm = Realm.getDefaultInstance();

        ListPreferences listPreference = ListPreferences.getInstance(context);

        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugUpcomingLaunches(offset);
                } else {
                    call = request.getUpcomingLaunches(offset);
                }
                launchResponse = call.execute();
                if (launchResponse.isSuccessful()) {
                    total = launchResponse.body().getTotal();
                    count = launchResponse.body().getCount();
                    offset = offset + count;
                    Timber.v("UpcomingLaunches Count: %s", offset);
                    Collections.addAll(items, launchResponse.body().getLaunches());
                } else {
                    throw new IOException(launchResponse.errorBody().string());
                }
            }
            for (LaunchRealm item : items) {
                LaunchRealm previous = mRealm.where(LaunchRealm.class)
                        .equalTo("id", item.getId())
                        .findFirst();
                if (previous != null) {
                    Timber.v("UpcomingLaunches updating items: %s", previous.getName());
                    item.setEventID(previous.getEventID());
                    item.setSyncCalendar(previous.syncCalendar());
                    item.setLaunchTimeStamp(previous.getLaunchTimeStamp());
                    item.setIsNotifiedDay(previous.getIsNotifiedDay());
                    item.setIsNotifiedHour(previous.getIsNotifiedHour());
                    item.setIsNotifiedTenMinute(previous.getIsNotifiedTenMinute());
                }
                item.getLocation().setPrimaryID();
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(item);
                mRealm.commitTransaction();
            }

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(Strings.ACTION_GET_UP_LAUNCHES);
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(true);
                    realm.copyToRealmOrUpdate(updateRecord);
                }
            });

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
            context.getApplicationContext().sendBroadcast(broadcastIntent);
            return true;
        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(Strings.ACTION_GET_UP_LAUNCHES);
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(true);
                    realm.copyToRealmOrUpdate(updateRecord);
                }
            });

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Strings.ACTION_FAILURE_UP_LAUNCHES);
            context.getApplicationContext().sendBroadcast(broadcastIntent);
            return false;
        }
    }

    private static void getNextLaunches(Context context) {
        LibraryRequestInterface request = getRetrofit().create(LibraryRequestInterface.class);
        Call<LaunchResponse> call;
        Response<LaunchResponse> launchResponse;
        RealmList<LaunchRealm> items = new RealmList<>();

        Realm mRealm = Realm.getDefaultInstance();

        ListPreferences listPreference = ListPreferences.getInstance(context);

        try {
            if (listPreference.isDebugEnabled()) {
                call = request.getDebugMiniNextLaunch();
            } else {
                call = request.getMiniNextLaunch();
            }
            launchResponse = call.execute();
            if (launchResponse.isSuccessful()) {
                Collections.addAll(items, launchResponse.body().getLaunches());
            } else {
                throw new IOException();
            }
            for (LaunchRealm item : items) {
                LaunchRealm previous = mRealm.where(LaunchRealm.class)
                        .equalTo("id", item.getId())
                        .findFirst();
                if (previous != null) {
                    if ((!previous.getNet().equals(item.getNet()) || (previous.getStatus().intValue() != item.getStatus().intValue()))) {
                        Timber.v("%s status has changed.", item.getName());
                        LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                        mRealm.beginTransaction();
                        if (notification != null) {
                            notification.resetNotifiers();
                        }
                        previous.resetNotifiers();
                        mRealm.copyToRealmOrUpdate(notification);
                        mRealm.copyToRealmOrUpdate(previous);
                        mRealm.commitTransaction();
                        getLaunchById(item.getId(), context);
                    }
                }
            }

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(Strings.ACTION_UPDATE_NEXT_LAUNCH);
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(true);
                    realm.copyToRealmOrUpdate(updateRecord);
                }
            });

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
            context.sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(Strings.ACTION_UPDATE_NEXT_LAUNCH);
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(false);
                    realm.copyToRealmOrUpdate(updateRecord);
                }
            });

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Strings.ACTION_FAILURE_UP_LAUNCHES);
            context.sendBroadcast(broadcastIntent);
        }
    }

    private static void getLaunchById(int id, Context context) {
        LibraryRequestInterface request = getRetrofit().create(LibraryRequestInterface.class);
        Call<LaunchResponse> call;

        Realm mRealm = Realm.getDefaultInstance();

        ListPreferences listPreference = ListPreferences.getInstance(context);

        if (listPreference.isDebugEnabled()) {
            call = request.getDebugLaunchByID(id);
        } else {
            call = request.getLaunchByID(id);
        }

        Response<LaunchResponse> launchResponse;
        try {
            launchResponse = call.execute();
            if (launchResponse.isSuccessful()) {
                RealmList<LaunchRealm> items = new RealmList<>(launchResponse.body().getLaunches());
                for (LaunchRealm item : items) {
                    LaunchRealm previous = mRealm.where(LaunchRealm.class)
                            .equalTo("id", item.getId())
                            .findFirst();
                    if (previous != null) {
                        item.setEventID(previous.getEventID());
                        item.setSyncCalendar(previous.syncCalendar());
                        item.setLaunchTimeStamp(previous.getLaunchTimeStamp());
                        item.setIsNotifiedDay(previous.getIsNotifiedDay());
                        item.setIsNotifiedHour(previous.getIsNotifiedHour());
                        item.setIsNotifiedTenMinute(previous.getIsNotifiedTenMinute());
                    }
                    mRealm.beginTransaction();
                    item.getLocation().setPrimaryID();
                    mRealm.copyToRealmOrUpdate(item);
                    mRealm.commitTransaction();
                    Timber.v("Updated launch: %s", item.getId());
                }
            }
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_SUCCESS_LAUNCH);
            context.sendBroadcast(broadcastIntent);
        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Strings.ACTION_FAILURE_LAUNCH);
            context.sendBroadcast(broadcastIntent);
        }
    }

    public void scheduleLaunchUpdates() {
        Timber.d("LaunchDataService - scheduleLaunchUpdates");
        UpdateJob.scheduleJob(this);
    }
}
