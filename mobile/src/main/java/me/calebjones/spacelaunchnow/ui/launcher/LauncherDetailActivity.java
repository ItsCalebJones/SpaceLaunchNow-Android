package me.calebjones.spacelaunchnow.ui.launcher;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.content.data.DataSaver;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.spacelaunchnow.LauncherAgency;
import me.calebjones.spacelaunchnow.data.models.spacelaunchnow.RocketDetail;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.ui.settings.SettingsActivity;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.views.CustomOnOffsetChangedListener;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LauncherDetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private Context context;
    private RecyclerView mRecyclerView;
    private TextView toolbarTitle, detail_rocket, detail_vehicle_agency;
    private ImageView detail_profile_backdrop;
    private CircleImageView detail_profile_image;
    private VehicleDetailAdapter adapter;
    private RealmResults<RocketDetail> rocketLaunches;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private int mMaxScrollSize;
    private ListPreferences sharedPreference;
    private int statusColor;
    private CoordinatorLayout coordinatorLayout;
    private CustomOnOffsetChangedListener customOnOffsetChangedListener;

    public LauncherDetailActivity() {
        super("Launcher Detail Activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        setTheme(R.style.BaseAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        sharedPreference = ListPreferences.getInstance(this.context);

        if (sharedPreference.isNightModeActive(this)) {
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
        } else {
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.title_text);
        detail_rocket = (TextView) findViewById(R.id.detail_sub_title);
        detail_vehicle_agency = (TextView) findViewById(R.id.detail_title);
        detail_profile_image = (CircleImageView) findViewById(R.id.detail_profile_image);
        detail_profile_backdrop = (ImageView) findViewById(R.id.detail_profile_backdrop);
        collapsingToolbar = findViewById(R.id.main_collapsing_bar);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.detail_appbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Fab button
            detail_profile_image.setScaleX(0);
            detail_profile_image.setScaleY(0);

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
        adapter = new VehicleDetailAdapter(context, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.vehicle_detail_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        displayRockets();
    }

    public void displayRockets() {
        Intent intent = getIntent();
        Gson gson = new Gson();
        final LauncherAgency launcher = gson.fromJson(intent.getStringExtra("json"), LauncherAgency.class);

        if (launcher == null) {
            Toast.makeText(context, R.string.error_launch_details, Toast.LENGTH_SHORT).show();
            Timber.e("Error - Unable to load launch details.");
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        }

        String name = "Unknown";
        if (launcher != null) {
            name = launcher.getLaunchers();
        }
        String agency = "Unknown";
        if (launcher != null) {
            agency = launcher.getAgency();
        }
        detail_rocket.setText(name);
        detail_vehicle_agency.setText(agency);

        rocketLaunches = getRealm().where(RocketDetail.class).contains("agency", agency).findAll();
        if (rocketLaunches.size() > 0) {
            adapter.clear();
            adapter.addItems(rocketLaunches);
        }
        final DataSaver dataSaver = new DataSaver(context);
//        swipeRefreshLayout.setRefreshing(true);
        final String finalAgency = agency;
        DataClient.getInstance().getVehiclesByAgency(agency, new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    RocketDetail[] details = response.body().getVehicles();
                    if (details.length > 0) {
                        getRealm().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                getRealm().where(RocketDetail.class).contains("agency", finalAgency).findAll().deleteAllFromRealm();
                            }
                        });
                        dataSaver.saveObjectsToRealm(details);
                        rocketLaunches = getRealm().where(RocketDetail.class).contains("agency", finalAgency).findAll();
                        adapter.clear();
                        adapter.addItems(rocketLaunches);
                    } else {
                        SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, "Error no launch vehicles found.");
                    }
                } else {
                    SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, "Error loading launch vehicles.");
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
//                swipeRefreshLayout.setRefreshing(false);
                SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, String.format("Error: %s", t.getLocalizedMessage()));
            }
        });


        applyProfileBackdrop(launcher.getImageURL());
        applyProfileLogo(launcher.getNationURL());
    }

    private void applyProfileBackdrop(String drawableURL) {
        Timber.d("LauncherDetailActivity - Loading Backdrop Image url: %s ", drawableURL);
        int palette;
        if (ListPreferences.getInstance(context).isNightModeActive(context)) {
            palette = GlidePalette.Profile.MUTED_DARK;
        } else {
            palette = GlidePalette.Profile.VIBRANT;
        }
        GlideApp.with(this)
                .load(drawableURL)
                .centerCrop()
                .listener(GlidePalette.with(drawableURL)
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
                                            collapsingToolbar.setContentScrimColor(color.getRgb());
                                            customOnOffsetChangedListener.updateStatusColor(color.getRgb());
                                            appBarLayout.setBackgroundColor(color.getRgb());
                                            adapter.updateColor(color.getRgb());
                                            adapter.notifyDataSetChanged();
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
                                            collapsingToolbar.setContentScrimColor(color.getRgb());
                                            customOnOffsetChangedListener.updateStatusColor(color.getRgb());
                                            appBarLayout.setBackgroundColor(color.getRgb());
                                            adapter.updateColor(color.getRgb());
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        })
                        .crossfade(true))
                .into(detail_profile_backdrop);

    }

    private void applyProfileLogo(String url) {
        Timber.d("LauncherDetailActivity - Loading Profile Image url: %s ", url);

        GlideApp.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.icon_international)
                .into(detail_profile_image);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
