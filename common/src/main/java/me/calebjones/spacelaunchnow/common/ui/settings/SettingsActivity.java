package me.calebjones.spacelaunchnow.common.ui.settings;

import android.os.Bundle;

import com.jaredrummler.cyanea.app.CyaneaPreferenceActivity;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import androidx.appcompat.app.ActionBar;

import androidx.appcompat.widget.Toolbar;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.BuildConfig;

public class SettingsActivity extends CyaneaPreferenceActivity {

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
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        if (BuildConfig.DEBUG){
            loadHeadersFromResource(R.xml.preference_headers_debug, target);
        } else {
            loadHeadersFromResource(R.xml.preference_headers, target);
        }
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
