package me.calebjones.spacelaunchnow.ui.main.news;


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

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.ui.main.launches.PreviousLaunchesFragment;
import me.calebjones.spacelaunchnow.ui.main.launches.UpcomingLaunchesFragment;
import me.calebjones.spacelaunchnow.ui.main.news.twitter.TwitterFragment;
import me.calebjones.spacelaunchnow.ui.main.news.web.WebNewsFragment;
import timber.log.Timber;

public class NewsViewPager extends Fragment {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private static ListPreferences sharedPreference;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity().getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        View inflatedView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        tabLayout = inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.news));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.twitter));
        viewPager = inflatedView.findViewById(R.id.viewpager);

        pagerAdapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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


    public class PagerAdapter extends FragmentPagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: return new WebNewsFragment();
                case 1: return new TwitterFragment();
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
