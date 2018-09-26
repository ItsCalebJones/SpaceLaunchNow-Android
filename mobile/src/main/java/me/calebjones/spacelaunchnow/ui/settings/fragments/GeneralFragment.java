package me.calebjones.spacelaunchnow.ui.settings.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.calendar.CalendarSyncManager;
import me.calebjones.spacelaunchnow.calendar.model.Calendar;
import me.calebjones.spacelaunchnow.calendar.model.CalendarItem;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.ui.settings.util.CalendarPermissionListener;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import timber.log.Timber;

public class GeneralFragment extends BaseSettingFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context context;
    private Realm mRealm;
    private MultiplePermissionsListener allPermissionsListener;
    private SwitchPreferences switchPreferences;
    private CalendarSyncManager calendarSyncManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.general_preferences);

        context = getActivity();
        mRealm = Realm.getDefaultInstance();
        calendarSyncManager = new CalendarSyncManager(context);

        createPermissionListeners();
        setupPreference();
        setName("General Fragment");
    }

    @Override
    public void onResume() {
        Timber.v("onResume - setting OnSharedPreferenceChangeListener");
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        switchPreferences = SwitchPreferences.getInstance(this.context);
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.v("onPause - removing OnSharedPreferenceChangeListener");
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        Timber.d("onDestroy");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.i("General preference %s changed.", key);
        if (key.equals("calendar_reminder_array") ) {
            Analytics.getInstance().sendPreferenceEvent(key);
            calendarSyncManager.syncAllEevnts();
        } else if (key.equals("calendar_count")){
            Analytics.getInstance().sendPreferenceEvent(key);
            calendarSyncManager.resyncAllEvents();
        } else if (key.equals("calendar_sync_state")) {
            Timber.v("Calendar Sync State: %s", sharedPreferences.getBoolean(key, true));
            Analytics.getInstance().sendPreferenceEvent(key, sharedPreferences.getBoolean(key, false));
            if (sharedPreferences.getBoolean(key, true)) {
                Timber.v("Calendar Status: %s", switchPreferences.getCalendarStatus());
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    Timber.v("Calendar Permission - Granted");
                    switchPreferences.setCalendarStatus(true);
                    setCalendarPreference();
                    if (mRealm.where(CalendarItem.class).findFirst() == null){
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
        } else {
            Analytics.getInstance().sendPreferenceEvent(key);
        }
    }

    private void setupPreference() {
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
    }

    private void setCalendarPreference() {
        final List<Calendar> calendarList = Calendar.getWritableCalendars(context.getContentResolver());

        final ArrayList<String> listName = new ArrayList<String>();
        ArrayList<String> listCount = new ArrayList<String>();

        for (int i = 0; i < calendarList.size(); i++) {
            Calendar calendar = calendarList.get(i);
            String calendarName;
            if (calendar.displayName.equals(calendar.accountName)){
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
        calendarPrefList.setEntries(nameSequences);
        calendarPrefList.setEntryValues(countSequences);

        String summary;
        final CalendarItem calendarItem = mRealm.where(CalendarItem.class).findFirst();

        if (calendarItem != null){
            summary = getString(R.string.current_calendar) + calendarItem.getAccountName();
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
                        calendarSyncManager.resyncAllEvents();
                    }
                });

                return true;
            }
        });
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
                summary = getString(R.string.default_calendar) + " " +calendarItem.getAccountName();
            } else {
                summary = getString(R.string.select_calendar);
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
                    calendarSyncManager.syncAllEevnts();
                }
            });
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
                                .withIcon(R.mipmap.ic_launcher)
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

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("calendar_sync_state", false);
        editor.apply();
        SwitchPreference calendarSyncState = (SwitchPreference) findPreference("calendar_sync_state");
        calendarSyncState.setChecked(false);
        switchPreferences.setCalendarStatus(false);
        if (isPermanentlyDenied){
            Toast.makeText(context, R.string.calendar_permissions_denied, Toast.LENGTH_LONG).show();
        }
    }

}

