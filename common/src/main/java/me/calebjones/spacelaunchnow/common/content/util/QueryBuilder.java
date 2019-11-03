package me.calebjones.spacelaunchnow.common.content.util;

import android.content.Context;


import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.main.Launch;

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

    public static RealmResults<Launch> buildUpcomingSwitchQueryForCalendar(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        RealmQuery<Launch> query;
        if (switchPreferences.getAllSwitch()) {
            query = getAllUpcomingQueryForCalendar(context, realm);
        } else {
            query = getSortedUpcomingQueryForCalendar(context, realm);
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

    private static RealmQuery<Launch> getAllUpcomingQueryForCalendar(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        Calendar calendar = Calendar.getInstance();
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
            query.equalTo("rocket.configuration.manufacturer.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 111)
                    .or()
                    .equalTo("rocket.configuration.manufacturer.id", 163)
                    .or()
                    .equalTo("rocket.configuration.manufacturer.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 17);
            query.or();
            query.equalTo("pad.location.id", 19);
            query.or();
            query.equalTo("pad.location.id", 33);
            query.or();
            query.equalTo("pad.location.id", 16);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 31);
            query.or();
            query.equalTo("pad.location.id", 14);
        }

        if (switchPreferences.getSwitchBO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 141);
        }

        if (switchPreferences.getSwitchRL()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 147);
        }

        if (switchPreferences.getSwitchNorthrop()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 257);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 27);
            query.or();
            query.equalTo("pad.location.id", 12);
        }

        if (switchPreferences.getSwitchFG()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 13);
        }


        if (switchPreferences.getSwitchRussia()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 15);
            query.or();
            query.equalTo("pad.location.id", 5);
            query.or();
            query.equalTo("pad.location.id", 6);
            query.or();
            query.equalTo("pad.location.id", 18);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 11);
        }

        if (switchPreferences.getSwitchWallops()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 21);
        }

        if (switchPreferences.getSwitchNZ()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 10);
        }


        if (switchPreferences.getSwitchJapan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 9);
            query.or();
            query.equalTo("pad.location.id", 41);
        }

        query.endGroup();

        query.sort("net", Sort.ASCENDING);
        return query;
    }

    private static RealmQuery<Launch> getSortedUpcomingQueryForCalendar(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        boolean first = true;
        Calendar calendar = Calendar.getInstance();

        Date date = calendar.getTime();
        RealmQuery<Launch> query = realm.where(Launch.class).greaterThanOrEqualTo("net", date);
        query.findAll();

        if (switchPreferences.getTBDSwitch()) {
            query.notEqualTo("status.id", 2).findAll();
        }
        query.beginGroup();

        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.configuration.manufacturer.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 111)
                    .or()
                    .equalTo("rocket.configuration.manufacturer.id", 163)
                    .or()
                    .equalTo("rocket.configuration.manufacturer.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 17);
            query.or();
            query.equalTo("pad.location.id", 19);
            query.or();
            query.equalTo("pad.location.id", 33);
            query.or();
            query.equalTo("pad.location.id", 16);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 31);
            query.or();
            query.equalTo("pad.location.id", 14);
        }

        if (switchPreferences.getSwitchBO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 141);
        }

        if (switchPreferences.getSwitchRL()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 147);
        }

        if (switchPreferences.getSwitchNorthrop()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 257);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 27);
            query.or();
            query.equalTo("pad.location.id", 12);
        }

        if (switchPreferences.getSwitchFG()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 13);
        }


        if (switchPreferences.getSwitchRussia()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 15);
            query.or();
            query.equalTo("pad.location.id", 5);
            query.or();
            query.equalTo("pad.location.id", 6);
            query.or();
            query.equalTo("pad.location.id", 18);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 11);
        }

        if (switchPreferences.getSwitchWallops()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 21);
        }

        if (switchPreferences.getSwitchNZ()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 10);
        }


        if (switchPreferences.getSwitchJapan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 9);
            query.or();
            query.equalTo("pad.location.id", 41);
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
            query.equalTo("rocket.configuration.manufacturer.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 111)
                    .or()
                    .equalTo("rocket.configuration.manufacturer.id", 163)
                    .or()
                    .equalTo("rocket.configuration.manufacturer.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 17);
            query.or();
            query.equalTo("pad.location.id", 19);
            query.or();
            query.equalTo("pad.location.id", 33);
            query.or();
            query.equalTo("pad.location.id", 16);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 31);
            query.or();
            query.equalTo("pad.location.id", 14);
        }

        if (switchPreferences.getSwitchBO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 141);
        }

        if (switchPreferences.getSwitchRL()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 147);
        }

        if (switchPreferences.getSwitchNorthrop()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.configuration.manufacturer.id", 257);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 27);
            query.or();
            query.equalTo("pad.location.id", 12);
        }

        if (switchPreferences.getSwitchFG()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 13);
        }


        if (switchPreferences.getSwitchRussia()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("pad.location.id", 15);
            query.or();
            query.equalTo("pad.location.id", 5);
            query.or();
            query.equalTo("pad.location.id", 6);
            query.or();
            query.equalTo("pad.location.id", 18);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 11);
        }

        if (switchPreferences.getSwitchWallops()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 21);
        }

        if (switchPreferences.getSwitchNZ()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 10);
        }


        if (switchPreferences.getSwitchJapan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("pad.location.id", 9);
            query.or();
            query.equalTo("pad.location.id", 41);
        }

        query.endGroup();
        return query.findAll().sort("net", Sort.ASCENDING);
    }
}