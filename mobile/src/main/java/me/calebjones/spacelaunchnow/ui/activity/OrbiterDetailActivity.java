package me.calebjones.spacelaunchnow.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.natives.Orbiter;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import timber.log.Timber;

public class OrbiterDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private ListPreferences sharedPreference;
    private android.content.SharedPreferences SharedPreferences;
    private CustomTabActivityHelper customTabActivityHelper;
    private View view, title_container, gridview;
    private Context context;
    private TextView toolbarTitle, detail_rocket, detail_vehicle_agency,
            orbiter_title, orbiter_description, wikiButton, orbiter_history, orbiter_history_description;
    private View orbiter_vehicle_card;
    private ImageView detail_profile_backdrop;
    private CircleImageView detail_profile_image;
    private AppBarLayout appBarLayout;
    private int mMaxScrollSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int m_theme;
        int layout;
        final int statusColor;
        this.context = getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);
        customTabActivityHelper = new CustomTabActivityHelper();

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.DarkTheme_Transparent;
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
            layout = R.layout.dark_activity_orbiter_detail;
        } else {
            m_theme = R.style.LightTheme_Transparent;
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
            layout = R.layout.activity_orbiter_detail;
        }

        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("OrbiterDetailActivity")
                    .putContentType("Activity"));
        }

        setTheme(m_theme);

        super.onCreate(savedInstanceState);
        setContentView(layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.title_text);
        detail_rocket = (TextView) findViewById(R.id.detail_rocket);
        detail_vehicle_agency = (TextView) findViewById(R.id.detail_vehicle_agency);
        detail_profile_image = (CircleImageView) findViewById(R.id.detail_profile_image);
        detail_profile_backdrop = (ImageView) findViewById(R.id.detail_profile_backdrop);
        orbiter_title = (TextView) findViewById(R.id.orbiter_title);
        orbiter_description = (TextView) findViewById(R.id.orbiter_description);
        wikiButton = (TextView) findViewById(R.id.wikiButton);
        appBarLayout = (AppBarLayout) findViewById(R.id.detail_appbar);
        orbiter_history = (TextView) findViewById(R.id.orbiter_history);
        orbiter_history_description = (TextView) findViewById(R.id.orbiter_history_description);
        orbiter_vehicle_card = findViewById(R.id.orbiter_vehicle_card);
        title_container = findViewById(R.id.detail_title_container);
        gridview = findViewById(R.id.gridview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Fab button
            detail_profile_image.setScaleX(0);
            detail_profile_image.setScaleY(0);
//            setupWindowAnimations();

            // Recover items from the intent
            final int position = getIntent().getIntExtra("position", 0);
            Timber.d("Position %s", position);

            // Add a listener to get noticed when the transition ends to animate the view
            ViewPropertyAnimator showTitleAnimator = Utils.showViewByScale(detail_profile_image);
            showTitleAnimator.setStartDelay(750);
        } else {
            detail_profile_image.setScaleX(1);
            detail_profile_image.setScaleY(1);
        }


        appBarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appBarLayout.getTotalScrollRange();
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalScroll = appBarLayout.getTotalScrollRange();
                int currentScroll = totalScroll + verticalOffset;

                int color = statusColor;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;

                if ((currentScroll) < 255) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(Color.argb(reverseNumber(currentScroll, 0, 255), r, g, b));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    }
                }
            }
        });

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        displayOrbiterDetails();
    }

    public int reverseNumber(int num, int min, int max) {
        return (max + min) - num;
    }

    public void displayOrbiterDetails() {
        final Activity activity = this;
        final Context context = this;

        Intent intent = getIntent();
        Gson gson = new Gson();
        final Orbiter orbiter = gson.fromJson(intent.getStringExtra("json"), Orbiter.class);

        detail_rocket.setText(String.format("%s Spacecraft", orbiter.getName()));
        detail_vehicle_agency.setText(orbiter.getName());

        Glide.with(this)
                .load(orbiter.getImageURL())
                .centerCrop()
                .crossFade()
                .into(detail_profile_backdrop);

        Glide.with(this)
                .load(orbiter.getNationURL())
                .centerCrop()
                .error(R.drawable.icon_international)
                .into(detail_profile_image);

        //Set up history information
        orbiter_history.setText(String.format("%s Spacecraft History", orbiter.getName()));
        orbiter_history_description.setText(orbiter.getHistory());

        if (orbiter.getWikiLink() != null && orbiter.getWikiLink().length() > 0) {
            wikiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.openCustomTab(activity, context, orbiter.getWikiLink());
                }
            });
        } else {
            wikiButton.setVisibility(View.INVISIBLE);
        }

        //Set up vehicle card Information
        orbiter_title.setText(String.format("%s Spacecraft Details", orbiter.getName()));
        orbiter_description.setText(orbiter.getDetails());
    }

    public void onStart() {
        super.onStart();
        Timber.v("LaunchDetailActivity onStart!");
        customTabActivityHelper.bindCustomTabsService(this);
    }

    public void onStop() {
        super.onStop();
        Timber.v("LaunchDetailActivity onStop!");
        customTabActivityHelper.unbindCustomTabsService(this);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
        getWindow().setEnterTransition(slide);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            detail_profile_image.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            detail_profile_image.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }
}
