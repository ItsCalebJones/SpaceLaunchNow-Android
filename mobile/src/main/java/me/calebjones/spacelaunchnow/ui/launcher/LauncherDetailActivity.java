package me.calebjones.spacelaunchnow.ui.launcher;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.content.data.DataSaver;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Launcher;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.Rocket;
import me.calebjones.spacelaunchnow.data.models.RocketDetail;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.ui.settings.SettingsActivity;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LauncherDetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private ListPreferences sharedPreference;

    private Context context;
    private RecyclerView mRecyclerView;
    private TextView toolbarTitle, detail_rocket, detail_vehicle_agency;
    private ImageView detail_profile_backdrop;
    private CircleImageView detail_profile_image;
    private VehicleDetailAdapter adapter;
    private RealmResults<RocketDetail> rocketLaunches;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinator;
    private int mMaxScrollSize;

    public LauncherDetailActivity() {
        super("Launcher Detail Activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int m_theme;
        final int statusColor;
        this.context = getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        if (sharedPreference.isNightModeActive(this)) {
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
        } else {
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }

        m_theme = R.style.BaseAppTheme;

        setTheme(m_theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.title_text);
        detail_rocket = (TextView) findViewById(R.id.detail_rocket);
        detail_vehicle_agency = (TextView) findViewById(R.id.detail_vehicle_agency);
        detail_profile_image = (CircleImageView) findViewById(R.id.detail_profile_image);
        detail_profile_backdrop = (ImageView) findViewById(R.id.detail_profile_backdrop);
        appBarLayout = (AppBarLayout) findViewById(R.id.detail_appbar);
        swipeRefreshLayout = findViewById(R.id.vehicle_swipe_refresh);
        coordinator = findViewById(R.id.coordinator_layout);
        swipeRefreshLayout.setEnabled(false);

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
        adapter = new VehicleDetailAdapter(context, this, getRealm());
        mRecyclerView = (RecyclerView) findViewById(R.id.vehicle_detail_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        displayRockets();
    }

    public int reverseNumber(int num, int min, int max) {
        return (max + min) - num;
    }

    public void displayRockets() {
        Intent intent = getIntent();
        Gson gson = new Gson();
        final Launcher launcher = gson.fromJson(intent.getStringExtra("json"), Launcher.class);

        if (launcher == null) {
            Toast.makeText(context, "Error - Unable to load launcher details.", Toast.LENGTH_SHORT).show();
            Timber.e("Error - Unable to load launch details.");
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        }

        final String name = launcher.getName();
        String agency = launcher.getAgency();
        detail_rocket.setText(name);
        detail_vehicle_agency.setText(agency);

        rocketLaunches = getRealm().where(RocketDetail.class).contains("family", name).findAll();
        if (rocketLaunches.size() > 0) {
            adapter.clear();
            adapter.addItems(rocketLaunches);
        }
        final DataSaver dataSaver = new DataSaver(context);
        swipeRefreshLayout.setRefreshing(true);
        DataClient.getInstance().getVehicles(name, new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    RocketDetail[] details = response.body().getVehicles();
                    if (details.length > 0) {
                        getRealm().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                getRealm().where(RocketDetail.class).contains("family",name).findAll().deleteAllFromRealm();
                            }
                        });
                        dataSaver.saveObjectsToRealm(details);
                        rocketLaunches = getRealm().where(RocketDetail.class).contains("family", name).findAll();
                        adapter.clear();
                        adapter.addItems(rocketLaunches);
                    } else {
                        SnackbarHandler.showErrorSnackbar(context, coordinator, "Error no launch vehicles found.");
                    }
                } else {
                    SnackbarHandler.showErrorSnackbar(context, coordinator, "Error loading launch vehicles.");
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                SnackbarHandler.showErrorSnackbar(context, coordinator, String.format("Error: %s", t.getLocalizedMessage()));
            }
        });


        applyProfileBackdrop(launcher.getImageURL());
        applyProfileLogo(launcher.getNationURL());
    }

    private void applyProfileBackdrop(String drawableURL) {
        Timber.d("LauncherDetailActivity - Loading Backdrop Image url: %s ", drawableURL);
        GlideApp.with(this)
                .load(drawableURL)
                .centerCrop()
                .placeholder(R.drawable.icon_international)
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
