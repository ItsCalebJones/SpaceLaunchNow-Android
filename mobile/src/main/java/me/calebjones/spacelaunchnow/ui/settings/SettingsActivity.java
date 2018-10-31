package me.calebjones.spacelaunchnow.ui.settings;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;

import de.mrapp.android.preference.activity.PreferenceActivity;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public final void onStart() {
        super.onStart();
        Analytics.getInstance().sendScreenView("Settings", "Settings started.");
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
