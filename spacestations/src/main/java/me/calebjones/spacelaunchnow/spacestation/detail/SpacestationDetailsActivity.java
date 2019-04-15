package me.calebjones.spacelaunchnow.spacestation.detail;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.spacestation.R2;
import me.calebjones.spacelaunchnow.common.base.BaseActivity;
import me.calebjones.spacelaunchnow.common.utils.CustomOnOffsetChangedListener;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.spacestation.R;
import me.calebjones.spacelaunchnow.spacestation.data.Callbacks;
import me.calebjones.spacelaunchnow.spacestation.data.SpacestationDataRepository;
import me.calebjones.spacelaunchnow.spacestation.detail.fragments.SpacestationDockedVehiclesFragment;
import me.calebjones.spacelaunchnow.spacestation.detail.fragments.expeditions.SpacestationExpeditionFragment;
import me.calebjones.spacelaunchnow.spacestation.detail.fragments.detail.SpacestationDetailFragment;
import timber.log.Timber;

public class SpacestationDetailsActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R2.id.spacestation_profile_backdrop)
    ImageView spacestationProfileBackdrop;
    @BindView(R2.id.spacestation_collapsing)
    CollapsingToolbarLayout spacestationCollapsing;
    @BindView(R2.id.spacestation_profile_image)
    CircleImageView spacestationProfileImage;
    @BindView(R2.id.spacestation_detail_toolbar)
    Toolbar toolbar;
    @BindView(R2.id.spacestation_title)
    TextView spacestationTitle;
    @BindView(R2.id.spacestation_subtitle)
    TextView spacestationSubtitle;
    @BindView(R2.id.spacestation_detail_tabs)
    TabLayout tabs;
    @BindView(R2.id.appbar)
    AppBarLayout appbar;
    @BindView(R2.id.spacestation_detail_viewpager)
    ViewPager viewPager;
    @BindView(R2.id.spacestation_adView)
    AdView spacestationAdView;
    @BindView(R2.id.spacestation_stateful_view)
    SimpleStatefulLayout spacestationStatefulView;
    @BindView(R2.id.spacestation_detail_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.rootview)
    CoordinatorLayout rootview;

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SpacestationDataRepository spacestationDataRepository;
    private SpacestationDetailViewModel viewModel;
    private Spacestation spacestation;
    private int spacestationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spacestation_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        swipeRefreshLayout.setOnRefreshListener(this);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float v, int i1 ) {
            }

            @Override
            public void onPageSelected( int position ) {
            }

            @Override
            public void onPageScrollStateChanged( int state ) {
                enableDisableSwipeRefresh( state == ViewPager.SCROLL_STATE_IDLE );
            }
        } );
        tabs.addTab(tabs.newTab().setText(getString(R.string.details)));
        tabs.addTab(tabs.newTab().setText(getString(R.string.expeditions)));
        tabs.addTab(tabs.newTab().setText(getString(R.string.docked_alt)));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabs.setTabTextColors(Utils.getSecondaryTitleTextColor(getCyanea().getPrimary()),
                Utils.getTitleTextColor(getCyanea().getPrimary()));
        tabs.setBackgroundColor(getCyanea().getPrimary());
        spacestationDataRepository = new SpacestationDataRepository(this, getRealm());

        appbar.addOnOffsetChangedListener(new CustomOnOffsetChangedListener(getCyanea().getPrimaryDark(), getWindow()));
        appbar.addOnOffsetChangedListener(this);

        //Grab information from Intent
        Intent mIntent = getIntent();
        spacestationId = mIntent.getIntExtra("spacestationId", 0);

        fetchData(spacestationId);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        viewModel = ViewModelProviders.of(this).get(SpacestationDetailViewModel.class);
        // update UI
        viewModel.getSpacestation().observe(this, this::updateViews);
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enable);
        }
    }

    private void fetchData(int spacestationId) {
        spacestationDataRepository.getSpacestationById(spacestationId, new Callbacks.SpacestationCallback() {
            @Override
            public void onSpacestationLoaded(Spacestation spacestation) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    updateViewModel(spacestation);
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
                if (throwable != null) {
                    Timber.e(throwable);
                } else {
                    Timber.e(message);
                }
            }
        });

    }

    private void updateViews(Spacestation spacestation) {
        this.spacestation = spacestation;
        spacestationTitle.setText(spacestation.getName());
        spacestationSubtitle.setText(spacestation.getType().getName());
        spacestationSubtitle.setTextColor(Utils.getSecondaryTitleTextColor(getCyanea().getPrimary()));
        spacestationTitle.setTextColor(Utils.getTitleTextColor(getCyanea().getPrimary()));
        GlideApp.with(this)
                .load(spacestation.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(spacestationProfileImage);
    }

    private void updateViewModel(Spacestation spacestation) {

        viewModel.getSpacestation().setValue(spacestation);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (mMaxScrollSize == 0) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        }

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            spacestationProfileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(300)
                    .start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            spacestationProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    private void showNetworkLoading(boolean loading) {
        if (loading) {
            showLoading();
        } else {
            hideLoading();
        }
    }

    private void showLoading() {
        Timber.v("Show Loading...");
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
    }

    private void hideLoading() {
        Timber.v("Hide Loading...");
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        fetchData(spacestationId);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SpacestationDetailFragment detailFragment;
        public SpacestationExpeditionFragment expeditionFragment;
        public SpacestationDockedVehiclesFragment dockingEventFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SpacestationDetailFragment.newInstance();
                case 1:
                    return SpacestationExpeditionFragment.newInstance();
                case 2:
                    return SpacestationDockedVehiclesFragment.newInstance();
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    detailFragment = (SpacestationDetailFragment) createdFragment;
                    break;
                case 1:
                    expeditionFragment = (SpacestationExpeditionFragment) createdFragment;
                    break;
                case 2:
                    dockingEventFragment = (SpacestationDockedVehiclesFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Details";
                case 1:
                    return "Expeditions";
                case 2:
                    return "Docked Vehicles";
            }
            return "";
        }
    }
}
