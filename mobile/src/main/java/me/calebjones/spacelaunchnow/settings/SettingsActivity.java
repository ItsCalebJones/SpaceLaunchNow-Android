package me.calebjones.spacelaunchnow.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;

import de.mrapp.android.preference.activity.PreferenceActivity;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public final void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected final void onCreatePreferenceHeaders() {
        if (BuildConfig.DEBUG){
            addPreferenceHeadersFromResource(R.xml.preference_headers_debug);
        } else {
            addPreferenceHeadersFromResource(R.xml.preference_headers);
        }
    }
}
