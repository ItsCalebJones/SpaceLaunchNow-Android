package me.calebjones.spacelaunchnow.astronauts.detail;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ShareCompat;
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
import butterknife.OnClick;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import me.calebjones.spacelaunchnow.astronauts.data.AstronautDataRepository;
import me.calebjones.spacelaunchnow.astronauts.data.Callbacks;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.base.BaseActivity;
import me.calebjones.spacelaunchnow.common.utils.CustomOnOffsetChangedListener;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.spacelaunchnow.astronauts.R;
import me.spacelaunchnow.astronauts.R2;
import timber.log.Timber;

public class AstronautDetailsActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R2.id.astronaut_profile_backdrop)
    ImageView astronautProfileBackdrop;
    @BindView(R2.id.astronaut_collapsing)
    CollapsingToolbarLayout astronautCollapsing;
    @BindView(R2.id.astronaut_profile_image)
    CircleImageView astronautProfileImage;
    @BindView(R2.id.astronaut_detail_toolbar)
    Toolbar toolbar;
    @BindView(R2.id.astronaut_title)
    TextView astronautTitle;
    @BindView(R2.id.astronaut_subtitle)
    TextView astronautSubtitle;
    @BindView(R2.id.astronaut_detail_tabs)
    TabLayout tabs;
    @BindView(R2.id.appbar)
    AppBarLayout appbar;
    @BindView(R2.id.astronaut_detail_viewpager)
    ViewPager viewPager;
    @BindView(R2.id.astronaut_adView)
    AdView astronautAdView;
    @BindView(R2.id.astronaut_stateful_view)
    SimpleStatefulLayout astronautStatefulView;
    @BindView(R2.id.astronaut_detail_swipe_refresh)
    SwipeRefreshLayout astronautDetailSwipeRefresh;
    @BindView(R2.id.astronaut_fab_share)
    FloatingActionButton astronautFabShare;
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
    private AstronautDataRepository astronautDataRepository;
    private AstronautDetailViewModel viewModel;
    private Astronaut astronaut;
    private int astronautId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astronaut_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        astronautDetailSwipeRefresh.setOnRefreshListener(this);
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
        tabs.addTab(tabs.newTab().setText("Profile"));
        tabs.addTab(tabs.newTab().setText("Flights"));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        astronautDataRepository = new AstronautDataRepository(this, getRealm());

        appbar.addOnOffsetChangedListener(new CustomOnOffsetChangedListener(getCyanea().getPrimaryDark(), getWindow()));
        appbar.addOnOffsetChangedListener(this);

        //Grab information from Intent
        Intent mIntent = getIntent();
        astronautId = mIntent.getIntExtra("astronautId", 0);

        fetchData(astronautId);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        viewModel = ViewModelProviders.of(this).get(AstronautDetailViewModel.class);
        // update UI
        viewModel.getAstronaut().observe(this, this::updateViews);
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (astronautDetailSwipeRefresh != null) {
            astronautDetailSwipeRefresh.setEnabled(enable);
        }
    }

    private void fetchData(int astronautId) {
        astronautDataRepository.getAstronautById(astronautId, new Callbacks.AstronautCallback() {
            @Override
            public void onAstronautLoaded(Astronaut astronaut) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    updateViewModel(astronaut);
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

    private void updateViews(Astronaut astronaut) {
        this.astronaut = astronaut;
        astronautTitle.setText(astronaut.getName());
        astronautSubtitle.setText(astronaut.getNationality());
        GlideApp.with(this)
                .load(astronaut.getProfileImage())
                .thumbnail(GlideApp.with(this)
                        .load(astronaut.getProfileImageThumbnail()))
                .placeholder(R.drawable.placeholder)
                .into(astronautProfileImage);
    }

    private void updateViewModel(Astronaut astronaut) {

        viewModel.getAstronaut().setValue(astronaut);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (mMaxScrollSize == 0) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        }

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            astronautProfileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(300)
                    .start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            astronautProfileImage.animate()
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
        astronautDetailSwipeRefresh.post(() -> astronautDetailSwipeRefresh.setRefreshing(true));
    }

    private void hideLoading() {
        Timber.v("Hide Loading...");
        astronautDetailSwipeRefresh.post(() -> astronautDetailSwipeRefresh.setRefreshing(false));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return true;
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

        if (id == R.id.info) {
            new MaterialDialog.Builder(this)
                    .title(me.calebjones.spacelaunchnow.common.R.string.improve_our_data)
                    .icon(new IconicsDrawable(this)
                            .icon(FontAwesome.Icon.faw_discord)
                            .color(Color.rgb(114, 137, 218))
                            .sizeDp(24))
                    .content(R.string.improve_our_data_content)
                    .negativeText(R.string.button_no)
                    .positiveText(R.string.ok)
                    .onNegative((dialog, which) -> dialog.dismiss())
                    .onPositive((dialog, which) -> {
                        String discordUrl = getString(R.string.discord_url);
                        Intent discordIntent = new Intent(Intent.ACTION_VIEW);
                        discordIntent.setData(Uri.parse(discordUrl));
                        startActivity(discordIntent);
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R2.id.astronaut_fab_share)
    void fabClicked(){
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(astronaut.getName())
                .setText(astronaut.getUrl())
                .startChooser();
    }

    @Override
    public void onRefresh() {
        fetchData(astronautId);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public AstronautProfileFragment profileFragment;
        public AstronautFlightsFragment flightsFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AstronautProfileFragment.newInstance();
                case 1:
                    return AstronautFlightsFragment.newInstance();
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    profileFragment = (AstronautProfileFragment) createdFragment;
                    break;
                case 1:
                    flightsFragment = (AstronautFlightsFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Profile";
                case 1:
                    return "Flights";
            }
            return "";
        }
    }
}
