package me.spacelaunchnow.astronauts;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.spacelaunchnow.astronauts.list.AstronautListFragment;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class AstronautViewpagerFragment extends BaseFragment {

    private AstronautListViewModel mViewModel;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private Context context;
    private AstronautListFragment activeFragment;
    private AstronautListFragment retiredFragment;
    private AstronautListFragment lostFragment;

    public static AstronautViewpagerFragment newInstance() {
        return new AstronautViewpagerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)  {
        this.context = getActivity().getApplicationContext();

        View inflatedView = inflater.inflate(R.layout.astronaut_list_fragment, container, false);

        tabLayout = inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Active"));
        tabLayout.addTab(tabLayout.newTab().setText("Retired"));
        tabLayout.addTab(tabLayout.newTab().setText("Lost in Service"));
        viewPager = inflatedView.findViewById(R.id.viewpager);

        pagerAdapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AstronautListViewModel.class);
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
                    if (activeFragment != null) {
                        return activeFragment;
                    } else {
                        return AstronautListFragment.newInstance(new int[]{1});
                    }
                case 1:
                    if (retiredFragment != null) {
                        return retiredFragment;
                    } else {
                        return AstronautListFragment.newInstance(new int[]{2});
                    }
                case 2:
                    if (lostFragment != null) {
                        return lostFragment;
                    } else {
                        return AstronautListFragment.newInstance(new int[]{4, 5});
                    }
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
//                case 0:
//                    nextLaunchFragment = (NextLaunchFragment) createdFragment;
//                    break;
                case 0:
                    activeFragment = (AstronautListFragment) createdFragment;
                    break;
                case 1:
                    retiredFragment = (AstronautListFragment) createdFragment;
                    break;
                case 2:
                    lostFragment = (AstronautListFragment) createdFragment;
                    break;
            }
            return createdFragment;
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
