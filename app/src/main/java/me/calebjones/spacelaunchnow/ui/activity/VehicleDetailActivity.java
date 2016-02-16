package me.calebjones.spacelaunchnow.ui.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.VehicleListAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import me.calebjones.spacelaunchnow.ui.fragment.vehicles.LaunchVehicleFragment;
import me.calebjones.spacelaunchnow.utils.CustomAnimatorListener;
import me.calebjones.spacelaunchnow.utils.CustomTransitionListener;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class VehicleDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private SharedPreference sharedPreference;
    private android.content.SharedPreferences SharedPreferences;

    private Context context;
    private View view, title_container, gridview;
    private RecyclerView mRecyclerView;
    private TextView toolbarTitle, detail_rocket, detail_vehicle_agency;
    private ImageView detail_profile_backdrop;
    private CircleImageView detail_profile_image;
    private StaggeredGridLayoutManager linearLayoutManager;
    private VehicleListAdapter adapter;
    private List<Rocket> rocketLaunches;
    private AppBarLayout appBarLayout;
    private int mMaxScrollSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int m_theme;
        final int statusColor;
        this.context = getApplicationContext();

        sharedPreference = SharedPreference.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.DarkTheme_Transparent;
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
        } else {
            m_theme = R.style.LightTheme_Transparent;
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }

        if (!BuildConfig.DEBUG){
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("VehicleDetailActivity")
                    .putContentType("Activity"));
        }

        setTheme(m_theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.title_text);
        detail_rocket = (TextView) findViewById(R.id.detail_rocket);
        detail_vehicle_agency = (TextView) findViewById(R.id.detail_vehicle_agency);
        detail_profile_image = (CircleImageView) findViewById(R.id.detail_profile_image);
        detail_profile_backdrop = (ImageView) findViewById(R.id.detail_profile_backdrop);
        appBarLayout = (AppBarLayout) findViewById(R.id.detail_appbar);
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

            // Recover book cover from BooksFragment cache
            Bitmap bookCoverBitmap = LaunchVehicleFragment.photoCache.get(position);
            ImageView toolbarBookCover = (ImageView) findViewById(R.id.detail_profile_backdrop);
            toolbarBookCover.setImageBitmap(bookCoverBitmap);

            // Define toolbar as the shared element
            detail_profile_backdrop.setBackground(new BitmapDrawable(getResources(), bookCoverBitmap));
            detail_profile_backdrop.setTransitionName("cover" + position);

            ViewPropertyAnimator showTitleAnimator = Utils.showViewByScale(detail_profile_image);
            showTitleAnimator.setStartDelay(500);
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
        adapter = new VehicleListAdapter(context);
        mRecyclerView = (RecyclerView) findViewById(R.id.gridview);
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            linearLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            linearLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        }
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        displayRockets();
    }

    public int reverseNumber(int num, int min, int max) {
        return (max + min) - num;
    }

    public void displayRockets() {
        Intent intent = getIntent();
        this.rocketLaunches = this.sharedPreference.getRocketsByFamily(intent.getStringExtra("family"));
        String launcher = intent.getStringExtra("family");
        detail_rocket.setText(launcher);
        detail_vehicle_agency.setText(intent.getStringExtra("agency"));
        adapter.clear();
        adapter.addItems(rocketLaunches);
        if (launcher.contains("Soyuz")) {

            applyProfileBackdrop(R.string.soyuz_image);
            applyProfileLogo(R.string.rus_logo);

        } else if (launcher.contains("Falcon")) {

            applyProfileBackdrop(R.string.falcon_image);
            applyProfileLogo(R.string.spacex_logo);

        } else if (launcher.contains("Proton")) {

            applyProfileBackdrop(R.string.proton_image);
            applyProfileLogo(R.string.rus_logo);

        } else if (launcher.contains("Delta")) {

            applyProfileBackdrop(R.string.delta_image);
            applyProfileLogo(R.string.ula_logo);

        } else if (launcher.contains("Ariane")) {

            applyProfileBackdrop(R.string.ariane_image);
            applyProfileLogo(R.string.ariane_logo);

        } else if (launcher.contains("Space")) {

            applyProfileBackdrop(R.string.shuttle_image);
            applyProfileLogo(getString(R.string.usa_flag));

        } else if (launcher.contains("Long")) {

            applyProfileBackdrop(R.string.long_image);
            applyProfileLogo(R.string.chn_logo);

        } else if (launcher.contains("Atlas")) {

            applyProfileBackdrop(R.string.atlas_image);
            applyProfileLogo(getString(R.string.usa_flag));

        } else if (launcher.contains("PSLV")) {

            applyProfileBackdrop(R.string.pslv_image);
            applyProfileLogo(R.string.ind_logo);

        } else if (launcher.contains("Vega")) {

            applyProfileBackdrop(R.string.vega_image);
            applyProfileLogo(R.drawable.icon_international);

        } else if (launcher.contains("Zenit")) {

            applyProfileBackdrop(R.string.zenit_image);
            applyProfileLogo(R.string.Yuzhnoye_logo);
        }
    }

    private void applyProfileBackdrop(int drawableURL) {
        Timber.d("VehicleDetailActivity - Loading Backdrop Image url: %s ", drawableURL);
        Glide.with(this)
                .load(getString(drawableURL))
                .centerCrop()
                .crossFade()
                .into(detail_profile_backdrop);
    }

    private void applyProfileLogo(String url) {
        Timber.d("VehicleDetailActivity - Loading Profile Image url: %s ", url);

        Glide.with(this)
                .load(url)
                .centerCrop()
                .error(R.drawable.icon_international)
                .into(detail_profile_image);
    }

    private void applyProfileLogo(int drawableURL) {
        Timber.d("VehicleDetailActivity - Loading Profile Image url: %s ", drawableURL);

        Glide.with(this)
                .load(getString(drawableURL))
                .centerCrop()
                .error(R.drawable.icon_international)
                .into(detail_profile_image);
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
