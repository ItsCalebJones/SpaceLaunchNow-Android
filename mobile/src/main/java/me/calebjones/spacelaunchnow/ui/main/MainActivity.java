package me.calebjones.spacelaunchnow.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
import com.michaelflisar.gdprdialog.GDPRConsentState;
import com.michaelflisar.gdprdialog.GDPRDefinitions;
import com.michaelflisar.gdprdialog.GDPRLocation;
import com.michaelflisar.gdprdialog.GDPRNetwork;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mrapp.android.preference.activity.PreferenceActivity;
import io.fabric.sdk.android.Fabric;
import jonathanfinerty.once.Amount;
import jonathanfinerty.once.Once;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.common.customviews.generate.OnFeedbackListener;
import me.calebjones.spacelaunchnow.common.customviews.generate.Rate;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.events.FilterViewEvent;
import me.calebjones.spacelaunchnow.ui.changelog.ChangelogActivity;
import me.calebjones.spacelaunchnow.ui.intro.OnboardingActivity;
import me.calebjones.spacelaunchnow.ui.main.launches.LaunchesViewPager;
import me.calebjones.spacelaunchnow.ui.main.news.NewsViewPager;
import me.calebjones.spacelaunchnow.ui.main.next.NextLaunchFragment;
import me.calebjones.spacelaunchnow.ui.main.vehicles.VehiclesViewPager;
import me.calebjones.spacelaunchnow.ui.settings.AboutActivity;
import me.calebjones.spacelaunchnow.ui.settings.SettingsActivity;
import me.calebjones.spacelaunchnow.ui.settings.fragments.AppearanceFragment;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterActivity;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements GDPR.IGDPRCallback {

    private static final String NAV_ITEM_ID = "navItemId";
    private static ListPreferences listPreferences;
    @BindView(R.id.adView)
    AdView adView;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    private LaunchesViewPager mlaunchesViewPager;
    private NextLaunchFragment mUpcomingFragment;
    private NewsViewPager mNewsViewpagerFragment;
    private VehiclesViewPager mVehicleViewPager;
    private Toolbar toolbar;
    private Drawer result = null;
    private SharedPreferences sharedPref;
    private SwitchPreferences switchPreferences;
    private CustomTabActivityHelper customTabActivityHelper;
    private Context context;
    private boolean adviewEnabled = false;
    private Rate rate;

    static final int SHOW_INTRO = 1;

    private int mNavItemId;
    private Snackbar snackbar;
    public int statusColor;

    public void mayLaunchUrl(Uri parse) {
        if (customTabActivityHelper.mayLaunchUrl(parse, null, null)) {
            Timber.v("mayLaunchURL Accepted - %s", parse.toString());
        } else {
            Timber.v("mayLaunchURL Denied - %s", parse.toString());
        }
    }

    public MainActivity() {
        super("Main Activity");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        if (!Fabric.isInitialized()) {
            Fabric.with(this, new Crashlytics());
        }
        if (!Once.beenDone(Once.THIS_APP_INSTALL, "showTutorial")) {
            startActivityForResult(new Intent(this, OnboardingActivity.class), SHOW_INTRO);
        }

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();

        if ("me.calebjones.spacelaunchnow.NIGHTMODE".equals(action)) {
            Intent sendIntent = new Intent(this, SettingsActivity.class);
            sendIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                    AppearanceFragment.class.getName());
            startActivity(sendIntent);
        }

        Timber.d("Creating Preference instances.");
        listPreferences = ListPreferences.getInstance(this.context);
        switchPreferences = SwitchPreferences.getInstance(this.context);

        int m_theme;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.context = getApplicationContext();
        customTabActivityHelper = new CustomTabActivityHelper();

        Timber.d("Checking if night mode active.");
        if (listPreferences.isNightModeActive(this)) {
            Timber.d("Night mode is active.");
            switchPreferences.setNightModeStatus(true);
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
        } else {
            Timber.d("Night mode is not active.");
            switchPreferences.setNightModeStatus(false);
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }
        m_theme = R.style.LightTheme_NoActionBar;

        Timber.d("Checking if theme changed.");
        if (getSharedPreferences("theme_changed", 0).getBoolean("recreate", false)) {
            SharedPreferences.Editor editor = getSharedPreferences("theme_changed", 0).edit();
            editor.putBoolean("recreate", false);
            editor.apply();
            recreate();
        }

        Timber.d("Setting theme.");
        setTheme(m_theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.d("Binding views.");
        ButterKnife.bind(this);

        adviewEnabled = false;
        hideAd();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.menu_next_launch;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        Timber.d("Building account header.");
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withHeaderBackground(new ImageHolder(getString(R.string.header_image)))
                .withSavedInstance(savedInstanceState)
                .build();

        Timber.d("Building rate builder.");
        rate = new Rate.Builder(context)
                .setTriggerCount(10)
                .setMinimumInstallTime(TimeUnit.DAYS.toMillis(3))
                .setMessage(R.string.please_rate_short)
                .setFeedbackAction(new OnFeedbackListener() {
                    @Override
                    public void onFeedbackTapped() {
                        showFeedback();
                    }
                })
                .setSnackBarParent(coordinatorLayout)
                .build();


        Timber.d("Building DrawerBuilder");
        result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home)
                                .withIcon(GoogleMaterial.Icon.gmd_home)
                                .withIdentifier(R.id.menu_next_launch)
                                .withSelectable(true),
                        new PrimaryDrawerItem().withName(R.string.launches)
                                .withIcon(GoogleMaterial.Icon.gmd_assignment)
                                .withIdentifier(R.id.menu_launches)
                                .withSelectable(true),
//                        new PrimaryDrawerItem().withName(R.string.missions)
//                                .withIcon(GoogleMaterial.Icon.gmd_satellite)
//                                .withIdentifier(R.id.menu_missions)
//                                .withSelectable(true),
                        new PrimaryDrawerItem().withName(R.string.news)
                                .withIcon(CommunityMaterial.Icon.cmd_newspaper)
                                .withIdentifier(R.id.menu_news)
                                .withSelectable(true),
                        new PrimaryDrawerItem().withName(R.string.vehicles)
                                .withIcon(FontAwesome.Icon.faw_rocket)
                                .withIdentifier(R.id.menu_vehicle)
                                .withSelectable(true),
                        new PrimaryDrawerItem()
                                .withIcon(CommunityMaterial.Icon.cmd_discord)
                                .withName(R.string.discord)
                                .withIdentifier(R.id.menu_discord)
                                .withSelectable(false),
//                        new DividerDrawerItem(),
//                        new ExpandableDrawerItem().withName(R.string.stay_connected).withIcon(CommunityMaterial.Icon.cmd_account).withDescription(R.string.connect_description).withIdentifier(19).withSelectable(false).withSubItems(
//                                new SecondaryDrawerItem()
//                                        .withIcon(CommunityMaterial.Icon.cmd_discord)
//                                        .withLevel(2)
//                                        .withName(R.string.discord)
//                                        .withDescription(R.string.discord_subtitle)
//                                        .withIdentifier(R.id.menu_discord)
//                                        .withSelectable(false),
//                                new SecondaryDrawerItem()
//                                        .withIcon(CommunityMaterial.Icon.cmd_twitter)
//                                        .withLevel(2)
//                                        .withName(R.string.twitter)
//                                        .withDescription(R.string.twitter_subtitle)
//                                        .withIdentifier(R.id.menu_twitter)
//                                        .withSelectable(false),
//                                new SecondaryDrawerItem()
//                                        .withIcon(CommunityMaterial.Icon.cmd_facebook)
//                                        .withLevel(2)
//                                        .withName(R.string.facebook)
//                                        .withDescription(R.string.facebook_subtitle)
//                                        .withIdentifier(R.id.menu_facebook)
//                                        .withSelectable(false),
//                                new SecondaryDrawerItem()
//                                        .withIcon(CommunityMaterial.Icon.cmd_web)
//                                        .withLevel(2)
//                                        .withName(R.string.website)
//                                        .withDescription(R.string.website_subtitle)
//                                        .withIdentifier(R.id.menu_website)
//                                        .withSelectable(false)
//                        ),
                        new DividerDrawerItem(),
                        new ExpandableDrawerItem().withName(R.string.get_help).withIcon(GoogleMaterial.Icon.gmd_account_box).withDescription(R.string.help_description).withIdentifier(20).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem()
                                        .withIcon(GoogleMaterial.Icon.gmd_info_outline)
                                        .withName(R.string.whats_new)
                                        .withDescription(R.string.whats_new_subtitle)
                                        .withIdentifier(R.id.menu_new)
                                        .withSelectable(false),
                                new SecondaryDrawerItem()
                                        .withIcon(GoogleMaterial.Icon.gmd_account_box)
                                        .withName(R.string.about).withDescription(R.string.about_subtitle)
                                        .withIdentifier(R.id.about)
                                        .withSelectable(false),
                                new SecondaryDrawerItem()
                                        .withIcon(GoogleMaterial.Icon.gmd_feedback)
                                        .withName(R.string.feedback)
                                        .withDescription(R.string.feedback_subtitle)
                                        .withIdentifier(R.id.menu_feedback)
                                        .withSelectable(false)
                        ),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.settings)
                                .withIcon(GoogleMaterial.Icon.gmd_settings)
                                .withIdentifier(R.id.menu_settings)
                                .withSelectable(true)
                ).withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem != null) {
                        navigate((int) drawerItem.getIdentifier());
                    }
                    return false;
                }).build();

        Timber.d("If not Supporter add footer else thank the user!");
        if (!SupporterHelper.isSupporter()) {
            FirebaseAnalytics.getInstance(this).setUserProperty("supporter", "false");
            Timber.d("Adding footer.");
            result.addStickyFooterItem(
                    new PrimaryDrawerItem().withName(R.string.supporter_title)
                            .withDescription(R.string.supporter_main)
                            .withIcon(FontAwesome.Icon.faw_dollar_sign)
                            .withIdentifier(R.id.menu_support)
                            .withSelectable(false));
        }

        if (SupporterHelper.isSupporter()) {
            FirebaseAnalytics.getInstance(this).setUserProperty("supporter", "true");
            Timber.d("Show thanks for support.");
            result.addStickyFooterItem(
                    new PrimaryDrawerItem().withName(R.string.thank_you_for_support)
                            .withIcon(GoogleMaterial.Icon.gmd_mood)
                            .withIdentifier(R.id.menu_support)
                            .withSelectable(false));
        }


        if ("SHOW_FILTERS".equals(action)) {
            navigate(R.id.menu_next_launch);
        } else {
            Timber.d("Navigate to initial fragment.");
            navigate(mNavItemId);
        }

    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
            getWindow().setReturnTransition(slide);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.v("MainActivity onStart!");
        customTabActivityHelper.bindCustomTabsService(this);
        mayLaunchUrl(Uri.parse("https://launchlibrary.net/"));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Timber.v("MainActivity onStop!");
        customTabActivityHelper.unbindCustomTabsService(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void showWhatsNew() {
        Intent whatsNew = new Intent(this, ChangelogActivity.class);
        startActivity(whatsNew);
    }

    public void onResume() {
        super.onResume();
        Timber.v("onResume");
        if (rate != null) {
            rate.count();
        }
        if (BuildConfig.DEBUG) {
            showRemainingCount();
        }
        if (!rate.isShown()) {
            if (!Once.beenDone(Once.THIS_APP_VERSION, "showChangelog")) {

                Once.markDone("showChangelog");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showChangelogSnackbar();
                    }
                }, 1000);

            }
            if (!SupporterHelper.isSupporter()) {
                if (!Once.beenDone("userCheckedSupporter")) {
                    if (Once.beenDone("appOpen", Amount.exactly(3))) {
                        if (!Once.beenDone("showRemoveAdThree") && !SupporterHelper.isSupporter()) {
                            Once.markDone("showRemoveAdThree");
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> showRemoveAd(), 5000);
                        }
                    } else if (Once.beenDone("appOpen", Amount.moreThan(1))) {
                        if (!Once.beenDone("showDiscord") && !Once.beenDone("discordResponse")) {
                            Once.markDone("showDiscord");
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> showDiscord(), 1000);
                        }
                    } else if (Once.beenDone("appOpen", Amount.moreThan(7))
                            && Once.beenDone("appOpen", Amount.lessThan(13))) {
                        if (!Once.beenDone("showRemoveAdSeven") && !SupporterHelper.isSupporter()) {
                            Once.markDone("showRemoveAdSeven");
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> showRemoveAd(), 5000);
                        }
                    } else if (Once.beenDone("appOpen", Amount.exactly(14))) {
                        if (!Once.beenDone("showRemoveAd14") && !SupporterHelper.isSupporter()) {
                            Once.markDone("showRemoveAd14");
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> showRemoveAd(), 5000);
                        }
                    }
                }
            }
        }
        if (getSharedPreferences("theme_changed", 0).getBoolean("recreate", false)) {
            SharedPreferences.Editor editor = getSharedPreferences("theme_changed", 0).edit();
            editor.putBoolean("recreate", false);
            editor.apply();
            recreate();
        }

        if (listPreferences.isNightModeActive(this)) {
            switchPreferences.setNightModeStatus(true);
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
        } else {
            switchPreferences.setNightModeStatus(false);
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }
        // show GDPR Dialog if necessary, the library takes care about if and how to show it
        GDPR.getInstance().checkIfNeedsToBeShown(this, getGDPRSetup());
        configureAdState(GDPR.getInstance().getConsentState());
    }

    private void showRemoveAd() {
        snackbar = Snackbar
                .make(coordinatorLayout, R.string.upgrade_pro, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setAction("Yes", view -> {
                    Once.markDone("userCheckedSupporter");
                    startActivity(new Intent(context, SupporterActivity.class));
                });
        snackbar.show();
    }

    private void showDiscord() {
        new MaterialDialog.Builder(this)
                .title("Official Discord Server")
                .icon(new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_discord)
                        .color(Color.rgb(114,137,218))
                        .sizeDp(24))
                .content("Join us on the Space Launch Now Discord server for live launch events and to share the latest news with spaceflight enthusiast!")
                .negativeText("No thanks!")
                .positiveText("Okay")
                .onNegative((dialog, which) -> {
                    Once.markDone("discordResponse");
                    dialog.dismiss();
                })
                .onPositive((dialog, which) -> {
                    Once.markDone("discordResponse");
                    String discordUrl = getString(R.string.discord_url);
                    Intent discordIntent = new Intent(Intent.ACTION_VIEW);
                    discordIntent.setData(Uri.parse(discordUrl));
                    startActivity(discordIntent);
                })
                .show();
    }

    private void showChangelogSnackbar() {
        snackbar = Snackbar
                .make(coordinatorLayout, getString(R.string.updated_version) + " " + Utils.getVersionName(context), Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAction("Changelog", view -> showWhatsNew());
        snackbar.show();

    }

    @SuppressLint("ShowToast")
    private synchronized void showRemainingCount() {
        int count = (int) rate.getRemainingCount();
        String message = String.format("%s more times until rate pop-up", count);
        Toast mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.setText(message);
        if (BuildConfig.DEBUG) {
            mToast.show();
        }
    }

    public void setActionBarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            } else {
                if (sharedPref.getBoolean("confirm_exit", false)) {
                    new MaterialDialog.Builder(this)
                            .title(R.string.confirm_exit)
                            .negativeText(R.string.cancel)
                            .positiveText(R.string.exit)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                            .show();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (BuildConfig.DEBUG) {
            getMenuInflater().inflate(R.menu.debug_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }
        if (SupporterHelper.isSupporter()) {
            menu.findItem(R.id.action_supporter).setVisible(false);
            menu.removeItem(R.id.action_supporter);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_consent) {
            showGDPRIfNecessary(true, GDPRLocation.IN_EAA_OR_UNKNOWN);
        }

        if (id == R.id.action_supporter) {
            Intent intent = new Intent(this, SupporterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void navigate(final int itemId) {
        Timber.v("Navigate to %s", itemId);
        // perform the actual navigation logic, updating the main_menu content fragment etc
        FragmentManager fm = getSupportFragmentManager();
        switch (itemId) {
            case R.id.menu_next_launch:
                mNavItemId = R.id.menu_next_launch;
                // Check to see if we have retained the worker fragment.
                mUpcomingFragment = (NextLaunchFragment) fm.findFragmentByTag("NEXT_LAUNCH");


                // If not retained (or first time running), we need to create it.
                if (mUpcomingFragment == null) {
                    mUpcomingFragment = new NextLaunchFragment();
                    if ("SHOW_FILTERS".equals(getIntent().getAction())) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("SHOW_FILTERS", true);
                        mUpcomingFragment.setArguments(bundle);
                    }
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent, mUpcomingFragment, "NEXT_LAUNCH").commit();
                }
                break;
            case R.id.menu_launches:
                mNavItemId = R.id.menu_launches;
                // Check to see if we have retained the worker fragment.
                mlaunchesViewPager = (LaunchesViewPager) fm.findFragmentByTag("LAUNCH_VIEWPAGER");

                // If not retained (or first time running), we need to create it.
                if (mlaunchesViewPager == null) {
                    mlaunchesViewPager = new LaunchesViewPager();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent, mlaunchesViewPager, "LAUNCH_VIEWPAGER").commit();
                }
                if (rate != null) {
                    rate.showRequest();
                }

                break;
            case R.id.menu_news:
                mNavItemId = R.id.menu_news;
                setActionBarTitle(getString(R.string.space_launch_news));
                // Check to see if we have retained the worker fragment.
                mNewsViewpagerFragment = (NewsViewPager) fm.findFragmentByTag("NEWS_FRAGMENT_VIEWPAGER");

                // If not retained (or first time running), we need to create it.
                if (mNewsViewpagerFragment == null) {
                    mNewsViewpagerFragment = new NewsViewPager();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent, mNewsViewpagerFragment, "NEWS_FRAGMENT").commit();
                }
                if (rate != null) {
                    rate.showRequest();
                }

                break;
            case R.id.menu_vehicle:
                mNavItemId = R.id.menu_vehicle;
                setActionBarTitle(getString(R.string.vehicles));
                // Check to see if we have retained the worker fragment.
                mVehicleViewPager = (VehiclesViewPager) fm.findFragmentByTag("VEHICLE_VIEWPAGER");

                // If not retained (or first time running), we need to create it.
                if (mVehicleViewPager == null) {
                    mVehicleViewPager = new VehiclesViewPager();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent, mVehicleViewPager, "VEHICLE_VIEWPAGER").commit();
                }
                if (rate != null) {
                    rate.showRequest();
                }

                break;
            case R.id.menu_launch:
                Utils.openCustomTab(this, getApplicationContext(), "https://launchlibrary.net/");
                break;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsIntent);
                break;
            case R.id.menu_new:
                showWhatsNew();
                break;
            case R.id.menu_support:
                Intent supportIntent = new Intent(this, SupporterActivity.class);
                startActivity(supportIntent);
                break;
            case R.id.menu_feedback:
                showFeedback();
                break;
            case R.id.menu_twitter:
                String url = getString(R.string.twitter_url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.menu_discord:
                String discordUrl = getString(R.string.discord_url);
                Intent discordIntent = new Intent(Intent.ACTION_VIEW);
                discordIntent.setData(Uri.parse(discordUrl));
                startActivity(discordIntent);
                break;
            case R.id.menu_facebook:
                String facebookUrl = getString(R.string.facebook_url);
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
                break;
            case R.id.menu_website:
                String websiteUrl = getString(R.string.spacelaunchnow_web);
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                websiteIntent.setData(Uri.parse(websiteUrl));
                startActivity(websiteIntent);
                break;
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            default:
                // ignore
                break;
        }
    }

    private void showFeedback() {
        new MaterialDialog.Builder(this)
                .title(R.string.feedback_title)
                .autoDismiss(true)
                .content(R.string.feedback_description)
                .neutralColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .negativeText(R.string.launch_data)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String url = getString(R.string.launch_library_reddit);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                })
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .positiveText(R.string.app_feedback)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.getBuilder()
                                .title(R.string.need_support)
                                .content(R.string.need_support_description)
                                .neutralText(R.string.email)
                                .negativeText(R.string.cancel)
                                .positiveText(R.string.discord)
                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                                        intent.setData(Uri.parse("mailto:"));
                                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@spacelaunchnow.me"});
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "Space Launch Now - Feedback");

                                        startActivity(Intent.createChooser(intent, "Email via..."));
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        String url = getString(R.string.discord_url);
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                })
                .show();
    }

    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    private void hideAd() {
        adView.setVisibility(View.GONE);
    }

    private void showAd() {
        Timber.v("Showing Ad!");
        if (adviewEnabled && adView.getVisibility() == View.GONE) {
            adView.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FilterViewEvent event) {
        if (!SupporterHelper.isSupporter()) {
            if (event.isOpened) {
                hideAd();
            } else {
                showAd();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SHOW_INTRO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Once.markDone("showTutorial");
                navigate(mNavItemId);
            }
        }
    }

    private void showGDPRIfNecessary(boolean forceShow, GDPRLocation location) {
        if (forceShow || location == GDPRLocation.IN_EAA_OR_UNKNOWN) {
            try {
                GDPR.getInstance().showDialog(this, getGDPRSetup(), location);
            } catch (IllegalStateException e) {
                Timber.e(e);
            }
        }
    }

    private GDPRSetup getGDPRSetup() {

        return new GDPRSetup(GDPRDefinitions.ADMOB,
                GDPRDefinitions.FIREBASE_CRASH,
                new GDPRNetwork("Fabric - Crashlytics",
                        "https://try.crashlytics.com/terms/",
                        context.getString(R.string.gdpr_type_crash),
                        true,
                        false),
                new GDPRNetwork("Fabric - Answers",
                        "https://answers.io/img/onepager/privacy.pdf",
                        context.getString(R.string.gdpr_type_analytics),
                        true,
                        false),
                GDPRDefinitions.FIREBASE_ANALYTICS)
                .withPrivacyPolicy("https://spacelaunchnow.me/app/privacy")
                .withAllowNoConsent(false)
                .withExplicitAgeConfirmation(true)
                .withCheckRequestLocation(true)
                .withBottomSheet(true)
                .withForceSelection(true);
    }

    @Override
    public void onConsentNeedsToBeRequested(GDPRLocation gdprLocation) {
        // default: forward the result and show the dialog
        showGDPRIfNecessary(true, gdprLocation);
    }

    @Override
    public void onConsentInfoUpdate(GDPRConsentState consentState, boolean isNewState) {
        GDPRConsent consent = consentState.getConsent();
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (isNewState) {
            Bundle bundle = new Bundle();
            bundle.putString("GDPR_Consent", consent.name());
            bundle.putString("GDPR_Location", consentState.getLocation().name());
            firebaseAnalytics.logEvent("SLN_GDPR_EVENT", bundle);
            firebaseAnalytics.setUserProperty("gdpr_consent", consent.name());
            firebaseAnalytics.setUserProperty("gdpr_location", consentState.getLocation().name());
            // user just selected this consent, do whatever you want...
            switch (consent) {
                case UNKNOWN:
                    // never happens!
                    break;
                case NO_CONSENT:
                    if (!SupporterHelper.isSupporter()) {
                        Intent intent = new Intent(this, SupporterActivity.class);
                        startActivity(intent);
                    }
                    firebaseAnalytics.setAnalyticsCollectionEnabled(false);
                    break;
                case NON_PERSONAL_CONSENT_ONLY:
                    firebaseAnalytics.setAnalyticsCollectionEnabled(true);
                    break;
                case PERSONAL_CONSENT:
                    firebaseAnalytics.setAnalyticsCollectionEnabled(true);
                    break;
            }
        } else {
            switch (consent) {
                case UNKNOWN:
                    // never happens!
                    break;
                case NO_CONSENT:
                    // with the default setup, the dialog will shown in this case again anyways!
                    if (!SupporterHelper.isSupporter()) {
                        Intent intent = new Intent(this, SupporterActivity.class);
                        startActivity(intent);
                    }
                    FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false);
                    break;
                case NON_PERSONAL_CONSENT_ONLY:
                    firebaseAnalytics.setAnalyticsCollectionEnabled(true);
                    break;
                case PERSONAL_CONSENT:
                    // user restarted activity and consent was already given...
                    break;
            }
        }
        configureAdState(consentState);
    }

    private void configureAdState(GDPRConsentState consentState) {
        boolean allowsPersonalAds = true;
        boolean allowAds = true;
        GDPRConsent consent = consentState.getConsent();

        if (consentState.getLocation() == GDPRLocation.IN_EAA_OR_UNKNOWN && consent == GDPRConsent.UNKNOWN) {
            allowAds = false;
        }

        if (consent == GDPRConsent.NO_CONSENT || consent == GDPRConsent.NON_PERSONAL_CONSENT_ONLY) {
            allowsPersonalAds = false;
        }

        Timber.v("Load Ads");
        if (!SupporterHelper.isSupporter() && allowAds) {
            Timber.d("Loading ads.");
            if (allowsPersonalAds) {
                adView.loadAd(new AdRequest.Builder().build());
            } else {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");

                adView.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build());
            }
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    Timber.d("Failed to load ads.");
                    adviewEnabled = false;
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLoaded() {
                    Timber.d("Ad loaded successfully.");
                    adviewEnabled = true;
                    showAd();
                    super.onAdLoaded();
                }
            });
        } else {
            Timber.d("Hiding ads.");
            adviewEnabled = false;
            hideAd();
        }
    }
}
