package me.calebjones.spacelaunchnow.events.detail;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.base.BaseActivity;
import me.calebjones.spacelaunchnow.common.content.LaunchStatusUtil;
import me.calebjones.spacelaunchnow.common.ui.adapters.ExpeditionAdapter;
import me.calebjones.spacelaunchnow.common.ui.adapters.ListAdapter;
import me.calebjones.spacelaunchnow.common.ui.adapters.SpacestationAdapter;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.common.ui.views.custom.CountDownView;
import me.calebjones.spacelaunchnow.common.utils.CustomOnOffsetChangedListener;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;
import me.calebjones.spacelaunchnow.events.R;
import me.calebjones.spacelaunchnow.events.R2;
import me.calebjones.spacelaunchnow.events.data.Callbacks;
import me.calebjones.spacelaunchnow.events.data.EventDataRepository;
import timber.log.Timber;

public class EventDetailsActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R2.id.event_profile_backdrop)
    ImageView eventProfileBackdrop;
    @BindView(R2.id.event_collapsing)
    CollapsingToolbarLayout eventCollapsing;
    @BindView(R2.id.event_profile_image)
    CircleImageView eventProfileImage;
    @BindView(R2.id.event_detail_toolbar)
    Toolbar toolbar;
    @BindView(R2.id.event_title)
    TextView eventTitle;
    @BindView(R2.id.event_subtitle)
    TextView eventSubtitle;
    @BindView(R2.id.appbar)
    AppBarLayout appbar;
    @BindView(R2.id.event_adView)
    AdView eventAdView;

    @BindView(R2.id.event_stateful_view)
    SimpleStatefulLayout eventStatefulView;
    @BindView(R2.id.event_detail_swipe_refresh)
    SwipeRefreshLayout eventDetailSwipeRefresh;
    @BindView(R2.id.event_fab_share)
    FloatingActionButton eventFabShare;
    @BindView(R2.id.rootview)
    CoordinatorLayout rootview;
    @BindView(R2.id.event_card_title)
    TextView eventCardTitle;
    @BindView(R2.id.event_type)
    TextView eventType;
    @BindView(R2.id.event_date)
    TextView eventDate;
    @BindView(R2.id.event_description)
    TextView eventDescription;
    @BindView(R2.id.event_launch_card_title)
    TextView launchCardTitle;
    @BindView(R2.id.event_launch_card_subtitle)
    TextView launchCardSubTitle;
    @BindView(R2.id.launch_recycler_view)
    RecyclerView launchRecyclerView;
    @BindView(R2.id.launchCardRootView)
    MaterialCardView launchCard;
    @BindView(R2.id.expedition_recycler_view)
    RecyclerView expeditionRecyclerView;
    @BindView(R2.id.expeditionCardRootView)
    CoordinatorLayout expeditionView;
    @BindView(R2.id.spacestationCardRootView)
    MaterialCardView spacestationCard;
    @BindView(R2.id.event_spacestation_card_title)
    TextView eventSpacestationCardTitle;
    @BindView(R2.id.event_spacestation_card_subtitle)
    TextView eventSpacestationCardSubTitle;
    @BindView(R2.id.spacestation_recycler_view)
    RecyclerView spacestationRecyclerView;
    @BindView(R2.id.event_web_button)
    AppCompatButton eventWebButton;
    @BindView(R2.id.event_watch_button)
    AppCompatButton eventWatchButton;



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
    private EventDataRepository eventDataRepository;
    private EventDetailViewModel viewModel;
    private Event event;
    private int eventId;
    private SimpleDateFormat sdf;
    private int color;
    private LinearLayoutManager linearLayoutManager;
    private ListAdapter adapter;
    private ExpeditionAdapter expeditionAdapter;
    private SpacestationAdapter spacestationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        eventDetailSwipeRefresh.setOnRefreshListener(this);
        eventDataRepository = new EventDataRepository(this, getRealm());

        appbar.addOnOffsetChangedListener(new CustomOnOffsetChangedListener(getCyanea().getPrimaryDark(), getWindow()));
        appbar.addOnOffsetChangedListener(this);

        //Grab information from Intent
        Intent mIntent = getIntent();
        eventId = mIntent.getIntExtra("eventId", 0);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("local_time", true)) {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
        } else {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy zzz");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        if (getCyanea().isDark()){
            color = ContextCompat.getColor(this, R.color.material_color_white);
        } else {
            color = ContextCompat.getColor(this, R.color.material_color_black);
        }

        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new ListAdapter(this, getCyanea().isDark());
        launchRecyclerView.setLayoutManager(linearLayoutManager);
        launchRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        launchRecyclerView.setAdapter(adapter);

        expeditionAdapter = new ExpeditionAdapter(this, getCyanea().isDark());
        expeditionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expeditionRecyclerView.setAdapter(expeditionAdapter);

        spacestationAdapter = new SpacestationAdapter(this);
        spacestationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        spacestationRecyclerView.setAdapter(spacestationAdapter);

        viewModel = ViewModelProviders.of(this).get(EventDetailViewModel.class);
        // update UI
        viewModel.getEvent().observe(this, this::updateViews);
        fetchData(eventId);

        if (!SupporterHelper.isSupporter()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            eventAdView.loadAd(adRequest);
            eventAdView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    eventAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    eventAdView.setVisibility(View.GONE);
                }

            });
        } else {
            eventAdView.setVisibility(View.GONE);
        }
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (eventDetailSwipeRefresh != null) {
            eventDetailSwipeRefresh.setEnabled(enable);
        }
    }

    private void fetchData(int eventId) {

        eventDataRepository.getEventById(eventId, new Callbacks.EventCallback() {
            @Override
            public void onEventLoaded(Event event) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    updateViewModel(event);
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

    private void updateViews(Event event) {
        this.event = event;
        eventTitle.setText(event.getName());
        eventTitle.setTextColor(Utils.getTitleTextColor(getCyanea().getPrimary()));

        eventSubtitle.setText(event.getLocation());
        eventSubtitle.setTextColor(Utils.getSecondaryTitleTextColor(getCyanea().getPrimary()));

        eventCardTitle.setText("Overview");
        eventType.setText(event.getType().getName());
        SimpleDateFormat df = Utils.getSimpleDateFormatForUI("EEEE, MMMM dd, yyyy - hh:mm a zzz");
        df.toLocalizedPattern();
        eventDate.setText(df.format(event.getDate()));
        eventDescription.setText(event.getDescription());
        GlideApp.with(this)
                .load(event.getFeatureImage())
                .placeholder(R.drawable.placeholder)
                .into(eventProfileImage);

        if (event.getLaunches() != null && event.getLaunches().size() > 0){
            launchCard.setVisibility(View.VISIBLE);
            adapter.addItems(event.getLaunches());
        } else {
            launchCard.setVisibility(View.GONE);
        }

        if (event.getNewsUrl() != null){
            eventWebButton.setVisibility(View.VISIBLE);
        } else {
            eventWebButton.setVisibility(View.GONE);
        }

        if (event.getVideoUrl() != null){
            eventWatchButton.setVisibility(View.VISIBLE);
        } else {
            eventWatchButton.setVisibility(View.GONE);
        }

        if (event.getExpeditions() != null && event.getExpeditions().size() > 0){
            expeditionView.setVisibility(View.VISIBLE);
            expeditionAdapter.addItems(event.getExpeditions());
        } else {
            expeditionView.setVisibility(View.GONE);
        }

        if (event.getSpacestations() != null && event.getSpacestations().size() > 0){
            spacestationCard.setVisibility(View.VISIBLE);
            spacestationAdapter.addItems(event.getSpacestations());
        } else {
            spacestationCard.setVisibility(View.GONE);
        }
    }

    private void updateViewModel(Event event) {
        adapter.clear();
        expeditionAdapter.clear();
        spacestationAdapter.clear();
        viewModel.getEvent().setValue(event);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (mMaxScrollSize == 0) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        }

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            eventProfileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(300)
                    .start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            eventProfileImage.animate()
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
        eventDetailSwipeRefresh.post(() -> eventDetailSwipeRefresh.setRefreshing(true));
    }

    private void hideLoading() {
        Timber.v("Hide Loading...");
        eventDetailSwipeRefresh.post(() -> eventDetailSwipeRefresh.setRefreshing(false));
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
                    .title(R.string.improve_our_data)
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

    @OnClick(R2.id.event_fab_share)
    void fabClicked(){
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(event.getName())
                .setText(event.getNewsUrl())
                .startChooser();
    }

    @OnClick(R2.id.event_watch_button)
    void watchClicked(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(event.getVideoUrl()));
        startActivity(i);
    }

    @OnClick(R2.id.event_web_button)
    void webClicked(){
        Utils.openCustomTab(this, getApplicationContext(), event.getNewsUrl());
    }

    @Override
    public void onRefresh() {
        fetchData(eventId);
    }
}
