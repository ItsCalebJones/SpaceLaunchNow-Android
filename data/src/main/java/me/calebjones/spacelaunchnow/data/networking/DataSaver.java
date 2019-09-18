package me.calebjones.spacelaunchnow.data.networking;

import android.content.Context;
import android.content.Intent;


import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.LaunchNotification;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.AstronautFlight;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;
import timber.log.Timber;

/**
 * This class is responsible for saving raw data to Realm.
 */


public class DataSaver {

    private Context context;
    public boolean isSaving = false;
    public boolean isSyncing = false;

    public DataSaver(Context context) {
        this.context = context;
    }

    public void saveObjectsToRealm(final RealmObject[] objects) {
        Realm mRealm = Realm.getDefaultInstance();
        final RealmList<RealmObject> realmList = new RealmList<>();
        if (objects != null) {
            Collections.addAll(realmList, objects);
            mRealm.executeTransaction(realm -> realm.copyToRealmOrUpdate(realmList));
            mRealm.close();
        }
    }

    public void saveLaunchesToRealm(final List<Launch> launches, final boolean mini) {
        isSaving = true;
        Timber.v("Saving %d launches to Realm.", launches.size());
        Realm mRealm = Realm.getDefaultInstance();
        for (Launch launch : launches) {
            removeAgencyFields(launch, mRealm);
        }

        mRealm.executeTransaction((Realm mRealm1) -> mRealm1.copyToRealmOrUpdate(launches));
        mRealm.close();
        isSaving = false;
    }

    private Launch removeAgencyFields(Launch launch, Realm mRealm) {
        if (launch.getRocket() != null) {
            if (launch.getRocket().getSpacecraftStage() != null) {
                SpacecraftStage spacecraftStage = launch.getRocket().getSpacecraftStage();
                if (spacecraftStage.getSpacecraft() != null
                        && spacecraftStage.getSpacecraft().getConfiguration() != null
                        && spacecraftStage.getSpacecraft().getConfiguration().getAgency() != null) {
                    Agency spacecraftAgency = launch.getRocket().getSpacecraftStage().getSpacecraft().getConfiguration().getAgency();
                    if (spacecraftAgency != null) {
                        if (spacecraftAgency.getAbbrev() == null) {
                            Agency previous = mRealm.where(Agency.class).equalTo("id", spacecraftAgency.getId()).findFirst();
                            if (previous != null) {
                                if (previous.getAbbrev() != null) {
                                    launch.getRocket().getSpacecraftStage().getSpacecraft().getConfiguration().setAgency(previous);
                                } else {
                                    launch.getRocket().getSpacecraftStage().getSpacecraft().getConfiguration().setAgency(null);
                                }
                            }
                        }
                    }
                }
                if (spacecraftStage.getLandingCrew() != null) {
                    for (AstronautFlight astronautFlight : spacecraftStage.getLandingCrew()) {
                        if (astronautFlight.getAstronaut().getAgency() != null) {
                            astronautFlight.getAstronaut().setAgency(null);
                        }
                    }
                }
                if (spacecraftStage.getLaunchCrew() != null) {
                    for (AstronautFlight astronautFlight : spacecraftStage.getLaunchCrew()) {
                        if (astronautFlight.getAstronaut().getAgency() != null) {
                            astronautFlight.getAstronaut().setAgency(null);
                        }
                    }
                }
                if (spacecraftStage.getOnboardCrew() != null) {
                    for (AstronautFlight astronautFlight : spacecraftStage.getOnboardCrew()) {
                        if (astronautFlight.getAstronaut().getAgency() != null) {
                            astronautFlight.getAstronaut().setAgency(null);
                        }
                    }
                }
            }
        }
        return launch;
    }

    private static boolean isLaunchTimeChanged(Launch previous, Launch item) {
        if ((Math.abs(previous.getNet().getTime() - item.getNet().getTime()) >= 360)) {
            return true;
        } else if (previous.getStatus() != null && item.getStatus() != null && previous.getStatus().getId().intValue() != item.getStatus().getId().intValue()) {
            return true;
        }
        return false;
    }

    public void sendResult(final Result result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(result.getAction());
        broadcastIntent.putExtra("result", result.isSuccessful());

        if (result.isSuccessful()) {
            Timber.i("%s - Successful: %s", result.getAction(), result.isSuccessful());

        } else if (!result.isSuccessful() && result.getErrorMessage() != null) {
            Timber.d("%s - ERROR: %s", result.getAction(), result.getErrorMessage());

            broadcastIntent.putExtra("error", result.getErrorMessage());

        } else if (!result.isSuccessful()) {
            Timber.d("%s - ERROR: Unknown - URL: %s", result.getAction(), result.getRequestURL());
            broadcastIntent.putExtra("error", "Unknown error has occurred.");
        }

        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(realm -> {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(result.getAction());
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(result.isSuccessful());
                    realm.copyToRealmOrUpdate(updateRecord);
                }
        );
        mRealm.close();
    }

    public void saveLaunchesToRealm(final List<Launch> launches) {
        isSaving = true;
        if (launches != null) {
            Realm mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(mRealm1 -> {
                Date now = Calendar.getInstance().getTime();
                for (final Launch item : launches) {
                    final Launch previous = mRealm1.where(Launch.class)
                            .equalTo("id", item.getId())
                            .findFirst();
                    if (previous != null) {
                        if (isLaunchTimeChanged(previous, item)) {
                            final LaunchNotification notification = mRealm1.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                            if (notification != null) {

                                notification.resetNotifiers();
                                mRealm1.copyToRealmOrUpdate(notification);
                            }
                        }
                        item.setLastUpdate(now);
                        item.setEventID(previous.getEventID());
                    }
                    Timber.v("Saving item: %s", item.getName());
                    mRealm1.copyToRealmOrUpdate(item);
                }
                mRealm1.copyToRealmOrUpdate(launches);
            });
            mRealm.close();
        }
        isSaving = false;
    }

    public void saveLaunchesToRealmAsync(final List<Launch> launches) {
        isSaving = true;
        if (launches != null) {
            Realm mRealm = Realm.getDefaultInstance();
            mRealm.executeTransactionAsync(realm -> {
                Date now = Calendar.getInstance().getTime();
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
                    }
                    item.setLastUpdate(now);
                    Timber.v("Saving item: %s", item.getName());
                    realm.copyToRealmOrUpdate(item);
                }
            });
            mRealm.close();
        }
        isSaving = false;
    }

    public void deleteLaunch(String id) {
        Realm mRealm = Realm.getDefaultInstance();
        Launch previous = mRealm.where(Launch.class)
                .equalTo("id", id)
                .findFirst();
        if (previous != null) {
            mRealm.executeTransaction(realm -> previous.deleteFromRealm());
        }
    }

    public void saveLaunchToRealm(Launch launch) {
        Realm mRealm = Realm.getDefaultInstance();
        Date now = Calendar.getInstance().getTime();
        final Launch previous = mRealm.where(Launch.class)
                .equalTo("id", launch.getId())
                .findFirst();
        if (previous != null) {
            if (isLaunchTimeChanged(previous, launch)) {
                final LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", launch.getId()).findFirst();
                if (notification != null) {

                    notification.resetNotifiers();
                    mRealm.copyToRealmOrUpdate(notification);
                }
            }
            launch.setLastUpdate(now);
            launch.setEventID(previous.getEventID());
        }
        Launch finalLaunch = removeAgencyFields(launch, mRealm);
        mRealm.executeTransaction(mRealm1 -> mRealm1.copyToRealmOrUpdate(finalLaunch));
        mRealm.close();
    }
}
