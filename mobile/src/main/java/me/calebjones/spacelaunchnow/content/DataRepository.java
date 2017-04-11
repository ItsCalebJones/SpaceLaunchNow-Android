package me.calebjones.spacelaunchnow.content;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.text.ParseException;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.realm.Launch;
import me.calebjones.spacelaunchnow.data.models.realm.Location;
import me.calebjones.spacelaunchnow.data.models.realm.Mission;
import timber.log.Timber;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class DataRepository {

    private Context context;

    public DataRepository(Context context) {
        this.context = context;
    }

    public RealmResults<Launch> getPreviousLaunchData(Realm realm) {
        Timber.v("Syncing launch data...");
        if (ListPreferences.getInstance(context).isFresh()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Launch> launches = null;
                    try {
                        launches = QueryBuilder.buildPrevQuery(context, realm);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (ListPreferences.getInstance(context).isFresh()) {
                        for (Launch launch : launches) {
                            Timber.d("Syncing launch %s for mission/location data.", launch.getName());
                            if (launch.getMissions().size() == 0) {
                                RealmResults<Mission> missions = realm.where(Mission.class).equalTo("launch.id", launch.getId()).findAll();
                                if (missions.size() > 0) {
                                    Timber.v("Matched launch %s with %s mission", launch.getId(), missions.get(0).getId());
                                    final RealmList<Mission> results = new RealmList<Mission>();
                                    results.addAll(missions.subList(0, missions.size()));
                                    launch.setMissions(results);
                                    realm.copyToRealmOrUpdate(launch);
                                } else {
                                    Timber.v("Unable to match Launch %s to a mission.", launch.getId());
                                }
                            }
                            if (launch.getLocation() == null) {
                                Location location = realm.where(Location.class).equalTo("pads.id", launch.getLocationid()).findFirst();
                                if (location != null) {
                                    Timber.v("Matched launch %s with %s location", launch.getId(), location.getName());
                                    launch.setLocation(location);
                                    realm.copyToRealmOrUpdate(launch);
                                } else {
                                    Timber.v("Unable to match Launch %s to a location.", launch.getId());
                                }
                            }
                        }
                        realm.copyToRealmOrUpdate(launches);
                        ListPreferences.getInstance(context).isFresh(false);
                    }
                }
            });
        }
        try {
            return QueryBuilder.buildPrevQueryAsync(context, realm);
        } catch (ParseException e) {
            Crashlytics.logException(e);
            return null;
        }
    }
}


