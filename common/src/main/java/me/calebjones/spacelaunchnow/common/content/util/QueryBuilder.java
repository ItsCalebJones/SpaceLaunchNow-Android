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

    public static RealmResults<Launch> buildUpcomingSwitchQuery(Context context,
                                                                Realm realm,
                                                                boolean isCalendar) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        RealmQuery<Launch> query;
        if (switchPreferences.getAllSwitch()) {
            query = getAllUpcomingQuery(context, realm, isCalendar);
        } else {
            query = getSortedUpcomingQuery(context, realm, isCalendar);
        }
        return query.findAll();
    }

    private static RealmQuery<Launch> getAllUpcomingQuery(Context context,
                                                          Realm realm,
                                                          boolean isCalendar) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        Calendar calendar = Calendar.getInstance();
        if (!isCalendar) {
            if (switchPreferences.getPersistSwitch()) {
                calendar.add(Calendar.HOUR_OF_DAY, -24);
            }
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

    private static RealmQuery<Launch> getSortedUpcomingQuery(Context context,
                                                             Realm realm,
                                                             boolean isCalendar) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        boolean firstLSP = true;
        boolean firstLocation = true;
        boolean firstOther = true;

        Calendar calendar = Calendar.getInstance();

        if (!isCalendar) {
            if (switchPreferences.getPersistSwitch()) {
                calendar.add(Calendar.HOUR_OF_DAY, -24);
            }
        }

        Date date = calendar.getTime();
        RealmQuery<Launch> query = realm.where(Launch.class)
                .greaterThanOrEqualTo("net", date);
        query.findAll();

        if (switchPreferences.getTBDSwitch()) {
            query.notEqualTo("status.id", 2).findAll();
        }

        if (switchPreferences.getSwitchStrictMatching()) {
            query = getStrictQuery(query, switchPreferences);
        } else {
            query = getNotStrictQuery(query, switchPreferences);
        }
        query.sort("net", Sort.ASCENDING);
        return query;
    }

    private static RealmQuery<Launch> getNotStrictQuery(RealmQuery<Launch> query, SwitchPreferences switchPreferences) {
        boolean firstLSP = true;
        boolean firstLocation = true;
        boolean firstOther = true;

        if (!switchPreferences.isOtherFilerted() && !switchPreferences.isLocationFilerted() && !switchPreferences.isLSPFilerted()){
            return query.equalTo("status.id", 9999);
        }

        if (switchPreferences.isLocationFilerted()) {
            query.beginGroup();

            if (switchPreferences.getSwitchKSC()) {
                firstLocation = false;
                query.beginGroup();
                query.equalTo("pad.location.id", 27);
                query.or();
                query.equalTo("pad.location.id", 12);
                query.endGroup();
            }

            if (switchPreferences.getSwitchFG()) {
                if (!firstLocation) {
                    query.or();
                } else {
                    firstLocation = false;
                }
                query.equalTo("pad.location.id", 13);
            }

            if (switchPreferences.getSwitchRussia()) {
                if (!firstLocation) {
                    query.or();
                } else {
                    firstLocation = false;
                }
                query.beginGroup();
                query.equalTo("pad.location.id", 15);
                query.or();
                query.equalTo("pad.location.id", 5);
                query.or();
                query.equalTo("pad.location.id", 6);
                query.or();
                query.equalTo("pad.location.id", 18);
                query.endGroup();
            }

            if (switchPreferences.getSwitchVan()) {
                if (!firstLocation) {
                    query.or();
                } else {
                    firstLocation = false;
                }
                query.equalTo("pad.location.id", 11);
            }

            if (switchPreferences.getSwitchWallops()) {
                if (!firstLocation) {
                    query.or();
                } else {
                    firstLocation = false;
                }
                query.equalTo("pad.location.id", 21);
            }

            if (switchPreferences.getSwitchNZ()) {
                if (!firstLocation) {
                    query.or();
                } else {
                    firstLocation = false;
                }
                query.equalTo("pad.location.id", 10);
            }

            if (switchPreferences.getSwitchTexas()) {
                if (!firstLocation) {
                    query.or();
                } else {
                    firstLocation = false;
                }
                query.beginGroup();
                query.equalTo("pad.location.id", 143);
                query.or();
                query.equalTo("pad.location.id", 29);
                query.or();
                query.equalTo("pad.location.id", 9999);
                query.endGroup();
            }

            query.endGroup();
        }

        if (!firstLocation && switchPreferences.isLSPFilerted()) {
            query.or();
        }

        if (switchPreferences.isLSPFilerted()) {
            query.beginGroup();

            if (switchPreferences.getSwitchNasa()) {
                firstLSP = false;
                query.beginGroup();
                query.equalTo("rocket.configuration.manufacturer.id", 44);
                query.or();
                query.equalTo("launchServiceProvider.id", 44);
                query.endGroup();
            }

            if (switchPreferences.getSwitchArianespace()) {
                if (!firstLSP) {
                    query.or();
                } else {
                    firstLSP = false;
                }
                query.beginGroup();
                query.equalTo("rocket.configuration.manufacturer.id", 115);
                query.or();
                query.equalTo("launchServiceProvider.id", 115);
                query.endGroup();
            }

            if (switchPreferences.getSwitchSpaceX()) {
                if (!firstLSP) {
                    query.or();
                } else {
                    firstLSP = false;
                }
                query.beginGroup();
                query.equalTo("launchServiceProvider.id", 121);
                query.endGroup();
            }

            if (switchPreferences.getSwitchULA()) {
                if (!firstLSP) {
                    query.or();
                } else {
                    firstLSP = false;
                }
                query.beginGroup();
                query.equalTo("launchServiceProvider.id", 124);
                query.endGroup();
            }

            if (switchPreferences.getSwitchRoscosmos()) {
                if (!firstLSP) {
                    query.or();
                } else {
                    firstLSP = false;
                }
                query.beginGroup();
                query.equalTo("rocket.configuration.manufacturer.id", 111);
                query.or();
                query.equalTo("rocket.configuration.manufacturer.id", 163);
                query.or();
                query.equalTo("rocket.configuration.manufacturer.id", 63);
                query.or();
                query.equalTo("launchServiceProvider.id", 111);
                query.or();
                query.equalTo("launchServiceProvider.id", 163);
                query.or();
                query.equalTo("launchServiceProvider.id", 63);
                query.endGroup();

            }

            if (switchPreferences.getSwitchBO()) {
                if (!firstLSP) {
                    query.or();
                } else {
                    firstLSP = false;
                }
                query.beginGroup();
                query.equalTo("rocket.configuration.manufacturer.id", 141);
                query.or();
                query.equalTo("launchServiceProvider.id", 141);
                query.endGroup();
            }

            if (switchPreferences.getSwitchRL()) {
                if (!firstLSP) {
                    query.or();
                } else {
                    firstLSP = false;
                }
                query.beginGroup();
                query.equalTo("rocket.configuration.manufacturer.id", 147);
                query.or();
                query.equalTo("launchServiceProvider.id", 147);
                query.endGroup();
            }

            if (switchPreferences.getSwitchNorthrop()) {
                if (!firstLSP) {
                    query.or();
                } else {
                    firstLSP = false;
                }
                query.beginGroup();
                query.equalTo("rocket.configuration.manufacturer.id", 257);
                query.or();
                query.equalTo("launchServiceProvider.id", 257);
                query.endGroup();
            }

            query.endGroup();
        }

        if (switchPreferences.isOtherFilerted()) {
            if (switchPreferences.getSwitchJapan()) {
                if (!firstLSP || !firstLocation) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("pad.location.id", 24);
                query.or();
                query.equalTo("pad.location.id", 26);
                query.or();
                query.equalTo("pad.location.id", 32);
                query.endGroup();
            }

            if (switchPreferences.getSwitchKodiak()) {
                if ((!firstLSP || !firstLocation) || !firstOther) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("pad.location.id", 25);
                query.endGroup();
            }

            if (switchPreferences.getSwitchOtherLocations()) {
                if ((!firstLSP || !firstLocation) || !firstOther) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("pad.location.id", 20);
                query.or();
                query.equalTo("pad.location.id", 144);
                query.or();
                query.equalTo("pad.location.id", 22);
                query.or();
                query.equalTo("pad.location.id", 3);
                query.endGroup();
            }

            if (switchPreferences.getSwitchCASC()) {
                if ((!firstLSP || !firstLocation) || !firstOther) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.notEqualTo("pad.location.id", 17);
                query.or();
                query.notEqualTo("pad.location.id", 19);
                query.or();
                query.notEqualTo("pad.location.id", 8);
                query.or();
                query.notEqualTo("pad.location.id", 16);
                query.or();
                query.notEqualTo("pad.location.id", 148);
                query.endGroup();
            }

            if (switchPreferences.getSwitchISRO()) {
                if ((!firstLSP || !firstLocation) || !firstOther) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("rocket.configuration.manufacturer.id", 31);
                query.or();
                query.equalTo("pad.location.id", 14);
                query.or();
                query.equalTo("launchServiceProvider.id", 31);
                query.endGroup();
            }
        }
        return query;
    }

    private static RealmQuery<Launch> getStrictQuery(RealmQuery<Launch> query, SwitchPreferences switchPreferences) {
        boolean firstLSP = true;
        boolean firstLocation = true;
        boolean firstOther = true;

        if ((!switchPreferences.isLSPFilerted() && switchPreferences.isLocationFilerted())
                || (!switchPreferences.isLocationFilerted() && switchPreferences.isLSPFilerted())
                && !switchPreferences.isOtherFilerted()){
            return query.equalTo("status.id", 9999);
        } else if (!switchPreferences.isOtherFilerted()
                && !switchPreferences.isLocationFilerted()
                && !switchPreferences.isLSPFilerted()){
            return query.equalTo("status.id", 9999);
        }


        if (switchPreferences.isLocationFilerted() && switchPreferences.isLSPFilerted()) {
            if (switchPreferences.isLocationFilerted()) {
                query.beginGroup();

                if (switchPreferences.getSwitchKSC()) {
                    firstLocation = false;
                    query.beginGroup();
                    query.equalTo("pad.location.id", 27);
                    query.or();
                    query.equalTo("pad.location.id", 12);
                    query.endGroup();
                }

                if (switchPreferences.getSwitchFG()) {
                    if (!firstLocation) {
                        query.or();
                    } else {
                        firstLocation = false;
                    }
                    query.equalTo("pad.location.id", 13);
                }

                if (switchPreferences.getSwitchRussia()) {
                    if (!firstLocation) {
                        query.or();
                    } else {
                        firstLocation = false;
                    }
                    query.beginGroup();
                    query.equalTo("pad.location.id", 15);
                    query.or();
                    query.equalTo("pad.location.id", 5);
                    query.or();
                    query.equalTo("pad.location.id", 6);
                    query.or();
                    query.equalTo("pad.location.id", 18);
                    query.endGroup();
                }

                if (switchPreferences.getSwitchVan()) {
                    if (!firstLocation) {
                        query.or();
                    } else {
                        firstLocation = false;
                    }
                    query.equalTo("pad.location.id", 11);
                }

                if (switchPreferences.getSwitchWallops()) {
                    if (!firstLocation) {
                        query.or();
                    } else {
                        firstLocation = false;
                    }
                    query.equalTo("pad.location.id", 21);
                }

                if (switchPreferences.getSwitchNZ()) {
                    if (!firstLocation) {
                        query.or();
                    } else {
                        firstLocation = false;
                    }
                    query.equalTo("pad.location.id", 10);
                }

                if (switchPreferences.getSwitchTexas()) {
                    if (!firstLocation) {
                        query.or();
                    } else {
                        firstLocation = false;
                    }
                    query.beginGroup();
                    query.equalTo("pad.location.id", 143);
                    query.or();
                    query.equalTo("pad.location.id", 29);
                    query.or();
                    query.equalTo("pad.location.id", 9999);
                    query.endGroup();
                }

                query.endGroup();
            }

            query.and();

            if (switchPreferences.isLSPFilerted()) {
                query.beginGroup();

                if (switchPreferences.getSwitchNasa()) {
                    firstLSP = false;
                    query.beginGroup();
                    query.equalTo("rocket.configuration.manufacturer.id", 44);
                    query.or();
                    query.equalTo("launchServiceProvider.id", 44);
                    query.endGroup();
                }

                if (switchPreferences.getSwitchArianespace()) {
                    if (!firstLSP) {
                        query.or();
                    } else {
                        firstLSP = false;
                    }
                    query.beginGroup();
                    query.equalTo("rocket.configuration.manufacturer.id", 115);
                    query.or();
                    query.equalTo("launchServiceProvider.id", 115);
                    query.endGroup();
                }

                if (switchPreferences.getSwitchSpaceX()) {
                    if (!firstLSP) {
                        query.or();
                    } else {
                        firstLSP = false;
                    }
                    query.beginGroup();
                    query.equalTo("launchServiceProvider.id", 121);
                    query.endGroup();
                }

                if (switchPreferences.getSwitchULA()) {
                    if (!firstLSP) {
                        query.or();
                    } else {
                        firstLSP = false;
                    }
                    query.beginGroup();
                    query.equalTo("launchServiceProvider.id", 124);
                    query.endGroup();
                }

                if (switchPreferences.getSwitchRoscosmos()) {
                    if (!firstLSP) {
                        query.or();
                    } else {
                        firstLSP = false;
                    }
                    query.beginGroup();
                    query.equalTo("rocket.configuration.manufacturer.id", 111);
                    query.or();
                    query.equalTo("rocket.configuration.manufacturer.id", 163);
                    query.or();
                    query.equalTo("rocket.configuration.manufacturer.id", 63);
                    query.or();
                    query.equalTo("launchServiceProvider.id", 111);
                    query.or();
                    query.equalTo("launchServiceProvider.id", 163);
                    query.or();
                    query.equalTo("launchServiceProvider.id", 63);
                    query.endGroup();

                }

                if (switchPreferences.getSwitchBO()) {
                    if (!firstLSP) {
                        query.or();
                    } else {
                        firstLSP = false;
                    }
                    query.beginGroup();
                    query.equalTo("rocket.configuration.manufacturer.id", 141);
                    query.or();
                    query.equalTo("launchServiceProvider.id", 141);
                    query.endGroup();
                }

                if (switchPreferences.getSwitchRL()) {
                    if (!firstLSP) {
                        query.or();
                    } else {
                        firstLSP = false;
                    }
                    query.beginGroup();
                    query.equalTo("rocket.configuration.manufacturer.id", 147);
                    query.or();
                    query.equalTo("launchServiceProvider.id", 147);
                    query.endGroup();
                }

                if (switchPreferences.getSwitchNorthrop()) {
                    if (!firstLSP) {
                        query.or();
                    } else {
                        firstLSP = false;
                    }
                    query.beginGroup();
                    query.equalTo("rocket.configuration.manufacturer.id", 257);
                    query.or();
                    query.equalTo("launchServiceProvider.id", 257);
                    query.endGroup();
                }

                query.endGroup();
            }
        }

        if (switchPreferences.isOtherFilerted()) {
            if (switchPreferences.getSwitchJapan()) {
                if (!firstLSP && !firstLocation) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("pad.location.id", 24);
                query.or();
                query.equalTo("pad.location.id", 26);
                query.or();
                query.equalTo("pad.location.id", 32);
                query.endGroup();
            }

            if (switchPreferences.getSwitchKodiak()) {
                if ((!firstLSP && !firstLocation) || !firstOther) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("pad.location.id", 25);
                query.endGroup();
            }

            if (switchPreferences.getSwitchOtherLocations()) {
                if ((!firstLSP && !firstLocation) || !firstOther) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("pad.location.id", 20);
                query.or();
                query.equalTo("pad.location.id", 144);
                query.or();
                query.equalTo("pad.location.id", 22);
                query.or();
                query.equalTo("pad.location.id", 3);
                query.endGroup();
            }

            if (switchPreferences.getSwitchCASC()) {
                if ((!firstLSP && !firstLocation) || !firstOther) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("pad.location.id", 17);
                query.or();
                query.equalTo("pad.location.id", 19);
                query.or();
                query.equalTo("pad.location.id", 8);
                query.or();
                query.equalTo("pad.location.id", 16);
                query.or();
                query.equalTo("pad.location.id", 148);
                query.endGroup();
            }

            if (switchPreferences.getSwitchISRO()) {
                if ((!firstLSP && !firstLocation) || !firstOther) {
                    query.or();
                }
                firstOther = false;
                query.beginGroup();
                query.equalTo("rocket.configuration.manufacturer.id", 31);
                query.or();
                query.equalTo("pad.location.id", 14);
                query.or();
                query.equalTo("launchServiceProvider.id", 31);
                query.endGroup();
            }
        }

        return query;
    }
}