package me.calebjones.spacelaunchnow.ui.main.vehicles;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.ui.main.vehicles.launcher.LauncherFragment;
import me.calebjones.spacelaunchnow.ui.main.vehicles.orbiter.OrbiterFragment;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;

public class VehiclesViewPager extends BaseFragment {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private int current_tab;
    private TabLayout tabLayout;
    private LauncherFragment launchVehicleFragment;
    private OrbiterFragment orbiterFragment;
    private static ListPreferences sharedPreference;
    private Context context;

    public static VehiclesViewPager newInstance() {

        VehiclesViewPager u = new VehiclesViewPager();
        Bundle b = new Bundle();
        u.setArguments(b);

        return u;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity().getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        setHasOptionsMenu(true);

        View inflatedView = inflater.inflate(R.layout.starship_fragment_view_pager, container, false);

        tabLayout = inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.launchers));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.orbiters));
        viewPager = inflatedView.findViewById(R.id.viewpager);

        pagerAdapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
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

        if(SupporterHelper.isSupporter()){
            menu.removeItem(R.id.action_supporter);
        }
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
