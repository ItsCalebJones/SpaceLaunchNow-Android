package me.calebjones.spacelaunchnow.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference.OnPreferenceClickListener;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;

public class SettingsFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    
    private static final String NOTIFICATIONS = "notifications";
    private static final String LAUNCH_TRACKING_OPTIONS = "launch_tracking_options";
    private static final String APPEARANCE = "appearance";
    
    private static SharedPreference sharedPreference;
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
        return false;
    }

    public interface Callback {
        void onNestedPreferenceSelected(int i);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        context = getActivity().getApplicationContext();
        sharedPreference = SharedPreference.getInstance(context);

        try {
            if (sharedPreference.getNightMode()) {
                addPreferencesFromResource(R.xml.dark_settings_fragment);
            } else {
                addPreferencesFromResource(R.xml.light_settings_fragment);
            }
        } catch (NullPointerException e) {
        }

        if (getActivity() instanceof Callback) {
            this.mCallback = (Callback) getActivity();
        }
        
        findPreference(NOTIFICATIONS).setOnPreferenceClickListener(this);
        findPreference(LAUNCH_TRACKING_OPTIONS).setOnPreferenceClickListener(this);
        findPreference(APPEARANCE).setOnPreferenceClickListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    public void onResume() {
        super.onResume();
    }
}
