package me.calebjones.spacelaunchnow.common.ui.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat;
import com.jaredrummler.cyanea.prefs.CyaneaSettingsActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import io.realm.Realm;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.content.calendar.CalendarSyncManager;
import me.calebjones.spacelaunchnow.common.content.calendar.model.Calendar;
import me.calebjones.spacelaunchnow.common.content.calendar.model.CalendarItem;
import me.calebjones.spacelaunchnow.common.content.notifications.NotificationBuilder;
import me.calebjones.spacelaunchnow.common.content.worker.CalendarSyncWorker;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.common.ui.settings.util.CalendarPermissionListener;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_LAUNCH_REMINDER;
import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_LAUNCH_REMINDER_NAME;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Context context;
    private Realm mRealm;
    private MultiplePermissionsListener allPermissionsListener;
    private SwitchPreferences switchPreferences;
    private SharedPreferences sharedPreferences;
    private CalendarSyncManager calendarSyncManager;
    private Preference widgetPresets;
    private ColorPreferenceCompat widgetBackgroundColor;
    private ColorPreferenceCompat widgetTextColor;
    private ColorPreferenceCompat widgetSecondaryTextColor;
    private ColorPreferenceCompat widgetIconColor;
    private ColorPreferenceCompat widgetAccentColor;
    private ColorPreferenceCompat widgetTitleColor;
    private SwitchPreference widgetRoundCorners;
    private SwitchPreference widgetHideSettings;
    private FirebaseMessaging firebaseMessaging;
    private boolean isCustomColor = false;
    private int[] textPrimaryArray;
    private int[] textSecondaryArray;
    private int[] backgroundArray;
    private int[] accentArray;
    private int[] titleTextArray;
    private List<Calendar> calendarList;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preferences);

        context = getActivity();
        mRealm = Realm.getDefaultInstance();
        calendarSyncManager = new CalendarSyncManager(context);
        textPrimaryArray = getResources().getIntArray(R.array.widget_presets_values_text_primary);
        textSecondaryArray = getResources().getIntArray(R.array.widget_presets_values_text_secondary);
        backgroundArray = getResources().getIntArray(R.array.widget_presets_values_background);
        accentArray = getResources().getIntArray(R.array.widget_presets_values_accent);
        titleTextArray = getResources().getIntArray(R.array.widget_presets_values_title_text);
        firebaseMessaging = FirebaseMessaging.getInstance();

        createPermissionListeners();
        setupPreference();
    }

    @Override
    public void onResume() {
        Timber.v("onResume - setting OnSharedPreferenceChangeListener");
        switchPreferences = SwitchPreferences.getInstance(this.context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.v("onPause - removing OnSharedPreferenceChangeListener");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        Timber.d("onDestroy");
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();

        Timber.i("General preference %s changed.", key);
        switch (key) {
            case "calendar_reminder_array":
                calendarSyncManager.syncAllEevnts();
                break;
            case "calendar_count":
                calendarSyncManager.resyncAllEvents();
                CalendarSyncWorker.syncImmediately();
                break;
            case "calendar_sync_state":
                Timber.v("Calendar Sync State: %s", Prefs.getBoolean(key, false));
                if (Prefs.getBoolean(key, false)) {
                    Timber.v("Calendar Status: %s", switchPreferences.getCalendarStatus());
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                        Timber.v("Calendar Permission - Granted");
                        switchPreferences.setCalendarStatus(true);
                        setCalendarPreference();
                        if (mRealm.where(CalendarItem.class).findFirst() == null) {
                            setDefaultCalendar();
                        } else {
                            calendarSyncManager.syncAllEevnts();
                        }
                    } else {
                        Timber.v("Calendar Permission - Denied/Pending");
                        checkCalendarPermission();
                    }
                } else {
                    calendarSyncManager.deleteAllEvents();
                    switchPreferences.setCalendarStatus(false);
                }
                break;
            case "theme": {
                SharedPreferences.Editor themeEditor = getActivity().getSharedPreferences("theme_changed", 0).edit();
                themeEditor.putBoolean("recreate", true);
                themeEditor.apply();

                if (switchPreferences.getNightMode()) {
                    if (switchPreferences.getDayNightAutoMode()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                        checkLocationPermission();
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        Toast.makeText(context, R.string.night_mode_restart, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.day_mode_restart, Toast.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;
            }
            case "theme_auto": {
                SharedPreferences.Editor themeEditor = getActivity().getSharedPreferences("theme_changed", 0).edit();
                themeEditor.putBoolean("recreate", true);
                themeEditor.apply();

                if (switchPreferences.getNightMode()) {
                    if (switchPreferences.getDayNightAutoMode()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                        checkLocationPermission();
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        Toast.makeText(context, R.string.auto_daynight_restart, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;
            }
            case "widget_background_color":
            case "widget_text_color":
            case "widget_secondary_text_color":
            case "widget_icon_color":
            case "widget_title_text_color":
            case "widget_list_accent_color":
            case "widget_refresh_enabled": {
                Intent intent = null;
                try {
                    intent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.widgets.WidgetBroadcastReceiver"));
                    intent.putExtra("updateUIOnly", true);
                    context.sendBroadcast(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "widget_theme_round_corner": {
                Intent intent = null;
                try {
                    intent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.widgets.WidgetBroadcastReceiver"));
                    intent.putExtra("updateUIOnly", true);
                    context.sendBroadcast(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "widget_presets": {
                checkWidgetPreset(Integer.parseInt(sharedPreferences.getString("widget_presets", "2")));
                Intent intent = null;
                try {
                    intent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.widgets.WidgetBroadcastReceiver"));
                    intent.putExtra("updateUIOnly", true);
                    context.sendBroadcast(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "eventNotifications":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("eventNotifications");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("eventNotifications");
                }
                break;
            case "notificationEnabled":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("notificationEnabled");
                    firebaseMessaging.subscribeToTopic("webcastLive");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("notificationEnabled");
                    firebaseMessaging.unsubscribeFromTopic("webcastLive");
                }
                break;
            case "netstampChanged":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("netstampChanged");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("netstampChanged");
                }
                break;
            case "webcastOnly":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("webcastOnly");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("webcastOnly");
                }
                break;
            case "twentyFourHour":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("twentyFourHour");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("twentyFourHour");
                }
                break;
            case "oneHour":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("oneHour");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("oneHour");
                }
                break;
            case "tenMinutes":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("tenMinutes");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("tenMinutes");
                }
                break;
            case "oneMinute":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("oneMinute");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("oneMinute");
                }
                break;
            case "inFlight":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("inFlight");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("inFlight");
                }
                break;
            case "success":
                if (Prefs.getBoolean(key, true)) {
                    firebaseMessaging.subscribeToTopic("success");
                } else {
                    firebaseMessaging.unsubscribeFromTopic("success");
                }
                break;
            case "custom_themes":
                Intent intent = new Intent(context, CyaneaSettingsActivity.class);
                context.startActivity(intent);
                break;
            case "locale_changer":
                ActivityCompat.finishAffinity(getActivity());
                Intent mainIntent = null;
                try {
                    mainIntent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.ui.main.MainActivity"));
                    context.startActivity(mainIntent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    private void checkWidgetPreset(Integer arrayPosition) {
        try {
            int backgroundColor = backgroundArray[arrayPosition];
            int primaryTextColor = textPrimaryArray[arrayPosition];
            int secondaryTextColor = textSecondaryArray[arrayPosition];
            int accentColor = accentArray[arrayPosition];
            int textTitleColor = titleTextArray[arrayPosition];
            Timber.v("Preset # %d", arrayPosition);
            if (arrayPosition != 8) {
                widgetBackgroundColor.saveValue(backgroundColor);
                widgetTextColor.saveValue(primaryTextColor);
                widgetSecondaryTextColor.saveValue(secondaryTextColor);
                widgetIconColor.saveValue(primaryTextColor);
                widgetAccentColor.saveValue(accentColor);
                widgetTitleColor.saveValue(textTitleColor);
                isCustomColor = false;
                Timber.v("Applied widget colors");
            } else {
                isCustomColor = true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void setupPreference() {

        Preference testPreference = findPreference("notifications_new_message_test");
        testPreference.setOnPreferenceClickListener(preference -> {
            Thread t = new Thread(this::sendTestNotification);
            t.start();
            return true;
        });
        Preference filterPreference = findPreference("notification_filters");
        filterPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = null;
            try {
                intent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.ui.main.MainActivity"));
                intent.setAction("SHOW_FILTERS");
                context.startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Preference manageNotifications = findPreference("manage_notification_channel");
            manageNotifications.setOnPreferenceClickListener(preference -> {
                Intent notificationSettings = new Intent();
                notificationSettings.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                //for Android 5-7
                notificationSettings.putExtra("app_package", context.getPackageName());
                notificationSettings.putExtra("app_uid", context.getApplicationInfo().uid);

                // for Android O
                notificationSettings.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

                startActivity(notificationSettings);
                return true;
            });
        }
        SwitchPreference calendarSyncState = (SwitchPreference) findPreference("calendar_sync_state");
        PreferenceCategory calendarCategory = (PreferenceCategory) findPreference("calendar_category");
        if (!SupporterHelper.isSupporter()) {
            calendarSyncState.setChecked(false);
            calendarSyncState.setEnabled(false);
            calendarCategory.setTitle(calendarCategory.getTitle() + getString(R.string.supporter_feature));
        } else {
            if (calendarSyncState.isChecked() && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                setCalendarPreference();
            } else if (calendarSyncState.isChecked() && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, R.string.calendar_permissions_denied, Toast.LENGTH_LONG).show();
            }
        }
        widgetPresets = findPreference("widget_presets");
        widgetBackgroundColor = (ColorPreferenceCompat) findPreference("widget_background_color");
        widgetTextColor = (ColorPreferenceCompat) findPreference("widget_text_color");
        widgetSecondaryTextColor = (ColorPreferenceCompat) findPreference("widget_secondary_text_color");
        widgetIconColor = (ColorPreferenceCompat) findPreference("widget_icon_color");
        widgetRoundCorners = (SwitchPreference) findPreference("widget_theme_round_corner");
        widgetAccentColor = (ColorPreferenceCompat) findPreference("widget_list_accent_color");
        widgetTitleColor = (ColorPreferenceCompat) findPreference("widget_title_text_color");
        widgetHideSettings = (SwitchPreference) findPreference("widget_refresh_enabled");
        if (!SupporterHelper.isSupporter()) {
            Preference themes = findPreference("custom_themes");
            themes.setEnabled(false);
            themes.setSelectable(false);
            themes.setTitle(themes.getTitle() + " " + getString(R.string.supporter_feature));

            Preference weather = findPreference("weather");
            weather.setEnabled(false);
            weather.setSelectable(false);

            PreferenceCategory prefCatWeather = (PreferenceCategory) findPreference("weather_category");
            prefCatWeather.setTitle(prefCatWeather.getTitle() + " " + getString(R.string.supporter_feature));
            Preference measurement = findPreference("weather_US_SI");
            measurement.setEnabled(false);
            measurement.setSelectable(false);

            PreferenceCategory prefCatWidget = (PreferenceCategory) findPreference("widget_category");
            prefCatWidget.setTitle(prefCatWidget.getTitle() + " " + getString(R.string.supporter_feature));


            widgetPresets.setEnabled(false);
            widgetPresets.setSelectable(false);

            widgetBackgroundColor.setEnabled(false);
            widgetBackgroundColor.setSelectable(false);

            widgetTextColor.setEnabled(false);
            widgetTextColor.setSelectable(false);

            widgetSecondaryTextColor.setEnabled(false);
            widgetSecondaryTextColor.setSelectable(false);

            widgetIconColor.setEnabled(false);
            widgetIconColor.setSelectable(false);

            widgetRoundCorners.setEnabled(false);
            widgetRoundCorners.setSelectable(false);

            widgetAccentColor.setEnabled(false);
            widgetAccentColor.setSelectable(false);

            widgetTitleColor.setEnabled(false);
            widgetTitleColor.setSelectable(false);

            widgetHideSettings.setEnabled(false);
            widgetHideSettings.setSelectable(false);
        }
        Preference localTime = findPreference("local_time");
        localTime.setOnPreferenceChangeListener(createLocalTimeListener());
    }

    private Preference.OnPreferenceChangeListener createLocalTimeListener() {
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                Timber.v("Clicked!");
                return true;
            }

        };
    }

    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Dexter.withActivity(getActivity()).withPermission(Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    if (response.isPermanentlyDenied()) {
                        Toast.makeText(context, R.string.location_denied, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        }
    }


    private void setCalendarPreference() {
        calendarList = Calendar.getWritableCalendars(context.getContentResolver());

        final ArrayList<String> listName = new ArrayList<String>();
        ArrayList<String> listCount = new ArrayList<String>();

        for (int i = 0; i < calendarList.size(); i++) {
            Calendar calendar = calendarList.get(i);
            String calendarName;
            if (calendar.displayName.equals(calendar.accountName)) {
                calendarName = calendar.accountName;
            } else {
                calendarName = calendar.displayName + " - " + calendar.accountName;
            }
            listName.add(calendarName);
            listCount.add(String.valueOf(i));
        }

        final CharSequence[] nameSequences = listName.toArray(new CharSequence[listName.size()]);
        final CharSequence[] countSequences = listCount.toArray(new CharSequence[listCount.size()]);
        ListPreference calendarPrefList = (ListPreference) findPreference("default_calendar_state");


        String summary;
        final CalendarItem calendarItem = mRealm.where(CalendarItem.class).findFirst();

        if (calendarItem != null) {
            summary = getString(R.string.current_calendar) + " " + calendarItem.getAccountName();
        } else {
            summary = getString(R.string.select_calendar);
        }
        calendarPrefList.setSummary(summary);
        calendarPrefList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object obj) {
                final Calendar calendar = calendarList.get(Integer.valueOf(obj.toString()));
                Timber.v("Updating selected calendar to %s", calendar.displayName);
                final CalendarItem calendarItem = new CalendarItem();
                calendarItem.setId(calendar.id);
                calendarItem.setAccountName(calendar.displayName);
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        CalendarItem oldCalendar = realm.where(CalendarItem.class).findFirst();
                        if (oldCalendar != null && (oldCalendar.getId() != calendarItem.getId())) {
                            realm.where(CalendarItem.class).findAll().deleteAllFromRealm();
                            realm.copyToRealm(calendarItem);
                        } else {
                            realm.copyToRealmOrUpdate(calendarItem);
                        }
                    }
                });
                Timber.v("Successfully updated active Calendar, sending resync.");
                setCalendarPreference();
                calendarSyncManager.resyncCalendarItem();
                calendarSyncManager.resyncAllEvents();
                return true;
            }
        });
        calendarPrefList.setEntries(nameSequences);
        calendarPrefList.setEntryValues(countSequences);
        calendarPrefList.setPersistent(true);
    }

    private void setDefaultCalendar() {
        final List<Calendar> calendarList = Calendar.getWritableCalendars(context.getContentResolver());

        if (calendarList.size() > 0) {
            ListPreference calendarPreference = (ListPreference) findPreference("default_calendar_state");
            String summary;
            final CalendarItem calendarItem = new CalendarItem();
            calendarItem.setAccountName(calendarList.get(0).accountName);
            calendarItem.setId(calendarList.get(0).id);

            if (calendarItem != null) {
                summary = getString(R.string.default_calendar) + " " + calendarItem.getAccountName();
            } else {
                summary = getString(R.string.select_calendar);
            }
            calendarPreference.setSummary(summary);
            mRealm.executeTransactionAsync(realm -> {
                realm.where(CalendarItem.class).findAll().deleteAllFromRealm();
                realm.copyToRealm(calendarItem);
            }, () -> calendarSyncManager.syncAllEevnts());
            setCalendarPreference();
        } else {
            Toast.makeText(context, R.string.no_calendars_available, Toast.LENGTH_LONG).show();
            SwitchPreference calendarSyncState = (SwitchPreference) findPreference("calendar_sync_state");
            calendarSyncState.setChecked(false);
            switchPreferences.setCalendarStatus(false);
        }
    }

    private void createPermissionListeners() {
        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new CalendarPermissionListener(this);

        allPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        DialogOnAnyDeniedMultiplePermissionsListener.Builder.withContext(context)
                                .withTitle(R.string.permission_denied)
                                .withMessage(R.string.permission_denied_description)
                                .withButtonText(android.R.string.ok)
                                .withIcon(R.drawable.ic_launcher)
                                .build());
    }

    public void checkCalendarPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Dexter.withActivity(getActivity())
                    .withPermissions(Manifest.permission.READ_CALENDAR,
                            Manifest.permission.WRITE_CALENDAR)
                    .withListener(allPermissionsListener).check();
        }
    }

    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(context).setTitle(R.string.calendar_permission_required)
                .setMessage(R.string.calendar_permission_required_description)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    switchPreferences.setCalendarStatus(false);
                    dialog.dismiss();
                    token.cancelPermissionRequest();
                })
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    token.continuePermissionRequest();
                })
                .setOnDismissListener(dialog -> {
                    switchPreferences.setCalendarStatus(false);
                    token.cancelPermissionRequest();
                })
                .show();
    }

    public void showPermissionGranted(String permission) {
        switchPreferences.setCalendarStatus(true);
        setCalendarPreference();
        if (mRealm.where(CalendarItem.class).findFirst() == null) {
            setDefaultCalendar();
        }
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        Prefs.putBoolean("calendar_sync_state", false);
        SwitchPreference calendarSyncState = (SwitchPreference) findPreference("calendar_sync_state");
        calendarSyncState.setChecked(false);
        switchPreferences.setCalendarStatus(false);
        if (isPermanentlyDenied) {
            Toast.makeText(context, R.string.calendar_permissions_denied, Toast.LENGTH_LONG).show();
        }
    }

    private void sendTestNotification() {
        Realm realm = Realm.getDefaultInstance();
        Launch launch = realm.where(Launch.class)
                .greaterThanOrEqualTo("net", new Date())
                .sort("net", Sort.ASCENDING).findFirst();

        if (launch != null) {
            NotificationBuilder.buildNotification(context, launch, launch.getName(), String.format("This is a test notification! (Channel - %s)", CHANNEL_LAUNCH_REMINDER_NAME), CHANNEL_LAUNCH_REMINDER);
        }
        realm.close();
    }
}
