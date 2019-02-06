package me.calebjones.spacelaunchnow.wear.ui.launch;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import androidx.wear.widget.drawer.WearableDrawerLayout;
import androidx.wear.widget.drawer.WearableNavigationDrawerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.wear.R;

public class LaunchActivity extends WearableActivity implements NavigationAdapter.AdapterCallback{

    @BindView(R.id.drawer_layout)
    WearableDrawerLayout wearableDrawerLayout;
    @BindView(R.id.top_navigation_drawer)
    WearableNavigationDrawerView wearableNavigationDrawer;

    private LaunchFragment launchFragment;
    private NavigationAdapter navigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);

        // Initialize content to first planet.
        launchFragment = new LaunchFragment();
        Bundle args = new Bundle();

        args.putInt("category", 0);
        launchFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, launchFragment).commit();

        navigationAdapter = new NavigationAdapter(this, this);

        // Top Navigation Drawer
        wearableNavigationDrawer = findViewById(R.id.top_navigation_drawer);
        wearableNavigationDrawer.setAdapter(navigationAdapter);
        wearableNavigationDrawer.getController().peekDrawer();
        wearableNavigationDrawer.addOnItemSelectedListener(navigationAdapter);

    }

    @Override
    public void onMethodCallback(int category) {
        launchFragment.updateCategories(category);
    }
}
