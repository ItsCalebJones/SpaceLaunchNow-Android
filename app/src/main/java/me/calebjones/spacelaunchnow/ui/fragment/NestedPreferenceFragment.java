package me.calebjones.spacelaunchnow.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.TextView;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.utils.TimeRangePickerDialogCustom;
import timber.log.Timber;

public class NestedPreferenceFragment extends PreferenceFragmentCompat implements TimeRangePickerDialogCustom.OnTimeRangeSelectedListener {


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
    private static final String TAG_KEY = "NESTED_KEY";
    public static final String TIMERANGEPICKER_TAG = "timerangepicker";
    private TextView toolbarTitle;
    private static SharedPreference sharedPreference;
    private Context context;

    class SharedPreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        final /* synthetic */ SharedPreferences valprefs;

        SharedPreferenceListener(SharedPreferences sharedPreferences) {
            this.valprefs = sharedPreferences;
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Timber.d("onSharedPreferenceChanged:  %s ", key);
            try {
                if (key.equals("notifications")) {
                    if (this.valprefs.getBoolean(key, false)) {
                    }
                }
                if (key.equals("theme") && NestedPreferenceFragment.this.getActivity() != null) {
                    Editor themeEditor = NestedPreferenceFragment.this.getActivity().getSharedPreferences("theme_changed", 0).edit();
                    themeEditor.putBoolean("recreate", true);
                    themeEditor.apply();
                    NestedPreferenceFragment.this.getActivity().recreate();
                }
                if (key.equals("auto_theme") && NestedPreferenceFragment.this.getActivity() != null) {
                    Editor themeEditor = NestedPreferenceFragment.this.getActivity().getSharedPreferences("theme_changed", 0).edit();
                    themeEditor.putBoolean("recreate", true);
                    themeEditor.apply();
                    NestedPreferenceFragment.this.getActivity().recreate();
                }
            } catch (NullPointerException e) {

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
        if (getActivity() != null) {
            this.toolbarTitle = (TextView) getActivity().findViewById(R.id.title_text);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferenceListener(prefs));
        checkPreferenceResource();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    public void onResume() {
        super.onResume();
    }

    private void checkPreferenceResource() {
        switch (getArguments().getInt(TAG_KEY)) {
            case NESTED_SCREEN_1_KEY /*1*/:
                addPreferencesFromResource(R.xml.nested_notification_preferences);
                if (this.toolbarTitle != null) {
                    this.toolbarTitle.setText("Notification");
                }
                break;
            case NESTED_SCREEN_2_KEY /*2*/:
                addPreferencesFromResource(R.xml.nested_loader_preferences);
                if (this.toolbarTitle != null) {
                    this.toolbarTitle.setText("Launch Tracking Options");
                }
                break;
            case NESTED_SCREEN_3_KEY /*2*/:
                addPreferencesFromResource(R.xml.nested_appearance_preferences);
                if (this.toolbarTitle != null) {
                    this.toolbarTitle.setText("Appearance");
                }
                Preference myPref = findPreference("auto_theme_window");
                myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        //open browser or intent here
                        TimeRangePickerDialogCustom timePickerDialog = TimeRangePickerDialogCustom.newInstance(
                                NestedPreferenceFragment.this, false);
                        timePickerDialog.show(getActivity().getSupportFragmentManager(), "");
                        return false;
                    }
                });
                break;
            default:
        }
    }

    @Override
    public void onTimeRangeSelected(int startHour, int startMin, int endHour, int endMin) {
        this.context = getContext();
        sharedPreference = SharedPreference.getInstance(this.context);
        sharedPreference.setNightModeStart(startHour + ":" + startMin);
        sharedPreference.setNightModeEnd(endHour + ":" + endMin);
    }

}
