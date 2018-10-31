package me.calebjones.spacelaunchnow.ui.main.launches;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.ui.launches.agency.PreviousAgencyLaunchesFragment;
import me.calebjones.spacelaunchnow.ui.launches.agency.UpcomingAgencyLaunchesFragment;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import timber.log.Timber;

public class LaunchesViewPager extends Fragment {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private static ListPreferences sharedPreference;
    private UpcomingLaunchesFragment upcomingFragment;
    private PreviousLaunchesFragment previousFragment;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity().getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        View inflatedView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        tabLayout = inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.upcoming));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.previous));
        viewPager = inflatedView.findViewById(R.id.viewpager);

        pagerAdapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    ((MainActivity) getActivity()).setActionBarTitle(sharedPreference.getUpTitle());
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Do your stuff
    }



    @Override
    public void onResume() {
        Timber.d("onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.d("onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Timber.d("onStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Timber.d("onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.v("onDestroyView");
        upcomingFragment = null;
        previousFragment = null;
        viewPager.clearOnPageChangeListeners();
        tabLayout.setOnTabSelectedListener(null);
        pagerAdapter = null;
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
                case 0: return UpcomingLaunchesFragment.newInstance("Upcoming");
                case 1: return PreviousLaunchesFragment.newInstance("Previous");
                default:
                    return null;
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    upcomingFragment = (UpcomingLaunchesFragment) createdFragment;
                    break;
                case 1:
                    previousFragment = (PreviousLaunchesFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
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
