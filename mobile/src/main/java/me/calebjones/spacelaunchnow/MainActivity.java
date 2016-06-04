package me.calebjones.spacelaunchnow;

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
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.ui.activity.SettingsActivity;
import me.calebjones.spacelaunchnow.ui.activity.SupportActivity;
import me.calebjones.spacelaunchnow.ui.fragment.launches.LaunchesViewPager;
import me.calebjones.spacelaunchnow.ui.fragment.launches.NextLaunchFragment;
import me.calebjones.spacelaunchnow.ui.fragment.missions.MissionFragment;
import me.calebjones.spacelaunchnow.ui.fragment.vehicles.VehiclesViewPager;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import timber.log.Timber;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NAV_ITEM_ID = "navItemId";
    private static ListPreferences listPreferences;
    private final Handler mDrawerActionHandler = new Handler();
    private LaunchesViewPager mlaunchesViewPager;
    private MissionFragment mMissionFragment;
    private NextLaunchFragment mUpcomingFragment;
    private VehiclesViewPager mVehicleViewPager;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private SharedPreferences sharedPref;
    private NavigationView navigationView;
    private SwitchPreferences switchPreferences;
    private CustomTabActivityHelper customTabActivityHelper;
    private Context context;

    private static final int REQUEST_CODE = 5467;

    private int mNavItemId;

    public int statusColor;

    public void mayLaunchUrl(Uri parse) {
        if (customTabActivityHelper.mayLaunchUrl(parse, null, null)) {
            Timber.v("mayLaunchURL Accepted - %s", parse.toString());
        } else {
            Timber.v("mayLaunchURL Denied - %s", parse.toString());
        }
    }

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Fabric.isInitialized()) {
            Fabric.with(this, new Crashlytics());
        }
        Timber.d("onCreate");
        int m_theme;
        int m_layout;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.context = getApplicationContext();
        customTabActivityHelper = new CustomTabActivityHelper();

        listPreferences = ListPreferences.getInstance(this.context);
        switchPreferences = SwitchPreferences.getInstance(this.context);

        if (listPreferences.getNightMode()) {
            switchPreferences.setNightModeStatus(true);
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
            m_theme = R.style.DarkTheme_NoActionBar;
            m_layout = R.layout.dark_activity_main;
        } else {
            switchPreferences.setNightModeStatus(false);
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
            m_theme = R.style.LightTheme_NoActionBar;
            m_layout = R.layout.activity_main;
        }

        if (getSharedPreferences("theme_changed", 0).getBoolean("recreate", false)) {
            SharedPreferences.Editor editor = getSharedPreferences("theme_changed", 0).edit();
            editor.putBoolean("recreate", false);
            editor.apply();
            recreate();
        }

        setTheme(m_theme);
        super.onCreate(savedInstanceState);
        setContentView(m_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                int color = statusColor;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;

                float currentScroll = slideOffset * 255;

                int currentScrollInt = Math.round(currentScroll);

                if ((slideOffset) < 1 && (slideOffset) > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.setStatusBarColor(Color.argb(reverseNumber(currentScrollInt, 0, 255), r, g, b));
                    }
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Timber.v("onDrawerOpened - Open");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Timber.v("onDrawerClosed - Closed");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.setStatusBarColor(statusColor);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.menu_next_launch;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) header.findViewById(R.id.backgroundView);
        Glide.with(this)
                .load("http://res.cloudinary.com/dnkkbfy3m/image/upload/v1462465326/navbar_one_sqfhes.png")
                .into(imageView);

        // select the correct nav menu item
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        checkFirstBoot();
    }

    private void setupWindowAnimations() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
            getWindow().setReturnTransition(slide);
        }
    }

    public int reverseNumber(int num, int min, int max) {
        int number = (max + min) - num;
        return number;
    }

    public void onStart() {
        super.onStart();
        Timber.v("MainActivity onStart!");
        customTabActivityHelper.bindCustomTabsService(this);
        mayLaunchUrl(Uri.parse("https://launchlibrary.net/"));
    }

    public void onStop() {
        super.onStop();
        Timber.v("MainActivity onStop!");
        customTabActivityHelper.unbindCustomTabsService(this);
    }

    public void checkFirstBoot() {
        if (listPreferences.getFirstBoot()) {
            switchPreferences.setPrevFiltered(false);
            getFirstLaunches();
            loadTutorial();
        } else {
            navigate(mNavItemId);
        }
    }

    public void showWhatsNew() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.whats_new, null);
        View nightView = inflater.inflate(R.layout.whats_new_night, null);

        MaterialStyledDialog dialog = new MaterialStyledDialog(this)
                .withIconAnimation(false)
                .withDialogAnimation(true)
                .setIcon(new IconicsDrawable(context).icon(MaterialDesignIconic.Icon.gmi_info).color(Color.WHITE))
                .setTitle("Whats New? " + Utils.getVersionName(this))
                .setScrollable(true)
                .setNegative("Feedback", new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        showFeedback();
                    }
                });


        dialog.setPositive("Okay", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                dialog.dismiss();
            }
        });

        if (listPreferences.getNightMode()) {
            dialog.setHeaderColor(R.color.darkPrimary);
            dialog.setCustomView(nightView);
        } else {
            dialog.setHeaderColor(R.color.colorPrimary);
            dialog.setCustomView(customView);
        }
        dialog.show();
    }

    public void getFirstLaunches() {
        Intent launchIntent = new Intent(this.context, LaunchDataService.class);
        launchIntent.setAction(Strings.ACTION_GET_ALL);

        this.context.startService(launchIntent);
    }

    public void onResume() {
        super.onResume();
        if (getSharedPreferences("theme_changed", 0).getBoolean("recreate", false)) {
            SharedPreferences.Editor editor = getSharedPreferences("theme_changed", 0).edit();
            editor.putBoolean("recreate", false);
            editor.apply();
            recreate();
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
                            .title("Confirm Exit")
                            .negativeText("Cancel")
                            .positiveText("Exit")
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
            getMenuInflater().inflate(R.menu.main_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        mNavItemId = item.getItemId();

        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        drawer.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(item.getItemId());
            }
        }, 350);

        return true;
    }

    private void navigate(final int itemId) {
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
                break;
            case R.id.menu_missions:
                mNavItemId = R.id.menu_missions;
                setActionBarTitle("Missions");
                // Check to see if we have retained the worker fragment.
                mMissionFragment = (MissionFragment) fm.findFragmentByTag("MISSION_FRAGMENT");

                // If not retained (or first time running), we need to create it.
                if (mMissionFragment == null) {
                    mMissionFragment = new MissionFragment();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent, mMissionFragment, "MISSION_FRAGMENT").commit();
                }
                break;
            case R.id.menu_vehicle:
                mNavItemId = R.id.menu_vehicle;
                setActionBarTitle("Vehicles");
                // Check to see if we have retained the worker fragment.
                mVehicleViewPager = (VehiclesViewPager) fm.findFragmentByTag("VEHICLE_VIEWPAGER");

                // If not retained (or first time running), we need to create it.
                if (mVehicleViewPager == null) {
                    mVehicleViewPager = new VehiclesViewPager();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent, mVehicleViewPager, "VEHICLE_VIEWPAGER").commit();
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
                Intent supportIntent = new Intent(this, SupportActivity.class);
                startActivity(supportIntent);
                break;
            case R.id.menu_feedback:
                showFeedback();
                break;
            default:
                // ignore
                break;
        }
    }

    private void showFeedback() {
        new MaterialDialog.Builder(this)
                .title("Submit Feedback")
                .autoDismiss(true)
                .content("Feel free to submit bugs or feature requests.")
                .negativeText("Reddit")
                .positiveColor(R.color.colorPrimary)
                .positiveText("Email")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String url = "https://www.reddit.com/r/spacelaunchnow";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@calebjones.me"});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SpaceLaunchNow - Feedback");
                        startActivity(Intent.createChooser(emailIntent, "Send Email"));
                    }
                })
                .show();
    }

    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    public void loadTutorial() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems());
        startActivityForResult(mainAct, REQUEST_CODE);
    }

    private ArrayList<TutorialItem> getTutorialItems() {
        TutorialItem tutorialItem1 = new TutorialItem("Space Launch Now", "Keep up to date on all your favorite  orbital launches, missions, and launch vehicles.",
                R.color.slide_one, R.drawable.intro_slide_one_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem2 = new TutorialItem("Notification for Launches", "Get notifications for upcoming launches and look into the history of spaceflight",
                R.color.slide_two, R.drawable.intro_slide_two_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem3 = new TutorialItem("Keep Track of Missions", "Find out whats going in the world of spaceflight.",
                R.color.slide_three, R.drawable.intro_slide_three_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem4 = new TutorialItem("Find Launch Vehicles", "Get to know the vehicles that have taken us to orbit.",
                R.color.slide_four, R.drawable.intro_slide_four_foreground, R.drawable.intro_slide_background);

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        tutorialItems.add(tutorialItem4);

        return tutorialItems;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //    super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (listPreferences.getFirstBoot()) {
                listPreferences.setFirstBoot(false);
                recreate();

            }
        }
    }
}
