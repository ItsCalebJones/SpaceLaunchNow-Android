package me.calebjones.spacelaunchnow.calendar.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class Event {

    public Integer id;
    public Integer calendarId;
    public String organizer;
    public String title;
    public String location;
    public String description;
    public Integer color;
    public long startDate;
    public long endDate;
    public String timezone;
    public String endTimezone;
    public String duration;
    public Integer allDay;
    public String recurrenceRule;
    public String recurrenceDate;
    public String exceptionRule;
    public String exceptionDate;
    public String originalId;
    public String originalSyncId;
    public Integer originalInstanceTime;
    public Integer originalAllDay;
    public Integer accessLevel;
    public Integer availability;
    public Integer guestsCanModify;
    public Integer guestsCanInviteOthers;
    public Integer guestsCanSeeGuests;
    public String customAppPackage;
    public String customAppUri;
    public String uID2445;
    public String selfAttendeeStatus;
    public Integer hasAlarm;
    public Integer hasAttendeeData;
    public Integer hasExtendedProperties;
    public String eventTimezone;

    protected ContentValues mapToContentValues() {
        final ContentValues contentValues = new ContentValues();

        if (id != null) contentValues.put(CalendarContract.Events._ID, id);
        contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId == null ? 1 : calendarId);
        if (organizer != null) contentValues.put(CalendarContract.Events.ORGANIZER, organizer);
        if (title != null) contentValues.put(CalendarContract.Events.TITLE, title);
        if (location != null) contentValues.put(CalendarContract.Events.EVENT_LOCATION, location);
        if (description != null) contentValues.put(CalendarContract.Events.DESCRIPTION, description);
        if (color != null) contentValues.put(CalendarContract.Events.EVENT_COLOR, color);
        contentValues.put(CalendarContract.Events.DTSTART, startDate);
        contentValues.put(CalendarContract.Events.DTEND, endDate);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, timezone == null ? TimeZone.getDefault().getID() : timezone);
        contentValues.put(CalendarContract.Events.EVENT_END_TIMEZONE, endTimezone == null ? TimeZone.getDefault().getID() : endTimezone);
        if (duration != null) contentValues.put(CalendarContract.Events.DURATION, duration);
        if (allDay != null) contentValues.put(CalendarContract.Events.ALL_DAY, allDay);
        if (recurrenceRule != null) contentValues.put(CalendarContract.Events.RRULE, recurrenceRule);
        if (recurrenceDate != null) contentValues.put(CalendarContract.Events.RDATE, recurrenceDate);
        if (exceptionRule != null) contentValues.put(CalendarContract.Events.EXRULE, exceptionRule);
        if (exceptionDate != null) contentValues.put(CalendarContract.Events.EXDATE, exceptionDate);
        if (originalId != null) contentValues.put(CalendarContract.Events.ORIGINAL_ID, originalId);
        if (originalSyncId != null) contentValues.put(CalendarContract.Events.ORIGINAL_SYNC_ID, originalSyncId);
        if (originalInstanceTime != null)
            contentValues.put(CalendarContract.Events.ORIGINAL_INSTANCE_TIME, originalInstanceTime);
        if (originalAllDay != null) contentValues.put(CalendarContract.Events.ORIGINAL_ALL_DAY, originalAllDay);
        if (accessLevel != null) contentValues.put(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
        if (availability != null) contentValues.put(CalendarContract.Events.AVAILABILITY, availability);
        if (guestsCanModify != null) contentValues.put(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify);
        if (guestsCanInviteOthers != null)
            contentValues.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers);
        if (guestsCanSeeGuests != null)
            contentValues.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (customAppPackage != null)
                contentValues.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, customAppPackage);
            if (customAppUri != null) contentValues.put(CalendarContract.Events.CUSTOM_APP_URI, customAppUri);
            if (uID2445 != null) contentValues.put(CalendarContract.Events.UID_2445, uID2445);
        }
        if (selfAttendeeStatus != null)
            contentValues.put(CalendarContract.Events.SELF_ATTENDEE_STATUS, selfAttendeeStatus);
        if (hasAlarm != null) contentValues.put(CalendarContract.Events.HAS_ALARM, hasAlarm);
        if (hasAttendeeData != null) contentValues.put(CalendarContract.Events.HAS_ATTENDEE_DATA, hasAttendeeData);
        if (hasExtendedProperties != null)
            contentValues.put(CalendarContract.Events.HAS_EXTENDED_PROPERTIES, hasExtendedProperties);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, eventTimezone == null ? TimeZone.getDefault().getID() : eventTimezone);

        return contentValues;
    }

    public int delete(final ContentResolver contentResolver) {
        return contentResolver.delete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id), null, null);
    }

    public void setCalendar(final Calendar calendar) {
        calendarId = calendar.id;
    }

    public static List<Event> getEventsForQuery(final String query, final String[] queryArgs, final String sortOrder, final ContentResolver contentResolver) {
        final String[] eventProjection = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.ORGANIZER,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.EVENT_COLOR,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_TIMEZONE,
                CalendarContract.Events.EVENT_END_TIMEZONE,
                CalendarContract.Events.DURATION,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.RRULE,
                CalendarContract.Events.RDATE,
                CalendarContract.Events.EXRULE,
                CalendarContract.Events.EXDATE,
                CalendarContract.Events.ORIGINAL_ID,
                CalendarContract.Events.ORIGINAL_SYNC_ID,
                CalendarContract.Events.ORIGINAL_INSTANCE_TIME,
                CalendarContract.Events.ORIGINAL_ALL_DAY,
                CalendarContract.Events.ACCESS_LEVEL,
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.GUESTS_CAN_MODIFY,
                CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS,
                CalendarContract.Events.GUESTS_CAN_SEE_GUESTS,
                CalendarContract.Events.SELF_ATTENDEE_STATUS,
                CalendarContract.Events.HAS_ALARM,
                CalendarContract.Events.HAS_ATTENDEE_DATA,
                CalendarContract.Events.HAS_EXTENDED_PROPERTIES,
                CalendarContract.Events.EVENT_TIMEZONE
        };

        List<String> tempList = new ArrayList<String>(Arrays.asList(eventProjection));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tempList.add(CalendarContract.Events.CUSTOM_APP_PACKAGE);
            tempList.add(CalendarContract.Events.CUSTOM_APP_URI);
            tempList.add(CalendarContract.Events.UID_2445);
        }

        final Cursor cursor = contentResolver.query(CalendarContract.Attendees.CONTENT_URI, tempList.toArray(new String[tempList.size()]), query, queryArgs, sortOrder);

        final List<Event> result = new ArrayList<Event>();


        while (cursor.moveToNext()) {
            final Event event = new Event();
            event.id = cursor.getInt(0);
            event.calendarId = cursor.getInt(1);
            event.organizer = cursor.getString(2);
            event.title = cursor.getString(3);
            event.location = cursor.getString(4);
            event.description = cursor.getString(5);
            event.color = cursor.getInt(6);
            event.startDate = cursor.getLong(7);
            event.endDate = cursor.getLong(8);
            event.timezone = cursor.getString(9);
            event.endTimezone = cursor.getString(10);
            event.duration = cursor.getString(11);
            event.allDay = cursor.getInt(12);
            event.recurrenceRule = cursor.getString(13);
            event.recurrenceDate = cursor.getString(14);
            event.exceptionRule = cursor.getString(15);
            event.exceptionDate = cursor.getString(16);
            event.originalId = cursor.getString(17);
            event.originalSyncId = cursor.getString(18);
            event.originalInstanceTime = cursor.getInt(19);
            event.originalAllDay = cursor.getInt(20);
            event.accessLevel = cursor.getInt(21);
            event.availability = cursor.getInt(22);
            event.guestsCanModify = cursor.getInt(23);
            event.guestsCanInviteOthers = cursor.getInt(24);
            event.guestsCanSeeGuests = cursor.getInt(25);
            event.selfAttendeeStatus = cursor.getString(26);
            event.hasAlarm = cursor.getInt(27);
            event.hasAttendeeData = cursor.getInt(28);
            event.hasExtendedProperties = cursor.getInt(29);
            event.eventTimezone = cursor.getString(30);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                event.customAppPackage = cursor.getString(31);
                event.customAppUri = cursor.getString(32);
                event.uID2445 = cursor.getString(33);
            }
            result.add(event);
        }

        cursor.close();

        return result;
    }

    public int create(final ContentResolver contentResolver) {
        final ContentValues contentValues = mapToContentValues();
        final Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
        return id = Integer.parseInt(uri.getLastPathSegment());
    }

    public int update(final ContentResolver contentResolver) {
        final ContentValues contentValues = mapToContentValues();
        selfAttendeeStatus = null;
        contentResolver.update(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id), contentValues, null, null);
        return id;
    }
}
