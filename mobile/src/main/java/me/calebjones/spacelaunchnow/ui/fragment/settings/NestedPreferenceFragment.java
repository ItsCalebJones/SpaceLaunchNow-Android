package me.calebjones.spacelaunchnow.ui.fragment.settings;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.calendar.CalendarSyncService;
import me.calebjones.spacelaunchnow.calendar.model.Calendar;
import me.calebjones.spacelaunchnow.calendar.model.CalendarItem;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.supporter.Products;
import me.calebjones.spacelaunchnow.content.receivers.MultiplePermissionListener;
import timber.log.Timber;


public class NestedPreferenceFragment extends PreferenceFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Give your color picker dialog unique IDs if you
    // have multiple dialog. This will make it possible
    // for you to distinguish between them when you
    // get a result back in your ColorPickerDialogListener.
    private static final int PRIMARY_ID = 0;
    private static final int ACCENT_ID = 1;

    public static final int NESTED_SCREEN_1_KEY = 1;
    public static final int NESTED_SCREEN_2_KEY = 2;
    public static final int NESTED_SCREEN_3_KEY = 3;
    public static final int NESTED_SCREEN_4_KEY = 4;
    public static final String TIMERANGEPICKER_TAG = "timerangepicker";
    private static final String TAG_KEY = "NESTED_KEY";
    private static ListPreferences listPreferences;
    private TextView toolbarTitle;
    private SwitchPreferences switchPreferences;
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private MultiplePermissionsListener allPermissionsListener;
    private static final String HOUR_KEY = "me.calebjones.spacelaunchnow.wear.hourmode";
    private static final String BACKGROUND_KEY = "me.calebjones.spacelaunchnow.wear.background";
    private static final int BACKGROUND_NORMAL = 0;
    private static final int BACKGROUND_CUSTOM = 1;
    private static final int BACKGROUND_DYNAMIC = 2;
    private Realm mRealm;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void checkCalendarPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Dexter.isRequestOngoing()) {
                return;
            }
            Dexter.checkPermissions(allPermissionsListener, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        }
    }

    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Dexter.isRequestOngoing()) {
                return;
            }
            Dexter.checkPermissions(allPermissionsListener, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    class SharedPreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        final SharedPreferences valPrefs;

        SharedPreferenceListener(SharedPreferences sharedPreferences) {
            this.valPrefs = sharedPreferences;
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switchPreferences = SwitchPreferences.getInstance(getActivity());
            Timber.d("onSharedPreferenceChanged:  %s ", key);
            try {
                if (key.equals("notifications")) {
                    if (this.valPrefs.getBoolean(key, false)) {
                    }
                }
                if (key.equals("theme") && NestedPreferenceFragment.this.getActivity() != null) {
                    Editor themeEditor = NestedPreferenceFragment.this.getActivity().getSharedPreferences("theme_changed", 0).edit();
                    themeEditor.putBoolean("recreate", true);
                    themeEditor.apply();

                    if (switchPreferences.getNightMode()) {
                        if (switchPreferences.getDayNightAutoMode()) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                            checkLocationPermission();
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            Toast.makeText(context, "Night mode might need to restart app to take effect.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Day mode might need to restart app to take effect.", Toast.LENGTH_SHORT).show();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    NestedPreferenceFragment.this.getActivity().recreate();
                }
                if (key.equals("theme_auto") && NestedPreferenceFragment.this.getActivity() != null) {
                    Editor themeEditor = NestedPreferenceFragment.this.getActivity().getSharedPreferences("theme_changed", 0).edit();
                    themeEditor.putBoolean("recreate", true);
                    themeEditor.apply();

                    if (switchPreferences.getNightMode()) {
                        if (switchPreferences.getDayNightAutoMode()) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                            checkLocationPermission();
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            Toast.makeText(context, "Auto DayNight disabled, might need to restart app to take effect.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    NestedPreferenceFragment.this.getActivity().recreate();
                }
                if (key.equals("notifications_launch_imminent_updates")) {
                    OneSignal.setSubscription(this.valPrefs.getBoolean(key, false));
                }
                if (key.equals("wear_hour_mode")) {
                    PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/config");
                    putDataMapReq.getDataMap().putBoolean(HOUR_KEY, this.valPrefs.getBoolean(key, false));
                    putDataMapReq.getDataMap().putLong("time", new Date().getTime());

                    PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
                }

                if (key.equals("calendar_reminder_array") ) {
                    CalendarSyncService.startActionSyncAll(context);
                }

                if (key.equals("calendar_count")){
                    CalendarSyncService.startActionResync(context);
                }

                if (key.equals("calendar_sync_state")) {
                    Timber.v("Calendar Sync State: %s", this.valPrefs.getBoolean(key, true));
                    if (this.valPrefs.getBoolean(key, true)) {
                        Timber.v("Calendar Status: %s", switchPreferences.getCalendarStatus());
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                            Timber.v("Calendar Permission - Granted");
                            switchPreferences.setCalendarStatus(true);
                            setCalendarPreference();
                            if (mRealm.where(CalendarItem.class).findFirst() == null){
                                setDefaultCalendar();
                            } else {
                                CalendarSyncService.startActionSyncAll(context);
                            }
                        } else {
                            Timber.v("Calendar Permission - Denied/Pending");
                            checkCalendarPermission();
                        }
                    } else {
                        CalendarSyncService.startActionDeleteAll(context);
                        switchPreferences.setCalendarStatus(false);
                    }
                }
            } catch (NullPointerException e) {
                Crashlytics.logException(e);
            }
        }
    }

    public static NestedPreferenceFragment newInstance(int key) {
        NestedPreferenceFragment fragment = new NestedPreferenceFragment();
        Bundle args = new Bundle();
        args.putInt(TAG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        createPermissionListeners();
        Dexter.continuePendingRequestsIfPossible(allPermissionsListener);

        mRealm = Realm.getDefaultInstance();

        if (getActivity() != null) {
            this.toolbarTitle = (TextView) getActivity().findViewById(R.id.title_text);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    public void onResume() {
        Timber.v("onResume");
        listPreferences = ListPreferences.getInstance(this.context);
        switchPreferences = SwitchPreferences.getInstance(this.context);
        listPreferences.isNightModeActive(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferenceListener(prefs));
        checkPreferenceResource();
        super.onResume();
    }

    private void checkPreferenceResource() {
        Realm realm = Realm.getDefaultInstance();
        boolean supporter = false;
        RealmResults<Products> realmResults = realm.where(Products.class).findAll();
        if (realmResults.size() > 0) {
            supporter = true;
        }
        mGoogleApiClient.connect();
        switch (getArguments().getInt(TAG_KEY)) {
            case NESTED_SCREEN_1_KEY:
                addPreferencesFromResource(R.xml.nested_notification_preferences);
                if (this.toolbarTitle != null) {
                    this.toolbarTitle.setText("Notifications");
                }
                break;
            case NESTED_SCREEN_2_KEY:
                addPreferencesFromResource(R.xml.nested_loader_preferences);

                SwitchPreference calendarSyncState = (SwitchPreference) findPreference("calendar_sync_state");
                PreferenceCategory calendarCategory = (PreferenceCategory) findPreference("calendar_category");
                if (!supporter) {
                    calendarSyncState.setChecked(false);
                    calendarSyncState.setEnabled(false);
                    calendarCategory.setTitle(calendarCategory.getTitle() + " (Supporter Feature)");
                } else {
                    if (calendarSyncState.isChecked() && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                        setCalendarPreference();
                    } else if (calendarSyncState.isChecked() && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Calendar permission denied, try re-selecting or go to Android Settings -> Apps to enable.", Toast.LENGTH_LONG).show();
                    }
                }

                if (this.toolbarTitle != null) {
                    this.toolbarTitle.setText("General");
                }
                break;
            case NESTED_SCREEN_3_KEY:
                addPreferencesFromResource(R.xml.nested_appearance_preferences);
                if (!supporter) {
                    Preference weather = findPreference("weather");
                    weather.setEnabled(false);
                    weather.setSelectable(false);
                    PreferenceCategory prefCatWeather = (PreferenceCategory) findPreference("weather_category");
                    prefCatWeather.setTitle(prefCatWeather.getTitle() + " (Supporter Feature)");
                    Preference measurement = findPreference("weather_US_SI");
                    measurement.setEnabled(false);
                    measurement.setSelectable(false);
                }
                if (this.toolbarTitle != null) {
                    this.toolbarTitle.setText("Appearance");
                }
                break;
            case NESTED_SCREEN_4_KEY:
                addPreferencesFromResource(R.xml.nested_wear_preferences);
                Preference dynamicBackground = findPreference("supporter_dynamic_background");
                dynamicBackground.setEnabled(false);
                dynamicBackground.setSelectable(false);
                dynamicBackground.setTitle(dynamicBackground.getTitle() + " (Coming Soon)");

                //TODO implement dynamic background
//                if (supporter) {
//                    dynamicBackground.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                        @Override
//                        public boolean onPreferenceClick(Preference preference) {
//                            Toast.makeText(context, "Clicked: " + preference.getKey(), Toast.LENGTH_LONG).show();
//                            return true;
//                        }
//                    });
//                } else {
//                    dynamicBackground.setEnabled(false);
//                    dynamicBackground.setSelectable(false);
//                    dynamicBackground.setTitle(dynamicBackground.getTitle() + " (Supporter Feature)");
//                }

                if (this.toolbarTitle != null) {
                    this.toolbarTitle.setText("Wear");
                }
                break;
            default:
        }

    }

    private void setCalendarPreference() {
        final List<Calendar> calendarList = Calendar.getWritableCalendars(context.getContentResolver());

        final ArrayList<String> listName = new ArrayList<String>();
        ArrayList<String> listCount = new ArrayList<String>();

        for (int i = 0; i < calendarList.size(); i++) {
            Calendar calendar = calendarList.get(i);
            listName.add(calendar.accountName);
            listCount.add(String.valueOf(i));
        }

        final CharSequence[] nameSequences = listName.toArray(new CharSequence[listName.size()]);
        final CharSequence[] countSequences = listCount.toArray(new CharSequence[listCount.size()]);
        ListPreference calendarPrefernce = (ListPreference) findPreference("default_calendar_state");
        calendarPrefernce.setEntries(nameSequences);
        calendarPrefernce.setEntryValues(countSequences);

        String summary;
        final CalendarItem calendarItem = mRealm.where(CalendarItem.class).findFirst();

        if (calendarItem != null){
            summary = "Current calendar: " + calendarItem.getAccountName();
        } else {
            summary = "Select a Calendar to add launch events.";
        }
        calendarPrefernce.setSummary(summary);
        calendarPrefernce.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object obj) {
                final Calendar calendar = calendarList.get(Integer.valueOf(obj.toString()));
                Timber.v("Updating selected calendar to %s", calendar.accountName);
                final CalendarItem calendarItem = new CalendarItem();
                calendarItem.setId(calendar.id);
                calendarItem.setAccountName(calendar.accountName);
                mRealm.executeTransactionAsync(new Realm.Transaction() {
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
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Timber.v("Successfully updated active Calendar, sending resync.");
                        setCalendarPreference();
                        CalendarSyncService.startActionResync(context);
                    }
                });

                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        Timber.d("onDestroy");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Timber.d("Google Client Disconnect");
            mGoogleApiClient.disconnect();
        }
    }

    private void createPermissionListeners() {
        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new MultiplePermissionListener(this);

        allPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        DialogOnAnyDeniedMultiplePermissionsListener.Builder.withContext(context)
                                .withTitle("Permission Denied")
                                .withMessage("If you change your mind, try to enable again. Or go to Settings -> Application -> Space Launch Now -> Permissions.")
                                .withButtonText(android.R.string.ok)
                                .withIcon(R.mipmap.ic_launcher)
                                .build());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(context).setTitle("Calendar Permission Needed")
                .setMessage("This permission is needed to sync launches with your calendar.")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchPreferences.setCalendarStatus(false);
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        switchPreferences.setCalendarStatus(false);
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    public void showPermissionGranted(String permission) {
        switchPreferences.setCalendarStatus(true);
        setCalendarPreference();
        if (mRealm.where(CalendarItem.class).findFirst() == null){
            setDefaultCalendar();
        }
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
                summary = "Default calendar: " + calendarItem.getAccountName();
            } else {
                summary = "Select a Calendar to add launch events.";
            }
            calendarPreference.setSummary(summary);
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(CalendarItem.class).findAll().deleteAllFromRealm();
                    realm.copyToRealm(calendarItem);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    CalendarSyncService.startActionSyncAll(context);
                }
            });
        }
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("calendar_sync_state", false);
        editor.apply();
        SwitchPreference calendarSyncState = (SwitchPreference) findPreference("calendar_sync_state");
        calendarSyncState.setChecked(false);
        switchPreferences.setCalendarStatus(false);
        if (isPermanentlyDenied){
            Toast.makeText(context, "Calendar permission denied, please go to Android Settings -> Apps to enable.", Toast.LENGTH_LONG).show();
        }
    }
}
