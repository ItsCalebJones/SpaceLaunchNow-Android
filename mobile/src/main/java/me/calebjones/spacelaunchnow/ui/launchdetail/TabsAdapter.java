package me.calebjones.spacelaunchnow.ui.launchdetail;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.launchdetail.fragments.AgencyDetailFragment;
import me.calebjones.spacelaunchnow.ui.launchdetail.fragments.MissionDetailFragment;
import me.calebjones.spacelaunchnow.ui.launchdetail.fragments.SummaryDetailFragment;

public class TabsAdapter extends FragmentPagerAdapter {

    private SummaryDetailFragment summaryFragment = SummaryDetailFragment.newInstance();
    private MissionDetailFragment missionFragment = MissionDetailFragment.newInstance();
    private AgencyDetailFragment agencyFragment = AgencyDetailFragment.newInstance();
    private Context context;

    public TabsAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return summaryFragment;
            case 1:
                return missionFragment;
            case 2:
                return agencyFragment;
        }
        return null;
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
