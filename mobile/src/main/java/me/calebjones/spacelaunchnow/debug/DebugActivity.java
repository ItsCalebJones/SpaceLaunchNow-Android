package me.calebjones.spacelaunchnow.debug;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.utils.ActivityUtils;

public class DebugActivity  extends AppCompatActivity{

    private DebugPresenter debugPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        DebugFragment debugFragment =
                (DebugFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (debugFragment == null) {
            // Create the fragment
            debugFragment = DebugFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), debugFragment, R.id.contentFrame);
        }

        // Create the presenter
        debugPresenter = new DebugPresenter(debugFragment);

        // Load previously saved state, if available.
        if (savedInstanceState != null) {

        }
    }
}
