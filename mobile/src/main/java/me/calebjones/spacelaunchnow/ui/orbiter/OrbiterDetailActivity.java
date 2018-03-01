package me.calebjones.spacelaunchnow.ui.orbiter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.spacelaunchnow.Orbiter;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.ui.settings.SettingsActivity;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import me.calebjones.spacelaunchnow.utils.views.CustomOnOffsetChangedListener;
import timber.log.Timber;

public class OrbiterDetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

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
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int mMaxScrollSize;
    private CustomOnOffsetChangedListener customOnOffsetChangedListener;

    public OrbiterDetailActivity() {
        super("Orbiter Detail Activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int m_theme;
        final int statusColor;
        this.context = getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);
        customTabActivityHelper = new CustomTabActivityHelper();

        if (sharedPreference.isNightModeActive(this)) {
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
        } else {
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }

        m_theme = R.style.BaseAppTheme;

        setTheme(m_theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orbiter_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.title_text);
        detail_rocket = (TextView) findViewById(R.id.detail_title);
        detail_vehicle_agency = (TextView) findViewById(R.id.detail_sub_title);
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
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        gridview = findViewById(R.id.vehicle_detail_list);

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
            showTitleAnimator.setStartDelay(500);
        } else {
            detail_profile_image.setScaleX(1);
            detail_profile_image.setScaleY(1);
        }

        customOnOffsetChangedListener = new CustomOnOffsetChangedListener(statusColor, getWindow());
        appBarLayout.addOnOffsetChangedListener(this);
        appBarLayout.addOnOffsetChangedListener(customOnOffsetChangedListener);
        mMaxScrollSize = appBarLayout.getTotalScrollRange();

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

    public void displayOrbiterDetails() {
        final Activity activity = this;
        final Context context = this;

        Intent intent = getIntent();
        Gson gson = new Gson();
        final Orbiter orbiter = gson.fromJson(intent.getStringExtra("json"), Orbiter.class);

        if (orbiter == null){
            Toast.makeText(context, R.string.error_orbiter_details, Toast.LENGTH_SHORT).show();
            Timber.e("Error - Unable to load launch details.");
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        }

        detail_rocket.setText(String.format("%s Spacecraft", orbiter.getName()));
        detail_vehicle_agency.setText(orbiter.getName());

        GlideApp.with(this)
                .load(orbiter.getImageURL())
                .centerCrop()
                .into(detail_profile_backdrop);

        int palette;
        if (ListPreferences.getInstance(context).isNightModeActive(context)) {
            palette = GlidePalette.Profile.MUTED_DARK;
        } else {
            palette = GlidePalette.Profile.VIBRANT;
        }

        GlideApp.with(this)
                .load(orbiter.getImageURL())
                .centerCrop()
                .listener(GlidePalette.with(orbiter.getImageURL())
                        .use(palette)
                        .intoCallBack(new BitmapPalette.CallBack() {
                            @Override
                            public void onPaletteLoaded(@Nullable Palette palette) {
                                if (ListPreferences.getInstance(context).isNightModeActive(context)) {
                                    if (palette != null) {
                                        Palette.Swatch color = null;
                                        if (palette.getDarkMutedSwatch() != null) {
                                            color = palette.getDarkMutedSwatch();
                                        } else if (palette.getDarkVibrantSwatch() != null){
                                            color = palette.getDarkVibrantSwatch();
                                        }
                                        if (color != null) {
                                            collapsingToolbarLayout.setContentScrimColor(color.getRgb());
                                            customOnOffsetChangedListener.updateStatusColor(color.getRgb());
                                            appBarLayout.setBackgroundColor(color.getRgb());
                                        }
                                    }
                                } else {
                                    if (palette != null) {
                                        Palette.Swatch color = null;
                                        if (palette.getVibrantSwatch() != null) {
                                            color = palette.getVibrantSwatch();
                                        } else if (palette.getMutedSwatch() != null){
                                            color = palette.getMutedSwatch();
                                        }
                                        if (color != null) {
                                            collapsingToolbarLayout.setContentScrimColor(color.getRgb());
                                            customOnOffsetChangedListener.updateStatusColor(color.getRgb());
                                            appBarLayout.setBackgroundColor(color.getRgb());
                                        }
                                    }
                                }
                            }
                        })
                        .crossfade(true))
                .into(detail_profile_backdrop);

        GlideApp.with(this)
                .load(orbiter.getNationURL())
                .centerCrop()
                .placeholder(R.drawable.icon_international)
                .into(detail_profile_image);

        //Set up history information
        orbiter_history.setText(String.format(getString(R.string.spacecraft_history), orbiter.getName()));
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
        orbiter_title.setText(String.format(getString(R.string.spacecraft_details), orbiter.getName()));
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
