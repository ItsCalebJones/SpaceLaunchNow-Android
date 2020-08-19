package me.calebjones.spacelaunchnow.starship.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;
import me.calebjones.spacelaunchnow.starship.StarshipListViewModel;
import me.calebjones.spacelaunchnow.starship.data.Callbacks;
import me.calebjones.spacelaunchnow.starship.data.StarshipDataRepository;
import me.spacelaunchnow.starship.R;
import me.spacelaunchnow.starship.R2;
import timber.log.Timber;

public class StarshipViewPager extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R2.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R2.id.viewpager)
    ViewPager viewPager;
    @BindView(R2.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private PagerAdapter pagerAdapter;
    private Context context;
    private StarshipListViewModel model;
    private StarshipDataRepository dataRepository;
    private Unbinder unbinder;


    public static StarshipViewPager newInstance() {

        StarshipViewPager u = new StarshipViewPager();
        Bundle b = new Bundle();
        u.setArguments(b);

        return u;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataRepository = new StarshipDataRepository(getContext(), getRealm());
        setScreenName("Starship Dashboard Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity();


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("newsUrl")) {
                Timber.v("Received bundle.");
                String url = bundle.getString("newsUrl");
                new Handler().postDelayed(() -> Utils.openCustomTab(context, url), 500);
                getArguments().remove("newsUrl");
            }
        }

        View inflatedView = inflater.inflate(R.layout.starship_fragment_view_pager, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Up Next"));
        tabLayout.addTab(tabLayout.newTab().setText("News"));

        pagerAdapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());

        swipeRefresh.setOnRefreshListener(this);

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

        model = ViewModelProviders.of(this).get(StarshipListViewModel.class);
        fetchData();
        return inflatedView;
    }

    private void fetchData() {
        Timber.v("fetchData - getting Starship Dashboard");

        dataRepository.getStarshipDashboard(new Callbacks.StarshipCallback() {

            @Override
            public void onStarshipDashboardLoaded(Starship starship) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    updateViewModel(starship);
                }
            }

            @Override
            public void onNetworkStateChanged(boolean refreshing) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    showNetworkLoading(refreshing);
                }
            }

            @Override
            public void onError(String message, @Nullable Throwable throwable) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    if (throwable != null) {
                        Timber.e(throwable);
                    } else {
                        Timber.e(message);
                    }
                    showNetworkLoading(false);
                }
            }
        });
    }

    private void showNetworkLoading(boolean refreshing) {
        swipeRefresh.setRefreshing(refreshing);
    }

    private void updateViewModel(Starship starship) {
        model.getStarshipDashboard().setValue(starship);
    }

    @Override
    public void onRefresh() {
        fetchData();
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
                    return new StarshipDashboardFragment();
                case 1:
                    return new StarshipDashboardFragment();
                case 2:
                    return new StarshipDashboardFragment();
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
