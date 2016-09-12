package me.calebjones.spacelaunchnow.settings.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.PreferenceFragment;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.supporter.SupporterHelper;
import timber.log.Timber;

public class AppearanceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreferences switchPreferences;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.appearance_preferences);
        switchPreferences = SwitchPreferences.getInstance(getActivity());
        context = getActivity();
        setupPreferences();
    }

    @Override
    public void onResume() {
        Timber.v("onResume - setting OnSharedPreferenceChangeListener");
//        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.v("onPause - removing OnSharedPreferenceChangeListener");
//        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.i("Appearance preference %s changed.", key);
        if (key.equals("theme")) {
            SharedPreferences.Editor themeEditor = getActivity().getSharedPreferences("theme_changed", 0).edit();
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
            getActivity().recreate();
            Intent intent = getActivity().getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                    AppearanceFragment.class.getName());
            getActivity().finish();
            startActivity(intent);
        }
        if (key.equals("theme_auto")) {
            SharedPreferences.Editor themeEditor = getActivity().getSharedPreferences("theme_changed", 0).edit();
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
            getActivity().recreate();
            Intent intent = getActivity().getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                    AppearanceFragment.class.getName());
            getActivity().finish();
            startActivity(intent);
        }
    }

    private void setupPreferences() {
        if (!SupporterHelper.isSupporter()) {
            Preference weather = findPreference("weather");
            weather.setEnabled(false);
            weather.setSelectable(false);
            PreferenceCategory prefCatWeather = (PreferenceCategory) findPreference("weather_category");
            prefCatWeather.setTitle(prefCatWeather.getTitle() + " (Supporter Feature)");
            Preference measurement = findPreference("weather_US_SI");
            measurement.setEnabled(false);
            measurement.setSelectable(false);
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
            if (Dexter.isRequestOngoing()) {
                return;
            }
            Dexter.checkPermission(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    if (response.isPermanentlyDenied()){
                        Toast.makeText(context, "Location denied, please go to Android Settings -> Apps to enable.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }
}
