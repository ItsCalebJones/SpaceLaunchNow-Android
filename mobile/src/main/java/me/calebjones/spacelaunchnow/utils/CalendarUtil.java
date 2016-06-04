package me.calebjones.spacelaunchnow.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.crashlytics.android.Crashlytics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import me.calebjones.spacelaunchnow.content.models.legacy.Launch;
import timber.log.Timber;

// these imports are used in the following code

public class CalendarUtil {

    private SharedPreferences sharedPrefs;

    final static String[] CALENDAR_QUERY_COLUMNS = {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    public Integer addEvent(Context context, Launch launch) {
        Timber.v("Adding launch event: %s", launch.getName());

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int selection = Integer.parseInt(sharedPrefs.getString("calendar_reminder", "5"));
        Timber.v("Reminder time: %s", String.valueOf(selection));


        Set<String> prefSelections = sharedPrefs.getStringSet("calendar_reminder_array", new HashSet<>(Arrays.asList("5", "1440")));
        String[] strings = prefSelections.toArray(new String[prefSelections.size()]);
        int[] prefSelected = new int[prefSelections.size()];
        for (int i = 0; i < prefSelected.length ; i++){
            prefSelected[i] = Integer.parseInt(strings[i]);
        }

        // Get list of Calendars (after Jim Blackler, http://jimblackler.net/blog/?p=151)
        ContentResolver contentResolver = context.getContentResolver();

        final Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI,
                CALENDAR_QUERY_COLUMNS, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                final String _id = cursor.getString(0);
                final String displayName = cursor.getString(1);
                final Boolean selected = !cursor.getString(2).equals("0");
                final String accountName = cursor.getString(3);

                Timber.v("Calendar: Id: %s Display Name: %s Selected: %s Name %s", _id, displayName, selected, accountName);
            }
        }

        ContentValues calEvent = new ContentValues();
        calEvent.put(CalendarContract.Events.CALENDAR_ID, 1);
        calEvent.put(CalendarContract.Events.TITLE, launch.getName());

        //Build Description String and assign it.
        String description = "";
        if (launch.getVidURLs() != null && launch.getVidURLs().size() >= 1) {
            description = "Video URLs: \n";
            for (int i = 0; i < launch.getVidURLs().size(); i++) {
                description = description + launch.getVidURLs().get(i) + "\n\n";
            }
        }
        if (launch.getMissions() != null && launch.getMissions().size() > 0) {
            description = description + launch.getMissions().get(0).getDescription();
        }

        calEvent.put(CalendarContract.Events.DESCRIPTION, description);
        calEvent.put(CalendarContract.Events.EVENT_LOCATION, launch.getLocation().getName());
        calEvent.put(CalendarContract.Events.DTSTART, launch.getStartDate().getTime());
        calEvent.put(CalendarContract.Events.DTEND, launch.getEndDate().getTime());
        calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, calEvent);

        // The returned Uri contains the content-retriever URI for the newly-inserted event, including its id
        if (cursor != null) {
            cursor.close();
        }
        if (uri != null) {
            for (int i = 0; i < prefSelected.length; i++){
                setReminder(context, Long.parseLong(uri.getLastPathSegment()), prefSelected[i]);
            }
            return Integer.parseInt(uri.getLastPathSegment());
        } else {
            return null;
        }
    }

    public boolean updateEvent(Context context, Launch launch) {
        Timber.v("Updating launch event: %s", launch.getName());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            ContentResolver cr = context.getContentResolver();
            ContentValues calEvent = new ContentValues();

            // The new title for the event
            calEvent.put(CalendarContract.Events.TITLE, launch.getName());

            //Build Description String and assign it.
            String description = "";
            if (launch.getVidURLs() != null && launch.getVidURLs().size() >= 1) {
                description = "Video URLs: \n";
                for (int i = 0; i < launch.getVidURLs().size(); i++) {
                    description = description + launch.getVidURLs().get(i) + "\n\n";
                }
            }
            if (launch.getMissions() != null && launch.getMissions().size() > 0) {
                description = description + launch.getMissions().get(0).getDescription();
            }

            calEvent.put(CalendarContract.Events.DESCRIPTION, description);
            calEvent.put(CalendarContract.Events.EVENT_LOCATION, launch.getLocation().getName());
            calEvent.put(CalendarContract.Events.DTSTART, launch.getStartDate().getTime());
            calEvent.put(CalendarContract.Events.DTEND, launch.getEndDate().getTime());
            calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());

            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, launch.getCalendarID());

            return cr.update(updateUri, calEvent, null, null) > 0;
        } return false;
    }

    public int deleteEvent(Context context, Launch launch) {
        Timber.v("Deleting launch event: %s", launch.getName());
        int iNumRowsDeleted = 0;

        if (launch.getCalendarID() > 0) {
            Uri eventUri = ContentUris
                    .withAppendedId(CalendarContract.Events.CONTENT_URI, launch.getCalendarID());
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
            Cursor c = CalendarContract.Reminders.query(contentResolver, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});
            if (c.moveToFirst()) {
                System.out.println("calendar"
                        + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            c.close();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

}