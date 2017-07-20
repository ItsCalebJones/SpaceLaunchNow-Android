package me.calebjones.spacelaunchnow.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.crashlytics.android.Crashlytics;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import io.realm.Realm;
import it.macisamuele.calendarprovider.EventInfo;
import me.calebjones.spacelaunchnow.calendar.model.Calendar;
import me.calebjones.spacelaunchnow.calendar.model.CalendarItem;
import me.calebjones.spacelaunchnow.calendar.model.Event;
import me.calebjones.spacelaunchnow.data.models.Launch;
import timber.log.Timber;

// these imports are used in the following code

public class CalendarUtility {

    final static String[] CALENDAR_QUERY_COLUMNS = {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };
    private SharedPreferences sharedPrefs;
    private CalendarItem calendarItem;

    public CalendarUtility(CalendarItem calendarItem) {
        this.calendarItem = calendarItem;
    }

    public Integer addEvent(Context context, Launch launch) {
        Timber.v("Adding launch event: %s", launch.getName());

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> prefSelections = sharedPrefs.getStringSet("calendar_reminder_array", new HashSet<>(Arrays.asList("5", "1440")));
        String[] strings = prefSelections.toArray(new String[prefSelections.size()]);
        int[] prefSelected = new int[prefSelections.size()];
        for (int i = 0; i < prefSelected.length; i++) {
            prefSelected[i] = Integer.parseInt(strings[i]);
        }

        //Build Description String and assign it.
        String description = "";
        String urls = "";
        if (launch.getVidURLs() != null && launch.getVidURLs().size() >= 1) {
            urls = "\n\nWatch Live: \n";
            for (int i = 0; i < launch.getVidURLs().size(); i++) {
                urls = urls + "\n" + launch.getVidURLs().get(i).getVal();
            }
        }
        if (launch.getMissions() != null && launch.getMissions().size() > 0) {
            description = launch.getMissions().get(0).getDescription() + urls;
        }

        description = description + "\n\n via Space Launch Now";

        Date startDate = launch.getWindowstart();
        Date endDate = launch.getWindowend();

        Event event = new Event();
        event.calendarId = calendarItem.getId();
        event.title = launch.getName();
        event.description = description;
        event.location = launch.getLocation().getName();
        if (startDate != null && endDate != null) {
            event.startDate = startDate.getTime();
            event.endDate = endDate.getTime();
        } else if (launch.getNet() != null) {
            event.startDate = launch.getNet().getTime();
        }
        event.timezone = TimeZone.getDefault().getDisplayName();

        int id = event.create(context.getContentResolver());
        for (int time : prefSelected) {
            setReminder(context, id, time);
        }
        return id;
    }

    public boolean updateEvent(Context context, Launch launch) {
        Timber.v("Updating launch event: %s", launch.getName());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            ContentResolver cr = context.getContentResolver();
            ContentValues calEvent = new ContentValues();

            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            Set<String> prefSelections = sharedPrefs.getStringSet("calendar_reminder_array", new HashSet<>(Arrays.asList("5", "1440")));
            String[] strings = prefSelections.toArray(new String[prefSelections.size()]);
            int[] prefSelected = new int[prefSelections.size()];
            for (int i = 0; i < prefSelected.length; i++) {
                prefSelected[i] = Integer.parseInt(strings[i]);
            }

            // The new title for the event
            calEvent.put(CalendarContract.Events.TITLE, launch.getName());

            //Build Description String and assign it.
            String description = "";
            String urls = "";
            if (launch.getVidURLs() != null && launch.getVidURLs().size() >= 1) {
                urls = "\n\nWatch Live:";
                for (int i = 0; i < launch.getVidURLs().size(); i++) {
                    urls = urls + "\n" + launch.getVidURLs().get(i).getVal();
                }
            }
            if (launch.getMissions() != null && launch.getMissions().size() > 0) {
                description = launch.getMissions().get(0).getDescription() + urls;
            }

            description = description + "\n\n via Space Launch Now";

            Date startDate = launch.getWindowstart();
            Date endDate = launch.getWindowend();

            calEvent.put(CalendarContract.Events.DESCRIPTION, description);
            calEvent.put(CalendarContract.Events.EVENT_LOCATION, launch.getLocation().getName());
            if (startDate != null && endDate != null) {
                calEvent.put(CalendarContract.Events.DTSTART, startDate.getTime());
                calEvent.put(CalendarContract.Events.DTEND, endDate.getTime());

            } else if (launch.getNet() != null) {
                calEvent.put(CalendarContract.Events.DTSTART, launch.getNet().getTime());
            }
            calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());

            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, launch.getEventID());

            try {
                return updateUri != null && cr.update(updateUri, calEvent, null, null) > 0;
            } catch (SQLiteException error) {
                if (updateUri != null) {
                    Crashlytics.log("UpdateURI is not null");
                }

                if (calEvent != null) {
                    Crashlytics.log("calEvent is not null");
                }
                Crashlytics.log("Launch:" + launch.getName());
                Crashlytics.logException(error);
                return false;
            }
        }
        return false;
    }

    public int deleteEvent(Context context, Launch launch) {
        Timber.v("Deleting launch event: %s", launch.getName());
        int iNumRowsDeleted = 0;

        if (launch.getEventID() != null && launch.getEventID() > 0) {
            Uri eventUri = ContentUris
                    .withAppendedId(CalendarContract.Events.CONTENT_URI, launch.getEventID());
            iNumRowsDeleted = context.getContentResolver().delete(eventUri, null, null);
        }
        return iNumRowsDeleted;
    }

    public int deleteEvent(Context context, Integer id) {
        Timber.v("Deleting launch event: %s", id);
        int iNumRowsDeleted;

        Uri eventUri = ContentUris
                .withAppendedId(CalendarContract.Events.CONTENT_URI, id);
        iNumRowsDeleted = context.getContentResolver().delete(eventUri, null, null);
        return iNumRowsDeleted;
    }

    private int deleteEvent(Context context, Long id) {
        Timber.v("Deleting launch event: %s", id);
        int iNumRowsDeleted = 0;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            Uri eventUri = ContentUris
                    .withAppendedId(CalendarContract.Events.CONTENT_URI, id);
            iNumRowsDeleted = context.getContentResolver().delete(eventUri, null, null);
        }
        return iNumRowsDeleted;
    }

    public void setReminder(Context context, long eventID, int timeBefore) {
        try {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            ContentResolver contentResolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

            Uri uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values);
            Cursor c = CalendarContract.Reminders.query(contentResolver, eventID, new String[]{CalendarContract.Reminders.MINUTES});
            c.close();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    private List<Calendar> getCalendars(Context context) {
        return Calendar.getCalendarsForQuery(null, null, null, context.getContentResolver());
    }

    public void deleteDuplicates(Context context, Realm realm, String queryStr, String type) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            List<EventInfo> eventInfos = EventInfo.getAllEvents(context);
            if (eventInfos != null) {
                for (EventInfo eventInfo : eventInfos) {
                    if (eventInfo != null & eventInfo.getDescription() != null && eventInfo.getDescription().contains(queryStr)) {
                        if (eventInfo.getId() != null) {
                            Launch launchRealm = realm.where(Launch.class).equalTo("eventID", eventInfo.getId()).findFirst();
                            if (launchRealm == null) {
                                deleteEvent(context, eventInfo.getId());
                            }
                        }
                    }
                }
            }
        }
    }
}
