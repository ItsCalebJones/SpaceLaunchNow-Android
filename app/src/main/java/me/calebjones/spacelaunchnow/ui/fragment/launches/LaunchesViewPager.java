package me.calebjones.spacelaunchnow.ui.fragment.launches;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import timber.log.Timber;

public class LaunchesViewPager extends Fragment {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private int current_tab;
    private TabLayout tabLayout;
    private UpcomingLaunchesFragment launchesFragment;
    private PreviousLaunchesFragment previousLaunchesFragment;
    private static SharedPreference sharedPreference;
    private Context context;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int mlayout;
        this.context = getActivity();


        sharedPreference = SharedPreference.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            mlayout = R.layout.dark_fragment_launches_view_pager;
        } else {
            mlayout = R.layout.fragment_launches_view_pager;
        }

        View inflatedView = inflater.inflate(mlayout, container, false);

        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Previous"));
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
                if (tab.getPosition() == 0){
                    ((MainActivity) getActivity()).setActionBarTitle("Space Launch Now");
                } else {
                    ((MainActivity) getActivity()).setActionBarTitle(sharedPreference.getPreviousTitle());
                }
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

    public void restartViews(String action) {
        Timber.d("Restarting view: %s", action);
        if (pagerAdapter != null){
            if (Strings.ACTION_SUCCESS_UP_LAUNCHES.equals(action) ) {
                pagerAdapter.notifyDataSetChanged();
            } else if (Strings.ACTION_SUCCESS_PREV_LAUNCHES.equals(action)) {
                pagerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Do your stuff
    }

    @Override
    public void onPause(){
        Timber.d("onPause");
        super.onPause();
    }

    @Override
    public void onStop(){
        Timber.d("onStop");
        super.onStop();
    }

    @Override
    public void onDetach(){
        Timber.d("onDetach");
        super.onDetach();
    }


    public class PagerAdapter extends FragmentPagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    launchesFragment = new UpcomingLaunchesFragment();
                    return launchesFragment;
                case 1:
                    previousLaunchesFragment = new PreviousLaunchesFragment();
                    return previousLaunchesFragment;
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
