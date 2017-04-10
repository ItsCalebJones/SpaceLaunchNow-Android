package me.calebjones.spacelaunchnow.ui.main.vehicles;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseFragment;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.ui.main.vehicles.launcher.LauncherFragment;
import me.calebjones.spacelaunchnow.ui.main.vehicles.orbiter.OrbiterFragment;

public class VehiclesViewPager extends BaseFragment {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private int current_tab;
    private TabLayout tabLayout;
    private LauncherFragment launchVehicleFragment;
    private OrbiterFragment orbiterFragment;
    private static ListPreferences sharedPreference;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int mlayout;
        this.context = getActivity().getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        setHasOptionsMenu(true);

        View inflatedView = inflater.inflate(R.layout.fragment_vehicles_viewpager, container, false);

        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Launchers"));
        tabLayout.addTab(tabLayout.newTab().setText("Orbiters"));
        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        pagerAdapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                current_tab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (sharedPreference.isNightModeActive(context)){
            tabLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.darkPrimary));
            tabLayout.setTabTextColors(ContextCompat.getColor(context, R.color.dark_theme_secondary_text_color),
                    ContextCompat.getColor(context, R.color.dark_theme_primary_text_color));
        } else {
            tabLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            tabLayout.setTabTextColors(ContextCompat.getColor(context, R.color.dark_theme_secondary_text_color),
                    ContextCompat.getColor(context, R.color.dark_theme_primary_text_color));
        }

        return inflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Do your stuff
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    launchVehicleFragment = new LauncherFragment();
                    return launchVehicleFragment;
                case 1:
                    orbiterFragment = new OrbiterFragment();
                    return orbiterFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
