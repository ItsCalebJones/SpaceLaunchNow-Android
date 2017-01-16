package me.calebjones.spacelaunchnow.content.util;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchRealm;
import timber.log.Timber;

public class QueryBuilder {
    public static boolean first = true;

    public static RealmResults<LaunchRealm> buildPrevQueryAsync(Context context, Realm realm) throws ParseException {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        ListPreferences listPreferences = ListPreferences.getInstance(context);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String start = listPreferences.getStartDate();
        String end = listPreferences.getEndDate();

        Date sDate;
        Date eDate;

        sDate = df.parse(start);
        eDate = df.parse(end);

        RealmQuery<LaunchRealm> query = realm.where(LaunchRealm.class).between("net", sDate, eDate).findAll().where();

        Integer[] countryFilter = switchPreferences.getPrevCountryFiltered();
        Integer[] agencyFilter = switchPreferences.getPrevAgencyFiltered();
        Integer[] locationFilter = switchPreferences.getPrevLocationFiltered();
        Integer[] vehicleFilter = switchPreferences.getPrevVehicleFiltered();


        if (countryFilter != null && countryFilter.length > 0) {
            query = filterCountry(query, switchPreferences.getPrevCountryFilteredArray()).findAll().where();
        }

        if (agencyFilter != null && agencyFilter.length > 0) {
            query = filterAgency(query, switchPreferences.getPrevAgencyFilteredArray()).findAll().where();
        }

        if (locationFilter != null && locationFilter.length > 0) {
            query = filterLocation(query, switchPreferences.getPrevLocationFilteredArray()).findAll().where();
        }

        if (vehicleFilter != null && vehicleFilter.length > 0) {
            query = filterVehicle(query, switchPreferences.getPrevVehicleFilteredArray());
        }

        Timber.v("Returning Query");
        return query.findAllSortedAsync("net", Sort.DESCENDING);
    }

    public static RealmResults<LaunchRealm> buildPrevQuery(Context context, Realm realm) throws ParseException {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        ListPreferences listPreferences = ListPreferences.getInstance(context);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String start = listPreferences.getStartDate();
        String end = listPreferences.getEndDate();

        Date sDate;
        Date eDate;

        sDate = df.parse(start);
        eDate = df.parse(end);

        RealmQuery<LaunchRealm> query = realm.where(LaunchRealm.class).between("net", sDate, eDate).findAll().where();

        Integer[] countryFilter = switchPreferences.getPrevCountryFiltered();
        Integer[] agencyFilter = switchPreferences.getPrevAgencyFiltered();
        Integer[] locationFilter = switchPreferences.getPrevLocationFiltered();
        Integer[] vehicleFilter = switchPreferences.getPrevVehicleFiltered();


        if (countryFilter != null && countryFilter.length > 0) {
            query = filterCountry(query, switchPreferences.getPrevCountryFilteredArray()).findAll().where();
        }

        if (agencyFilter != null && agencyFilter.length > 0) {
            query = filterAgency(query, switchPreferences.getPrevAgencyFilteredArray()).findAll().where();
        }

        if (locationFilter != null && locationFilter.length > 0) {
            query = filterLocation(query, switchPreferences.getPrevLocationFilteredArray()).findAll().where();
        }

        if (vehicleFilter != null && vehicleFilter.length > 0) {
            query = filterVehicle(query, switchPreferences.getPrevVehicleFilteredArray());
        }

        Timber.v("Returning Query");
        return query.findAllSorted("net", Sort.DESCENDING);
    }

    public static RealmResults<LaunchRealm> buildUpQueryAsync(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);

        Date date = new Date();

        RealmQuery<LaunchRealm> query = realm.where(LaunchRealm.class).greaterThanOrEqualTo("net", date).findAll().where();

        Integer[] countryFilter = switchPreferences.getUpCountryFiltered();
        Integer[] agencyFilter = switchPreferences.getUpAgencyFiltered();
        Integer[] locationFilter = switchPreferences.getUpLocationFiltered();
        Integer[] vehicleFilter = switchPreferences.getUpVehicleFiltered();

        if (countryFilter != null && countryFilter.length > 0) {
            ArrayList<String> array = switchPreferences.getUpCountryFilteredArray();
            if (array != null && array.size() > 0) {
                query = filterCountry(query, array).findAll().where();
            } else {
                Crashlytics.logException(new Throwable("Error - array is null."));
            }
        }

        if (agencyFilter != null && agencyFilter.length > 0) {
            query = filterAgency(query, switchPreferences.getUpAgencyFilteredArray()).findAll().where();
        }

        if (locationFilter != null && locationFilter.length > 0) {
            query = filterLocation(query, switchPreferences.getUpLocationFilteredArray()).findAll().where();
        }

        if (vehicleFilter != null && vehicleFilter.length > 0) {
            query = filterVehicle(query, switchPreferences.getUpVehicleFilteredArray());

        }

