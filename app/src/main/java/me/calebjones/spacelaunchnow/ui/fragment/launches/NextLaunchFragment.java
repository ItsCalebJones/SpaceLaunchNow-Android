package me.calebjones.spacelaunchnow.ui.fragment.launches;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.LaunchBigAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;

public class NextLaunchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.van_switch)
    AppCompatCheckBox vanSwitch;
    @Bind(R.id.ples_switch)
    AppCompatCheckBox plesSwitch;
    @Bind(R.id.KSC_switch)
    AppCompatCheckBox kscSwitch;
    @Bind(R.id.cape_switch)
    AppCompatCheckBox capeSwitch;
    @Bind(R.id.nasa_switch)
    AppCompatCheckBox nasaSwitch;
    @Bind(R.id.spacex_switch)
    AppCompatCheckBox spacexSwitch;
    @Bind(R.id.roscosmos_switch)
    AppCompatCheckBox roscosmosSwitch;
    @Bind(R.id.ula_switch)
    AppCompatCheckBox ulaSwitch;
    @Bind(R.id.arianespace_switch)
    AppCompatCheckBox arianespaceSwitch;
    @Bind(R.id.casc_switch)
    AppCompatCheckBox cascSwitch;
    @Bind(R.id.isro_switch)
    AppCompatCheckBox isroSwitch;
    @Bind(R.id.all_switch)
    AppCompatCheckBox customSwitch;
    private View view;
    private SuperRecyclerView mRecyclerView;
    private LaunchBigAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View color_reveal;
    private FloatingActionButton menu;
    private List<Launch> rocketLaunches;
    private SharedPreference sharedPreference;
    private Context context;
    private boolean active;
    private boolean switchChanged;

    public NextLaunchFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreference = SharedPreference.getInstance(getActivity().getApplication());
        rocketLaunches = new ArrayList();
        adapter = new LaunchBigAdapter(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity().getApplication();
        final int color;
        active = false;

        if (sharedPreference.getNightMode()) {
            color = R.color.darkPrimary;
        } else {
            color = R.color.colorPrimary;
        }

        sharedPreference = SharedPreference.getInstance(context);

        if (!BuildConfig.DEBUG) {
            if (!BuildConfig.DEBUG) {
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("NextLaunchFragment")
                        .putContentType("Fragment"));
            }
        }

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        LayoutInflater lf = getActivity().getLayoutInflater();
        view = lf.inflate(R.layout.fragment_upcoming, container, false);
        ButterKnife.bind(this, view);

        setUpSwitches();
        color_reveal = view.findViewById(R.id.color_reveal);
        color_reveal.setBackgroundColor(ContextCompat.getColor(context, color));
        menu = (FloatingActionButton) view.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setUpSwitches();
                    if (!active) {
                        switchChanged = false;
                        active = true;
                        mSwipeRefreshLayout.setEnabled(false);
                        menu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            showView();
                        } else {
                            color_reveal.setVisibility(View.VISIBLE);
                        }
                    } else {
                        active = false;
                        menu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_alert));
                        mSwipeRefreshLayout.setEnabled(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            hideView();
                        } else {
                            color_reveal.setVisibility(View.INVISIBLE);
                        }
                        if (switchChanged) {
                            refreshView();
                        }
                    }
                }
        });

        mRecyclerView = (SuperRecyclerView) view.findViewById(R.id.recycler_view);
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        /*Set up Pull to refresh*/
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        if (this.sharedPreference.getUpcomingFirstBoot()) {
            this.sharedPreference.setUpcomingFirstBoot(false);
            Timber.d("Upcoming Launch Fragment: First Boot.");
            if (this.sharedPreference.getLaunchesUpcoming().size() == 0) {
                showLoading();
                fetchData();
            } else {
                this.rocketLaunches.clear();
                displayLaunches();
            }
        } else {
            Timber.d("Upcoming Launch Fragment: Not First Boot.");
            this.rocketLaunches.clear();
            displayLaunches();
        }
        return view;
    }

    private void refreshView() {
        rocketLaunches = sharedPreference.getLaunchesUpcoming();
        List<Launch> goList = new ArrayList<>();
        for (int i = 0; i < rocketLaunches.size(); i++) {
            if (rocketLaunches.get(i).getStatus() == 1) {
                goList.add(rocketLaunches.get(i));
            }
        }
        goList = sharedPreference.filterLaunches(goList);
        sharedPreference.setNextLaunches(goList);
        adapter.clear();
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet) && goList.size() == 1){
            layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);
        } else if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);
        }
        adapter.addItems(goList);
        adapter.notifyDataSetChanged();
    }


    public void displayLaunches() {
        rocketLaunches = sharedPreference.getNextLaunches();

        if (rocketLaunches != null) {
            if (rocketLaunches.size() == 0) {
                Timber.v("Next launches is empty...");
            } else {
                if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet) && rocketLaunches.size() == 1){
                    layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(adapter);
                } else if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
                    layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(adapter);
                }
                adapter.clear();
                adapter.addItems(rocketLaunches);
            }
        }
    }

    private void setUpSwitches() {
        customSwitch.setChecked(sharedPreference.getAllSwitch());
        nasaSwitch.setChecked(sharedPreference.getSwitchNasa());
        spacexSwitch.setChecked(sharedPreference.getSwitchSpaceX());
        roscosmosSwitch.setChecked(sharedPreference.getSwitchRoscosmos());
        ulaSwitch.setChecked(sharedPreference.getSwitchULA());
        arianespaceSwitch.setChecked(sharedPreference.getSwitchArianespace());
        cascSwitch.setChecked(sharedPreference.getSwitchCASC());
        isroSwitch.setChecked(sharedPreference.getSwitchISRO());
        plesSwitch.setChecked(sharedPreference.getSwitchPles());
        capeSwitch.setChecked(sharedPreference.getSwitchCape());
        vanSwitch.setChecked(sharedPreference.getSwitchVan());
        kscSwitch.setChecked(sharedPreference.getSwitchKSC());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideView() {

        // get the center for the clipping circle
        int x = (int) (menu.getX() + menu.getWidth() / 2);
        int y = (int) (menu.getY() + menu.getHeight() / 2);

        // get the initial radius for the clipping circle
        int initialRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(color_reveal, x, y, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                color_reveal.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showView() {

        // get the center for the clipping circle
        int x = (int) (menu.getX() + menu.getWidth() / 2);
        int y = (int) (menu.getY() + menu.getHeight() / 2);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(color_reveal, x, y, 0, finalRadius);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

//                showAlertDialog();
            }
        });

        color_reveal.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void fetchData() {
        this.sharedPreference.removeUpcomingLaunches();
        Intent intent = new Intent(getContext(), LaunchDataService.class);
        intent.setAction(Strings.ACTION_GET_UP_LAUNCHES);
        Timber.d("Sending service intent!");
        getContext().startService(intent);
    }

    public void showLoading() {
        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
    }


    @Override
    public void onResume() {
        setTitle();
        Timber.d("OnResume!");
        super.onResume();
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    private void setTitle() {
        ((MainActivity) getActivity()).setActionBarTitle("Space Launch Now");
    }

    public void onFinishedRefreshing() {
        rocketLaunches.clear();
        displayLaunches();
        mSwipeRefreshLayout.setRefreshing(false);
        hideLoading();
    }

    private void hideLoading() {
        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.GONE);
        progressView.resetAnimation();
    }

    //Currently only used to debug
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //
        if (BuildConfig.DEBUG) {
            menu.clear();
            inflater.inflate(R.menu.debug_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.debug_add_launch) {
            if (sharedPreference.getDebugLaunch()) {
                sharedPreference.setDebugLaunch(false);
            } else {
                sharedPreference.setDebugLaunch(true);
            }
            onRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.nasa_switch)
    public void nasa_switch() {
        switchChanged = true;
        sharedPreference.setSwitchNasa(!sharedPreference.getSwitchNasa());
    }

    @OnClick(R.id.spacex_switch)
    public void spacex_switch() {
        switchChanged = true;
        sharedPreference.setSwitchSpaceX(!sharedPreference.getSwitchSpaceX());
    }

    @OnClick(R.id.roscosmos_switch)
    public void roscosmos_switch() {
        switchChanged = true;
        sharedPreference.setSwitchRoscosmos(!sharedPreference.getSwitchRoscosmos());
    }

    @OnClick(R.id.ula_switch)
    public void ula_switch() {
        switchChanged = true;
        sharedPreference.setSwitchULA(!sharedPreference.getSwitchULA());
    }

    @OnClick(R.id.arianespace_switch)
    public void arianespace_switch() {
        switchChanged = true;
        sharedPreference.setSwitchArianespace(!sharedPreference.getSwitchArianespace());
    }

    @OnClick(R.id.casc_switch)
    public void casc_switch() {
        switchChanged = true;
        sharedPreference.setSwitchCASC(!sharedPreference.getSwitchCASC());
    }

    @OnClick(R.id.isro_switch)
    public void isro_switch() {
        switchChanged = true;
        sharedPreference.setSwitchISRO(!sharedPreference.getSwitchISRO());
    }

    @OnClick(R.id.KSC_switch)
    public void KSC_switch() {
        switchChanged = true;
        sharedPreference.setSwitchKSC(!sharedPreference.getSwitchKSC());
    }

    @OnClick(R.id.ples_switch)
    public void ples_switch() {
        switchChanged = true;
        sharedPreference.setSwitchPles(!sharedPreference.getSwitchPles());
    }

    @OnClick(R.id.van_switch)
    public void van_switch() {
        switchChanged = true;
        sharedPreference.setSwitchVan(!sharedPreference.getSwitchVan());
    }

    @OnClick(R.id.cape_switch)
    public void cape_switch() {
        switchChanged = true;
        sharedPreference.setSwitchCape(!sharedPreference.getSwitchCape());
    }

    @OnClick(R.id.all_switch)
    public void all_switch() {
        switchChanged = true;
        sharedPreference.setAllSwitch(!sharedPreference.getAllSwitch());
            setUpSwitches();
    }
}
