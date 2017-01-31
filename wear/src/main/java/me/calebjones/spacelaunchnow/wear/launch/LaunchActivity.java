package me.calebjones.spacelaunchnow.wear.launch;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.view.Gravity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.wear.R;

public class LaunchActivity extends WearableActivity implements NavigationAdapter.AdapterCallback{

    @BindView(R.id.drawer_layout)
    WearableDrawerLayout wearableDrawerLayout;
    @BindView(R.id.top_navigation_drawer)
    WearableNavigationDrawer wearableNavigationDrawer;

    private LaunchFragment launchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);

        // Initialize content to first planet.
        launchFragment = new LaunchFragment();
        Bundle args = new Bundle();

        args.putInt("category", 1);
        launchFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, launchFragment).commit();

        // Main Wearable Drawer Layout that wraps all content
        wearableDrawerLayout.peekDrawer(Gravity.BOTTOM);
        wearableDrawerLayout.peekDrawer(Gravity.TOP);

        // Top Navigation Drawer
        wearableNavigationDrawer = (WearableNavigationDrawer) findViewById(R.id.top_navigation_drawer);
        wearableNavigationDrawer.setAdapter(new NavigationAdapter(this));

    }

    @Override
    public void onMethodCallback(int category) {
        launchFragment.updateCategories(category);
    }
}
