package me.calebjones.spacelaunchnow.calendar.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andraskindler on 16/12/13.
 */
public class Reminder {

    public Integer id;
    public Integer eventId;
    public Integer minutesBefore;
    public Integer method;

    protected ContentValues mapToContentValues() {
        final ContentValues contentValues = new ContentValues();
        if (id != null) contentValues.put(CalendarContract.Reminders._ID, id);
        if (eventId != null) contentValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
        contentValues.put(CalendarContract.Reminders.MINUTES, minutesBefore == null ? CalendarContract.Reminders.MINUTES_DEFAULT : minutesBefore);
        contentValues.put(CalendarContract.Reminders.METHOD, method == null ? CalendarContract.Reminders.METHOD_DEFAULT : method);
        return contentValues;
    }

    public int addToEvent(final ContentResolver contentResolver, final Event event) {
        eventId = event.id;
        final Uri uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, mapToContentValues());
        return id = Integer.parseInt(uri.getLastPathSegment());
    }

    public int delete(final ContentResolver contentResolver) {
        return contentResolver.delete(ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, id), null, null);
    }

    public int update(final ContentResolver contentResolver) {
        final ContentValues contentValues = mapToContentValues();
        contentResolver.update(ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, id), contentValues, null, null);
        return id;
    }

    public static List<Reminder> getRemindersForQuery(final String query, final String[] queryArgs, final String sortOrder, final ContentResolver contentResolver) {
        final String[] attendeeProjection = new String[]{
                CalendarContract.Reminders._ID,
                CalendarContract.Reminders.EVENT_ID,
                CalendarContract.Reminders.MINUTES,
                CalendarContract.Reminders.METHOD
        };

        final Cursor cursor = contentResolver.query(CalendarContract.Reminders.CONTENT_URI, attendeeProjection, query, queryArgs, sortOrder);
        cursor.moveToFirst();
        final List<Reminder> reminders = new ArrayList<Reminder>();
        while (cursor.moveToNext()) {
            final Reminder reminder = new Reminder();
            reminder.id = cursor.getInt(0);
            reminder.eventId = cursor.getInt(1);
            reminder.minutesBefore = cursor.getInt(2);
            reminder.method = cursor.getInt(3);
            reminders.add(reminder);
        }
        cursor.close();
        return reminders;
    }
}
