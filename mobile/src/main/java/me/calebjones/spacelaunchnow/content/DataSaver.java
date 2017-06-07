package me.calebjones.spacelaunchnow.content;

import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;

import java.util.Collections;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.data.models.LaunchNotification;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import timber.log.Timber;

public class DataSaver {

    private Context context;
    private DataManager dataManager;
    public boolean isSaving = false;
    public boolean isSyncing = false;

    public DataSaver(Context context, DataManager dataManager) {
        this.context = context;
        this.dataManager = dataManager;
    }

    public DataSaver(Context context) {
        this.context = context;
    }

    public void saveObjectsToRealm(final RealmObject[] objects) {
        Realm mRealm = Realm.getDefaultInstance();
        final RealmList<RealmObject> realmList = new RealmList<>();
        Collections.addAll(realmList, objects);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(realmList);
            }
        });
        mRealm.close();
    }

    public void saveLaunchesToRealm(Launch[] launches, boolean mini) {
        isSaving = true;
        Realm mRealm = Realm.getDefaultInstance();

        if (mini) {
            for (Launch item : launches) {
                Launch previous = mRealm.where(Launch.class)
                        .equalTo("id", item.getId())
                        .findFirst();
                if (previous != null) {
                    if (isLaunchTimeChanged(previous, item)) {
                        Timber.v("%s status has changed.", item.getName());
                        LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                        mRealm.beginTransaction();
                        if (notification != null) {
                            notification.resetNotifiers();
                            mRealm.copyToRealmOrUpdate(notification);
                        }
                        previous.resetNotifiers();
                        mRealm.copyToRealmOrUpdate(previous);
                        mRealm.commitTransaction();
                        dataManager.getLaunchById(item.getId());
                    }
                }
            }
        } else {
            for (Launch item : launches) {
                mRealm.beginTransaction();
                Launch previous = mRealm.where(Launch.class)
                        .equalTo("id", item.getId())
                        .findFirst();
                if (previous != null) {
                    if (isLaunchTimeChanged(previous, item)) {
                        LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                        if (notification != null) {
                            notification.resetNotifiers();
                            mRealm.copyToRealmOrUpdate(notification);
                        }
                    }
                    item.setEventID(previous.getEventID());
                    item.setSyncCalendar(previous.syncCalendar());
                    item.setLaunchTimeStamp(previous.getLaunchTimeStamp());
                    item.setNotifiable(previous.isNotifiable());
                }
                if (item.getLocation() != null) {
                    item.getLocation().setPrimaryID();
                }
                Timber.v("Saving item: %s", item.getName());
                mRealm.copyToRealmOrUpdate(item);
                mRealm.commitTransaction();
            }
        }
        syncNotifiers();
        isSaving = false;
    }

    public void saveLaunchesToRealm(Launch[] launches) {
        isSaving = true;
        Realm mRealm = Realm.getDefaultInstance();

        for (Launch item : launches) {
            mRealm.beginTransaction();
            Launch previous = mRealm.where(Launch.class)
                    .equalTo("id", item.getId())
                    .findFirst();
            if (previous != null) {
                if (isLaunchTimeChanged(previous, item)) {
                    LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                    if (notification != null) {
                        notification.resetNotifiers();
                        mRealm.copyToRealmOrUpdate(notification);
                    }
                }
                item.setEventID(previous.getEventID());
                item.setSyncCalendar(previous.syncCalendar());
                item.setLaunchTimeStamp(previous.getLaunchTimeStamp());
                item.setNotifiable(previous.isNotifiable());
            }
            if (item.getLocation() != null) {
                item.getLocation().setPrimaryID();
            }
            Timber.v("Saving item: %s", item.getName());
            mRealm.copyToRealmOrUpdate(item);
            mRealm.commitTransaction();
        }
        syncNotifiers();
        isSaving = false;
    }

    public static boolean isLaunchTimeChanged(Launch previous, Launch item) {
        if ((Math.abs(previous.getNet().getTime() - item.getNet().getTime()) >= 3600)) {
            return true;
        } else if (previous.getStatus().intValue() != item.getStatus().intValue()) {
            return true;
        }
        return false;
    }

    public void syncNotifiers() {
        isSyncing = true;
        RealmResults<Launch> launchRealms;
        Date date = new Date();

        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        Realm mRealm = Realm.getDefaultInstance();

        if (switchPreferences.getAllSwitch()) {
            launchRealms = mRealm.where(Launch.class)
                    .greaterThanOrEqualTo("net", date)
                    .findAllSorted("net", Sort.ASCENDING);
        } else {
            launchRealms = QueryBuilder.buildSwitchQuery(context, mRealm);
        }

        for (final Launch launchRealm : launchRealms) {
            if (!launchRealm.isUserToggledNotifiable() && !launchRealm.isNotifiable()) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        launchRealm.setNotifiable(true);
                    }
                });
            }
        }
        mRealm.close();
        isSyncing = false;
    }

    public void sendResult(final Result result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(result.getAction());
        broadcastIntent.putExtra("result", result.isSuccessful());

        if (result.isSuccessful()) {
            Timber.i("%s - Successful: %s", result.getAction(), result.isSuccessful());

            Analytics.from(context).sendNetworkEvent(result.getAction(), result.getRequestURL(), result.isSuccessful());

        } else if (!result.isSuccessful() && result.getErrorMessage() != null) {
            Timber.e("%s - ERROR: %s", result.getAction(), result.getErrorMessage());

            broadcastIntent.putExtra("error", result.getErrorMessage());

            Crashlytics.log(result.getErrorMessage());

            Analytics.from(context).sendNetworkEvent(result.getAction(), result.getRequestURL(), result.isSuccessful(), result.getErrorMessage());

        } else if (!result.isSuccessful()) {
            Timber.e("%s - ERROR: Unknown - URL: %s", result.getAction(), result.getRequestURL());

            Crashlytics.log(result.getAction() + " - " + result.getRequestURL());

            broadcastIntent.putExtra("error", "Unknown error has occurred.");

            Analytics.from(context).sendNetworkEvent(result.getAction(), result.getRequestURL(), result.isSuccessful());
        }

        context.sendBroadcast(broadcastIntent);

        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UpdateRecord updateRecord = new UpdateRecord();
                updateRecord.setType(result.getAction());
                updateRecord.setDate(new Date());
                updateRecord.setSuccessful(result.isSuccessful());
                realm.copyToRealmOrUpdate(updateRecord);
            }
        });
    }
}
