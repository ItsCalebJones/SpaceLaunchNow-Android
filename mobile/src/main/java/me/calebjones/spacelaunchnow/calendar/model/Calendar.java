package me.calebjones.spacelaunchnow.calendar.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calendar {

    public Integer id;
    public String accountName;
    public String accountType;
    public String name;
    public String displayName;
    public Integer color;
    public Integer accessLevel;
    public String ownerAccount;
    public Integer syncEvents;
    public String timeZone;
    public String allowedReminders;
    public String allowedAvailability;
    public String allowedAttendeeTypes;

    protected ContentValues mapToContentValues() {
        final ContentValues contentValues = new ContentValues();
        if (id != null) contentValues.put(CalendarContract.Calendars._ID, id);
        if (accountName != null) contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        if (accountType != null) contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, accountType);
        if (name != null) contentValues.put(CalendarContract.Calendars.NAME, name);
        if (displayName != null) contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, displayName);
        if (color != null) contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, color);
        if (accessLevel != null) contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, accessLevel);
        if (ownerAccount != null) contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, ownerAccount);
        if (syncEvents != null) contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, syncEvents);
        if (timeZone != null) contentValues.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone);
        if (allowedReminders != null) contentValues.put(CalendarContract.Calendars.ALLOWED_REMINDERS, allowedReminders);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            if (allowedAvailability != null)
                contentValues.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, allowedAvailability);
            if (allowedAttendeeTypes != null)
                contentValues.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, allowedAttendeeTypes);
        }

        return contentValues;
    }

    public static List<Calendar> getWritableCalendars(final ContentResolver contentResolver) {
        final String calendarQuery = "(" + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ?)";
        final String[] calendarQueryArgs = new String[]{
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_OWNER),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_EDITOR),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_ROOT)

        };
        return getCalendarsForQuery(calendarQuery, calendarQueryArgs, null, contentResolver);
    }

    public static List<Calendar> getCalendarsForQuery(final String query, final String[] queryArgs, final String sortOrder, final ContentResolver contentResolver) {
        final String[] calendarProjection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.SYNC_EVENTS,
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                CalendarContract.Calendars.ALLOWED_REMINDERS
        };

        List<String> tempList = new ArrayList<String>(Arrays.asList(calendarProjection));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            tempList.add(CalendarContract.Calendars.ALLOWED_AVAILABILITY);
            tempList.add(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES);
        }

        final Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, tempList.toArray(new String[tempList.size()]), query, queryArgs, sortOrder);

        final List<Calendar> calendars = new ArrayList<Calendar>();
        while (cursor.moveToNext()) {
            final Calendar calendar = new Calendar();
            calendar.id = cursor.getInt(0);
            calendar.accountName = cursor.getString(1);
            calendar.accountType = cursor.getString(2);
            calendar.name = cursor.getString(3);
            calendar.displayName = cursor.getString(4);
            calendar.color = cursor.getInt(5);
            calendar.accessLevel = cursor.getInt(6);
            calendar.ownerAccount = cursor.getString(7);
            calendar.syncEvents = cursor.getInt(8);
            calendar.timeZone = cursor.getString(9);
            calendar.allowedReminders = cursor.getString(10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                calendar.allowedAvailability = cursor.getString(11);
                calendar.allowedAttendeeTypes = cursor.getString(12);
            }
            calendars.add(calendar);
        }
        cursor.close();

        return calendars;
    }

}
