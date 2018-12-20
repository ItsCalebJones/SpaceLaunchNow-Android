package me.calebjones.spacelaunchnow.ui.launchdetail;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.ui.launchdetail.fragments.AgencyDetailFragment;
import me.calebjones.spacelaunchnow.ui.launchdetail.fragments.mission.MissionDetailFragment;
import me.calebjones.spacelaunchnow.ui.launchdetail.fragments.SummaryDetailFragment;

public class TabsAdapter extends FragmentStatePagerAdapter {

    public SummaryDetailFragment summaryFragment;
    public MissionDetailFragment missionFragment;
    public AgencyDetailFragment agencyFragment;
    private Context context;

    public TabsAdapter(LaunchDetailActivity activity) {
        super(activity.getSupportFragmentManager());
        this.context = activity;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SummaryDetailFragment.newInstance();
            case 1:
                return MissionDetailFragment.newInstance();
            case 2:
                return AgencyDetailFragment.newInstance();
        }
        return null;
    }

    // Here we can finally safely save a reference to the created
    // Fragment, no matter where it came from (either getItem() or
    // FragmentManger). Simply save the returned Fragment from
    // super.instantiateItem() into an appropriate reference depending
    // on the ViewPager position.
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // save the appropriate reference depending on position
        switch (position) {
            case 0:
                summaryFragment = (SummaryDetailFragment) createdFragment;
                break;
            case 1:
                missionFragment = (MissionDetailFragment) createdFragment;
                break;
            case 2:
                agencyFragment = (AgencyDetailFragment) createdFragment;
                break;
        }
        return createdFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.details);
            case 1:
                return context.getString(R.string.mission);
            case 2:
                return context.getString(R.string.agencies);
        }
        return "";
    }
}
