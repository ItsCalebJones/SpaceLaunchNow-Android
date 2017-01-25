package me.calebjones.spacelaunchnow.debug;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.utils.ActivityUtils;

public class DebugActivity extends AppCompatActivity implements DebugContract.NavigatorProvider {

    private DebugPresenter debugPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Debug Menu");
        ab.setSubtitle("Authorized persons only!");
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
        debugPresenter = new DebugPresenter(debugFragment, ListPreferences.getInstance(this.getApplication()));
        debugPresenter.setNavigator(getNavigator(debugPresenter));

        // Load previously saved state, if available.
        if (savedInstanceState != null) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                debugPresenter.onHomeClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public DebugContract.Navigator getNavigator(DebugContract.Presenter presenter) {
        return new DebugNavigator(this);
    }
}
