package me.calebjones.spacelaunchnow.content.util;

import android.content.Context;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import timber.log.Timber;

public class QueryBuilder {
    public static boolean first = true;

    public static RealmResults<Launch> buildUpcomingSwitchQuery(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        RealmQuery<Launch> query;
        if (switchPreferences.getAllSwitch()) {
            query = getAllUpcomingQuery(context, realm);
        } else {
            query = getSortedUpcomingQuery(context, realm);
        }
        return query.findAll();
    }

    private static RealmQuery<Launch> getAllUpcomingQuery(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        Calendar calendar = Calendar.getInstance();
        if (switchPreferences.getPersistSwitch()) {
            calendar.add(Calendar.HOUR_OF_DAY, -24);
        }
        Date date = calendar.getTime();
        RealmQuery<Launch> query = realm.where(Launch.class)
                .greaterThanOrEqualTo("net", date);
        if (switchPreferences.getTBDSwitch()) {
            query.notEqualTo("status.id", 2);
            query.findAll();
        }
        query.sort("net", Sort.ASCENDING);
        return query;
    }

    private static RealmQuery<Launch> getSortedUpcomingQuery(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        boolean first = true;
        Calendar calendar = Calendar.getInstance();
        if (switchPreferences.getPersistSwitch()) {
            calendar.add(Calendar.HOUR_OF_DAY, -24);
        }

        Date date = calendar.getTime();
        RealmQuery<Launch> query = realm.where(Launch.class)
                .greaterThanOrEqualTo("net", date);
        query.findAll();

        if (switchPreferences.getTBDSwitch()) {
            query.notEqualTo("status.id", 2).findAll();
        }
        query.beginGroup();

        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.configuration.launchServiceProvider.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 111)
                    .or()
                    .equalTo("rocket.configuration.launchServiceProvider.id", 163)
                    .or()
                    .equalTo("rocket.configuration.launchServiceProvider.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 88);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 31);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 17);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 16);
        }

        if (switchPreferences.getSwitchPles()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 11);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 18);
        }

        query.endGroup();

        query.sort("net", Sort.ASCENDING);
        return query;
    }


    public static RealmResults<Launch> buildSwitchQuery(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        boolean first = true;
        Date date = new Date();
        RealmQuery<Launch> query = realm.where(Launch.class)
                .greaterThanOrEqualTo("net", date);

        query.findAll();

        if (switchPreferences.getTBDSwitch()) {
            query.notEqualTo("status.id", 2).findAll();
        }

        query.beginGroup();

        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.configuration.launchServiceProvider.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 121);
        }


        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 111)
                    .or()
                    .equalTo("rocket.configuration.launchServiceProvider.id", 163)
                    .or()
                    .equalTo("rocket.configuration.launchServiceProvider.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 88);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 31);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 17);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 16);
        }

        if (switchPreferences.getSwitchPles()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 11);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 18);
        }

        query.endGroup();
        return query.findAll().sort("net", Sort.ASCENDING);
    }

    public static RealmResults<Launch> buildSwitchQuery(Context context, Realm realm, boolean calendarState) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        boolean first = true;
        Date date = new Date();
        RealmQuery<Launch> query = realm.where(Launch.class)
                .greaterThanOrEqualTo("net", date).equalTo("syncCalendar", calendarState);

        query.findAll();

        if (switchPreferences.getTBDSwitch()) {
            query.notEqualTo("status.id", 2).findAll();
        }

        query.beginGroup();

        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.configuration.launchServiceProvider.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 111)
                    .or()
                    .equalTo("rocket.configuration.launchServiceProvider.id", 163)
                    .or()
                    .equalTo("rocket.configuration.launchServiceProvider.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 88);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.launchServiceProvider.id", 31);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 17);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 16);
        }

        if (switchPreferences.getSwitchPles()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 11);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 18);
        }

        query.endGroup();
        query.sort("net", Sort.ASCENDING);
        return query.findAll();
    }
}
