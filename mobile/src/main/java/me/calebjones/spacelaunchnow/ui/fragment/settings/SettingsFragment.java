package me.calebjones.spacelaunchnow.ui.fragment.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.crashlytics.android.Crashlytics;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.ui.activity.AboutActivity;
import me.calebjones.spacelaunchnow.ui.activity.SupportActivity;

public class SettingsFragment extends PreferenceFragment implements android.preference.Preference.OnPreferenceClickListener {
    
    private static final String NOTIFICATIONS = "notifications";
    private static final String LAUNCH_TRACKING_OPTIONS = "launch_tracking_options";
    private static final String APPEARANCE = "appearance";
    private static final String WEAR = "wear";
    private static final String ABOUT = "about";
    private static final String SUPPORT = "support";
    
    private static ListPreferences sharedPreference;
    private Context context;
    private Callback mCallback;

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(NOTIFICATIONS)) {
            mCallback.onNestedPreferenceSelected(1);
        }
        if (preference.getKey().equals(LAUNCH_TRACKING_OPTIONS)) {
            mCallback.onNestedPreferenceSelected(2);
        }
        if (preference.getKey().equals(APPEARANCE)) {
            mCallback.onNestedPreferenceSelected(3);
        }
        if (preference.getKey().equals(WEAR)) {
            mCallback.onNestedPreferenceSelected(4);
        }
        if (preference.getKey().equals(ABOUT)) {
            Intent intent = new Intent(getActivity().getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        }
        if (preference.getKey().equals(SUPPORT)) {
            Intent intent = new Intent(getActivity().getApplicationContext(), SupportActivity.class);
            startActivity(intent);
        }
        return false;
    }

    public interface Callback {
        void onNestedPreferenceSelected(int i);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        context = getActivity().getApplicationContext();
        sharedPreference = ListPreferences.getInstance(context);

        try {
            if (sharedPreference.isNightModeActive(context)) {
                addPreferencesFromResource(R.xml.dark_settings_fragment);
            } else {
                addPreferencesFromResource(R.xml.light_settings_fragment);
            }
        } catch (NullPointerException e) {
            Crashlytics.logException(e);
        }

        if (getActivity() instanceof Callback) {
            this.mCallback = (Callback) getActivity();
        }
        
        findPreference(NOTIFICATIONS).setOnPreferenceClickListener(this);
        findPreference(LAUNCH_TRACKING_OPTIONS).setOnPreferenceClickListener(this);
        findPreference(APPEARANCE).setOnPreferenceClickListener(this);
        findPreference(WEAR).setOnPreferenceClickListener(this);
        findPreference(ABOUT).setOnPreferenceClickListener(this);
        findPreference(SUPPORT).setOnPreferenceClickListener(this);
    }

    public void onResume() {
        super.onResume();
    }
}
