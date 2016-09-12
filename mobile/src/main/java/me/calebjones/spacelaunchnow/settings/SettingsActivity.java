package me.calebjones.spacelaunchnow.settings;

import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;


public class SettingsActivity extends BasePreferenceActivity {

    @Override
    protected final void onCreatePreferenceHeaders() {
        if (BuildConfig.DEBUG){
            addPreferenceHeadersFromResource(R.xml.preference_headers_debug);
        } else {
            addPreferenceHeadersFromResource(R.xml.preference_headers);
        }
    }
}
