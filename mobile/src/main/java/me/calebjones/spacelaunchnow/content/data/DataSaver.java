package me.calebjones.spacelaunchnow.content.data;

import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.LaunchNotification;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Mission;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import timber.log.Timber;

/**
 * This class is responsible for saving raw data to Realm.
 */


public class DataSaver {

    private Context context;
    private DataClientManager dataClientManager;
    public boolean isSaving = false;
    public boolean isSyncing = false;

    public DataSaver(Context context, DataClientManager dataClientManager) {
        this.context = context;
        this.dataClientManager = dataClientManager;
    }

    public DataSaver(Context context) {
        this.context = context;
    }

    public void saveObjectsToRealm(final RealmObject[] objects) {
        Realm mRealm = Realm.getDefaultInstance();
        final RealmList<RealmObject> realmList = new RealmList<>();
        if (objects != null) {
            Collections.addAll(realmList, objects);
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(realmList);
                }
            });
            mRealm.close();
        }
    }

    public void saveLaunchesToRealm(final Launch[] launches, final boolean mini) {
        isSaving = true;
        Realm mRealm = Realm.getDefaultInstance();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm mRealm) {

                if (mini) {
                    for (Launch item : launches) {
                        final Launch previous = mRealm.where(Launch.class)
                                .equalTo("id", item.getId())
                                .findFirst();
                        if (previous != null) {
                            if (isLaunchTimeChanged(previous, item)) {
                                Timber.i("%s status has changed.", item.getName());
                                final LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                                if (notification != null) {
                                    notification.resetNotifiers();
                                    mRealm.copyToRealmOrUpdate(notification);
                                }
                                previous.resetNotifiers();
                                mRealm.copyToRealmOrUpdate(previous);
                                dataClientManager.getLaunchById(item.getId());
                            }
                        }
                    }
                } else {
                    for (final Launch item : launches) {
                        final Launch previous = mRealm.where(Launch.class)
                                .equalTo("id", item.getId())
                                .findFirst();
                        if (item.getMissions() != null && item.getMissions().size() > 0){
                            Mission newMisison = item.getMissions().get(0);
                            RealmResults<Mission> missions = mRealm.where(Mission.class).equalTo("id", newMisison.getId()).findAll();
                            if  (missions != null && missions.size() > 0){
                                RealmList <Mission> results = new RealmList<Mission>();
                                results.addAll(missions.subList(0, missions.size()));
                                item.setMissions(results);
                            }
                        }
                        if (previous != null) {
                            if (isLaunchTimeChanged(previous, item)) {
                                final LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
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
                    }
                    mRealm.copyToRealmOrUpdate(Arrays.asList(launches));
                }
            }
        });
        syncNotifiers(mRealm);
        mRealm.close();
        isSaving = false;
    }

    private static boolean isLaunchTimeChanged(Launch previous, Launch item) {
        if ((Math.abs(previous.getNet().getTime() - item.getNet().getTime()) >= 3600)) {
            return true;
        } else if (previous.getStatus() != null && item.getStatus() != null && previous.getStatus().intValue() != item.getStatus().intValue()) {
            return true;
        }
        return false;
    }

    public void syncNotifiers(Realm mRealm) {
        isSyncing = true;
        RealmResults<Launch> launchRealms;
        Date date = new Date();

        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);

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
            Timber.d("%s - ERROR: %s", result.getAction(), result.getErrorMessage());

            broadcastIntent.putExtra("error", result.getErrorMessage());

            Crashlytics.log(result.getErrorMessage());

            Analytics.from(context).sendNetworkEvent(result.getAction(), result.getRequestURL(), result.isSuccessful(), result.getErrorMessage());

        } else if (!result.isSuccessful()) {
            Timber.d("%s - ERROR: Unknown - URL: %s", result.getAction(), result.getRequestURL());

            Crashlytics.log(result.getAction() + " - " + result.getRequestURL());

            broadcastIntent.putExtra("error", "Unknown error has occurred.");

            Analytics.from(context).sendNetworkEvent(result.getAction(), result.getRequestURL(), result.isSuccessful());
        }

        context.sendBroadcast(broadcastIntent);
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UpdateRecord updateRecord = new UpdateRecord();
                updateRecord.setType(result.getAction());
                updateRecord.setDate(new Date());
                updateRecord.setSuccessful(result.isSuccessful());
                realm.copyToRealmOrUpdate(updateRecord);
            }
        });
        mRealm.close();
    }

    public void saveLaunchesToRealm(final RealmList<Launch> launches) {
        isSaving = true;
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Launch item : launches) {

                    Launch previous = realm.where(Launch.class)
                            .equalTo("id", item.getId())
                            .findFirst();
                    if (previous != null) {
                        if (isLaunchTimeChanged(previous, item)) {
                            final LaunchNotification notification = realm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                            if (notification != null) {
                                notification.resetNotifiers();
                                realm.copyToRealmOrUpdate(notification);
                            }
                        }

                        item.setEventID(previous.getEventID());
                        item.setSyncCalendar(previous.syncCalendar());
                        item.setLaunchTimeStamp(previous.getLaunchTimeStamp());
                        item.setNotifiable(previous.isNotifiable());
                    }
                    if (item.getMissions() != null && item.getMissions().size() > 0){
                        Mission newMisison = item.getMissions().get(0);
                        RealmResults<Mission> missions = realm.where(Mission.class).equalTo("id", newMisison.getId()).findAll();
                        if  (missions != null && missions.size() > 0){
                            RealmList <Mission> results = new RealmList<Mission>();
                            results.addAll(missions.subList(0, missions.size()));
                            item.setMissions(results);
                        }
                    }
                    Timber.v("Saving item: %s", item.getName());
                    realm.copyToRealmOrUpdate(item);
                }
            }
        });
        syncNotifiers(mRealm);
        isSaving = false;
        mRealm.close();
    }

    public void saveLaunchesToRealmAsync(final RealmList<Launch> launches) {
        isSaving = true;
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Launch item : launches) {

                    Launch previous = realm.where(Launch.class)
                            .equalTo("id", item.getId())
                            .findFirst();
                    if (previous != null) {
                        if (isLaunchTimeChanged(previous, item)) {
                            final LaunchNotification notification = realm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                            if (notification != null) {
                                notification.resetNotifiers();
                                realm.copyToRealmOrUpdate(notification);
                            }
                        }
                        item.setEventID(previous.getEventID());
                        item.setSyncCalendar(previous.syncCalendar());
                        item.setLaunchTimeStamp(previous.getLaunchTimeStamp());
                        item.setNotifiable(previous.isNotifiable());
                    }
                    if (item.getMissions() != null && item.getMissions().size() > 0){
                        Mission newMisison = item.getMissions().get(0);
                        RealmResults<Mission> missions = realm.where(Mission.class).equalTo("id", newMisison.getId()).findAll();
                        if  (missions != null && missions.size() > 0){
                            RealmList <Mission> results = new RealmList<Mission>();
                            results.addAll(missions.subList(0, missions.size()));
                            item.setMissions(results);
                        }
                    }
                    Timber.v("Saving item: %s", item.getName());
                    realm.copyToRealmOrUpdate(item);
                }
            }
        });
        syncNotifiers(mRealm);
        isSaving = false;
        mRealm.close();
    }
}