        Timber.v("Returning Query");
        return query.findAllSortedAsync("net", Sort.ASCENDING);
    }

    public static RealmResults<LaunchRealm> buildUpQuery(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);

        Date date = new Date();

        RealmQuery<LaunchRealm> query = realm.where(LaunchRealm.class).greaterThanOrEqualTo("net", date).findAll().where();

        Integer[] countryFilter = switchPreferences.getUpCountryFiltered();
        Integer[] agencyFilter = switchPreferences.getUpAgencyFiltered();
        Integer[] locationFilter = switchPreferences.getUpLocationFiltered();
        Integer[] vehicleFilter = switchPreferences.getUpVehicleFiltered();

        if (countryFilter != null && countryFilter.length > 0) {
            ArrayList<String> array = switchPreferences.getUpCountryFilteredArray();
            if (array != null && array.size() > 0) {
                query = filterCountry(query, array).findAll().where();
            } else {
                Crashlytics.logException(new Throwable("Error - array is null."));
            }
        }

        if (agencyFilter != null && agencyFilter.length > 0) {
            query = filterAgency(query, switchPreferences.getUpAgencyFilteredArray()).findAll().where();
        }

        if (locationFilter != null && locationFilter.length > 0) {
            query = filterLocation(query, switchPreferences.getUpLocationFilteredArray()).findAll().where();
        }

        if (vehicleFilter != null && vehicleFilter.length > 0) {
            query = filterVehicle(query, switchPreferences.getUpVehicleFilteredArray());

        }

        Timber.v("Returning Query");
        return query.findAllSorted("net", Sort.ASCENDING);
    }

    private static RealmQuery<LaunchRealm> filterVehicle(RealmQuery<LaunchRealm> query, ArrayList<String> vehicleFilter) {
        boolean firstGroup = true;
        for (String key : vehicleFilter) {
            if (key.contains("SLV")) {
                key = "SLV";
            }
            Timber.v("Vehicle key: %s", key);
            if (!firstGroup) {
                query.or();
            } else {
                firstGroup = false;
            }
            query.contains("rocket.name", key);
        }
        return query;
    }

    private static RealmQuery<LaunchRealm> filterLocation(RealmQuery<LaunchRealm> query, ArrayList<String> locationFilter) {
        boolean firstGroup = true;
        for (String key : locationFilter) {
            String[] parts = key.split(",");
            key = parts[0];
            if (key.length() > 5) {
                key = key.substring(0, 5);
            }
            Timber.v("Location key: %s", key);
            if (!firstGroup) {
                query.or();
            } else {
                firstGroup = false;
            }
            query.beginsWith("location.name", key);
        }
        return query;
    }

    private static RealmQuery<LaunchRealm> filterAgency(RealmQuery<LaunchRealm> query, ArrayList<String> agencyFilter) {
        boolean firstGroup = true;
        for (String key : agencyFilter) {
            Timber.v("Agency key: %s", key);
            if (key.contains("NASA")) {
                if (!firstGroup) {
                    query.or();
                } else {
                    firstGroup = false;
                }
                query.equalTo("rocket.agencies.id", 44)
                        .or()
                        .equalTo("location.pads.agencies.id", 44);
            } else if (key.contains("SpaceX")) {
                if (!firstGroup) {
                    query.or();
                } else {
                    firstGroup = false;
                }
                query.equalTo("rocket.agencies.id", 121);

            } else if (key.contains("ROSCOSMOS")) {
                if (!firstGroup) {
                    query.or();
                } else {
                    firstGroup = false;
                }
                query.equalTo("rocket.agencies.id", 111)
                        .or()
                        .equalTo("location.pads.agencies.id", 111)
                        .or()
                        .equalTo("rocket.agencies.id", 163)
                        .or()
                        .equalTo("location.pads.agencies.id", 163)
                        .or()
                        .equalTo("rocket.agencies.id", 63)
                        .or()
                        .equalTo("location.pads.agencies.id", 63);

            } else if (key.contains("ULA")) {
                if (!firstGroup) {
                    query.or();
                } else {
                    firstGroup = false;
                }
                query.equalTo("rocket.agencies.id", 124)
                        .or()
                        .equalTo("location.pads.agencies.id", 124);

            } else if (key.contains("Arianespace")) {
                if (!firstGroup) {
                    query.or();
                } else {
                    firstGroup = false;
                }
                query.equalTo("rocket.agencies.id", 115)
                        .or()
                        .equalTo("location.pads.agencies.id", 115);

            } else if (key.contains("CASC")) {
                if (!firstGroup) {
                    query.or();
                } else {
                    firstGroup = false;
                }
                query.equalTo("rocket.agencies.id", 88)
                        .or()
                        .equalTo("location.pads.agencies.id", 88);

            } else if (key.contains("ISRO")) {
                if (!firstGroup) {
                    query.or();
                } else {
                    firstGroup = false;
                }
                query.equalTo("rocket.agencies.id", 31)
                        .or()
                        .equalTo("location.pads.agencies.id", 31);
            }
        }
        return query;
    }

    private static RealmQuery<LaunchRealm> filterCountry(RealmQuery<LaunchRealm> query, ArrayList<String> countryFilter) {
        boolean firstGroup = true;
        for (String key : countryFilter) {
            Timber.v("Country key: %s", key);
            if (key.contains("China")){
                key = "CHN";
            } else if (key.contains("Russia")){
                key = "RUS";
            } else if (key.contains("India")){
                key = "IND";
            } else if (key.contains("Multi")){
                key = ",";
            }
            if (!firstGroup) {
                query.or();
            } else {
                firstGroup = false;
            }
            query.contains("location.pads.agencies.countryCode", key);
            query.or();
            query.contains("rocket.agencies.countryCode", key);
        }
        return query;
    }

    public static RealmResults<LaunchRealm> buildSwitchQueryAsync(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        boolean first = true;
        Date date = new Date();
        RealmQuery<LaunchRealm> query = realm.where(LaunchRealm.class)
                .greaterThanOrEqualTo("net", date).beginGroup();
        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.agencies.id", 44)
                    .or()
                    .equalTo("location.pads.agencies.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 115)
                    .or()
                    .equalTo("location.pads.agencies.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 121)
                    .or()
                    .equalTo("location.pads.agencies.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 124)
                    .or()
                    .equalTo("location.pads.agencies.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 111)
                    .or()
                    .equalTo("location.pads.agencies.id", 111)
                    .or()
                    .equalTo("rocket.agencies.id", 163)
                    .or()
                    .equalTo("location.pads.agencies.id", 163)
                    .or()
                    .equalTo("rocket.agencies.id", 63)
                    .or()
                    .equalTo("location.pads.agencies.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 88)
                    .or()
                    .equalTo("location.pads.agencies.id", 88);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 31)
                    .or()
                    .equalTo("location.pads.agencies.id", 31);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 17);
        }

        if (switchPreferences.getSwitchCape()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 16);
        }

        if (switchPreferences.getSwitchPles()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 11);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("location.id", 18);
        }

        return query.endGroup().findAllSortedAsync("net", Sort.ASCENDING);
    }

    public static RealmResults<LaunchRealm> buildSwitchQuery(Context context, Realm realm) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        boolean first = true;
        Date date = new Date();
        RealmQuery<LaunchRealm> query = realm.where(LaunchRealm.class)
                .greaterThanOrEqualTo("net", date).beginGroup();
        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.agencies.id", 44)
                    .or()
                    .equalTo("location.pads.agencies.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 115)
                    .or()
                    .equalTo("location.pads.agencies.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 121)
                    .or()
                    .equalTo("location.pads.agencies.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 124)
                    .or()
                    .equalTo("location.pads.agencies.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 111)
                    .or()
                    .equalTo("location.pads.agencies.id", 111)
                    .or()
                    .equalTo("rocket.agencies.id", 163)
                    .or()
                    .equalTo("location.pads.agencies.id", 163)
                    .or()
                    .equalTo("rocket.agencies.id", 63)
                    .or()
                    .equalTo("location.pads.agencies.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 88)
                    .or()
                    .equalTo("location.pads.agencies.id", 88);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 31)
                    .or()
                    .equalTo("location.pads.agencies.id", 31);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 17);
        }

        if (switchPreferences.getSwitchCape()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 16);
        }

        if (switchPreferences.getSwitchPles()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 11);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("location.id", 18);
        }

        return query.endGroup().findAllSorted("net", Sort.ASCENDING);
    }

    public static RealmResults<LaunchRealm> buildSwitchQuery(Context context, Realm realm, boolean calendarState) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        boolean first = true;
        Date date = new Date();
        RealmQuery<LaunchRealm> query = realm.where(LaunchRealm.class)
                .greaterThanOrEqualTo("net", date).equalTo("syncCalendar", true).beginGroup();
        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.agencies.id", 44)
                    .or()
                    .equalTo("location.pads.agencies.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 115)
                    .or()
                    .equalTo("location.pads.agencies.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 121)
                    .or()
                    .equalTo("location.pads.agencies.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 124)
                    .or()
                    .equalTo("location.pads.agencies.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 111)
                    .or()
                    .equalTo("location.pads.agencies.id", 111)
                    .or()
                    .equalTo("rocket.agencies.id", 163)
                    .or()
                    .equalTo("location.pads.agencies.id", 163)
                    .or()
                    .equalTo("rocket.agencies.id", 63)
                    .or()
                    .equalTo("location.pads.agencies.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 88)
                    .or()
                    .equalTo("location.pads.agencies.id", 88);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 31)
                    .or()
                    .equalTo("location.pads.agencies.id", 31);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 17);
        }

        if (switchPreferences.getSwitchCape()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 16);
        }

        if (switchPreferences.getSwitchPles()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 11);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("location.id", 18);
        }

        return query.endGroup().findAllSorted("net", Sort.ASCENDING);
    }
}
