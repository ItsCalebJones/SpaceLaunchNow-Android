package me.calebjones.spacelaunchnow.common.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;

import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import me.calebjones.spacelaunchnow.common.R;

public class SettingsActivity extends CyaneaAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
