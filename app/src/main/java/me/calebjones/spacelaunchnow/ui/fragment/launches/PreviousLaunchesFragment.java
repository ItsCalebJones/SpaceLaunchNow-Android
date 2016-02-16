package me.calebjones.spacelaunchnow.ui.fragment.launches;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.content.adapter.LaunchCompactAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;


public class PreviousLaunchesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, DatePickerDialog.OnDateSetListener {

    private View view, empty;
    private RecyclerView mRecyclerView;
    private LaunchCompactAdapter adapter;
    private String newURL;
    private FloatingActionMenu menu;
    private String start_date, end_date;
    private List<Launch> rocketLaunches;
    private SharedPreference sharedPreference;
    private SharedPreferences sharedPrefs;
    private int mScrollPosition;
    private FloatingActionButton agency, vehicle, country, reset;
    private int mScrollOffset = 4;
    private static final Field sChildFragmentManagerField;


    private StaggeredGridLayoutManager staggeredLayoutManager;
    private LinearLayoutManager layoutManager;
    private SlideInBottomAnimationAdapter animatorAdapter;

    public PreviousLaunchesFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sharedPreference = SharedPreference.getInstance(getContext());
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.rocketLaunches = new ArrayList();
        adapter = new LaunchCompactAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        if (!BuildConfig.DEBUG){
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("PreviousLaunchesFragment")
                    .putContentType("Fragment"));
        }

        LayoutInflater lf = getActivity().getLayoutInflater();

        view = lf.inflate(R.layout.fragment_previous_launches, container, false);
        ButterKnife.bind(getActivity());

        agency = (FloatingActionButton) view.findViewById(R.id.agency);
        vehicle = (FloatingActionButton) view.findViewById(R.id.vehicle);
        country = (FloatingActionButton) view.findViewById(R.id.country);
        reset = (FloatingActionButton) view.findViewById(R.id.reset);
        menu = (FloatingActionMenu) view.findViewById(R.id.menu);
        empty = view.findViewById(R.id.empty_launch_root);
        menu.setTranslationX(menu.getWidth() + 250);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView hideView = (RecyclerView) view.findViewById(R.id.recycler_view_staggered);
        if (mRecyclerView.getVisibility() != View.VISIBLE) {
            hideView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > mScrollOffset) {
                    if (dy > 0) {
                        menu.hideMenu(true);
                    } else {
                        menu.showMenu(true);
                    }
                }
            }
        });

        if (this.sharedPreference.getPreviousFirstBoot()) {
            this.sharedPreference.setPreviousFirstBoot(false);
            Timber.d("Previous Launch Fragment: First Boot.");
            getDefaultDateRange();
            if(this.sharedPreference.getLaunchesPrevious() == null || this.sharedPreference.getLaunchesPrevious().size() == 0){
                fetchData();
            } else {
                this.rocketLaunches.clear();
                displayLaunches();
            }
        } else {
            Timber.d("Previous Launch Fragment: Not First Boot.");
            this.rocketLaunches.clear();
            getDateRange();
            displayLaunches();
        }
        setUpFab();
        return view;
    }

    private void setUpFab() {
        menu.setClosedOnTouchOutside(true);

        createCustomAnimation();

        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (sharedPreference.getFiltered()){
                    sharedPreference.setFiltered(false);
                    sharedPreference.removeFilteredList();
                    getDefaultDateRange();
                    displayLaunches();
                }
                menu.hideMenu(true);
            }
        });

        agency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAgencyDialog();
            }
        });
        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVehicleDialog();
            }
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryDialog();
            }
        });
    }

    private void showCountryDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Select a Country")
                .content("Check an country below, to remove all filters use reset icon in the toolbar.")
                .items(R.array.country)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                adapter.clear();
                                fetchDataFiltered(2, "USA", "USA");
                                break;
                            case 1:
                                adapter.clear();
                                fetchDataFiltered(2, "China", "China");
                                break;
                            case 2:
                                adapter.clear();
                                fetchDataFiltered(2, "Russia", "Russia");
                                break;
                            case 3:
                                adapter.clear();
                                fetchDataFiltered(2, "India", "India");
                                break;
                            case 4:
                                adapter.clear();
                                fetchDataFiltered(2, "Multi", "Multi");
                                break;

                        }
                        menu.toggle(false);
                        return true;
                    }
                })
                .positiveText("Filter")
                .negativeText("Close")
                .icon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher))
                .show();
    }

    private void showAgencyDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Select an Agency")
                .content("Check an agency below, to remove all filters use reset icon in the toolbar.")
                .items(R.array.agencies)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String date = formatDatesForTitle(start_date) + " " + formatDatesForTitle(end_date);
                        switch (which) {
                            case 0:
                                adapter.clear();
                                fetchDataFiltered(0, "44", "NASA");
                                break;
                            case 1:
                                adapter.clear();
                                fetchDataFiltered(0, "121", "SpaceX");
                                break;
                            case 2:
                                adapter.clear();
                                fetchDataFiltered(0, "63", "ROSCOSMOS");
                                break;
                            case 3:
                                adapter.clear();
                                fetchDataFiltered(0, "124", "ULA");
                                break;
                            case 4:
                                adapter.clear();
                                fetchDataFiltered(0, "115", "Arianespace");
                                break;
                            case 5:
                                adapter.clear();
                                fetchDataFiltered(0, "88", "CASC");
                                break;
                            case 6:
                                adapter.clear();
                                fetchDataFiltered(0, "31", "ISRO");
                                break;

                        }
                        menu.toggle(false);
                        return true;
                    }
                })
                .positiveText("Filter")
                .negativeText("Close")
                .icon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher))
                .show();
    }

    private void showVehicleDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Select a Launch Vehicle")
                .content("Check a vehicle below, to remove all filters use reset icon in the toolbar.")
                .items(R.array.vehicles)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                adapter.clear();
                                fetchDataFiltered(1, "Falcon", "Falcon");
                                break;
                            case 1:
                                adapter.clear();
                                fetchDataFiltered(1, "Proton", "Proton");
                                break;
                            case 2:
                                adapter.clear();
                                fetchDataFiltered(1, "Soyuz", "Soyuz");
                                break;
                            case 3:
                                adapter.clear();
                                fetchDataFiltered(1 , "Atlas", "Atlas");
                                break;
                            case 4:
                                adapter.clear();
                                fetchDataFiltered(1 , "Delta", "Delta");
                                break;
                            case 5:
                                adapter.clear();
                                fetchDataFiltered(1 , "Long", "Long March");
                                break;
                            case 6:
                                adapter.clear();
                                fetchDataFiltered(1 , "SLV", "PSLV/GSLV");
                                break;
                            case 7:
                                adapter.clear();
                                fetchDataFiltered(1 , "Ariane", "Ariane");
                                break;
                            case 8:
                                adapter.clear();
                                fetchDataFiltered(1 , "Zenit", "Zenit");
                                break;
                            case 9:
                                adapter.clear();
                                fetchDataFiltered(1 , "Rokot", "Rokot");
                                break;
                        }

                        menu.toggle(false);
                        return true;
                    }
                })
                .positiveText("Filter")
                .negativeText("Close")
                .icon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher))
                .show();
    }

    private void setTitle() {
        ((MainActivity) getActivity()).setActionBarTitle(this.sharedPreference.getPreviousTitle());
    }

    public void recreate(){
        recreate();
    }

    private String formatDatesForTitle(String start_date) {
        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat out = new SimpleDateFormat("LLL yyyy");

        Date sDate;

        try {
            sDate = in.parse(start_date);
            return out.format(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO Test empty
    public void displayLaunches() {
        Timber.v("DisplayLaunches - Filtered - %s", sharedPreference.getFiltered());
        if (!sharedPreference.getFiltered()){
            rocketLaunches = sharedPreference.getLaunchesPrevious();
        } else {
            rocketLaunches = sharedPreference.getLaunchesPreviousFiltered();
        }

        if (rocketLaunches != null) {
            Timber.v("DisplayLaunches - List size: %s", rocketLaunches.size());

            adapter.clear();
            if (rocketLaunches.size() > 0) {
                empty.setVisibility(View.GONE);
                adapter.addItems(rocketLaunches);
            } else {
                empty.setVisibility(View.VISIBLE);
            }
            //Animate the FAB's loading
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fabSlideIn();
                }
            }, 750);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    public void fetchData() {
        showLoading();
        String url = "https://launchlibrary.net/1.1/launch/" + this.start_date + "/" + this.end_date + "?sort=desc&limit=1000";
        Timber.d("Sending Intent URL: %s", url);
        Intent intent = new Intent(getContext(), LaunchDataService.class);
        intent.putExtra("URL", url);
        intent.setAction(Strings.ACTION_GET_PREV_LAUNCHES);
        getContext().startService(intent);
    }

    private void showLoading() {
        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
    }

    // Three types: 0 - Agency 1 - Vehicle 2 - Country
    public void fetchDataFiltered(int type, String key, String title) {
        Timber.d("Filtering by: %s", key);

        if (!BuildConfig.DEBUG){
            Answers.getInstance().logSearch(new SearchEvent()
                    .putQuery(key));
        }

        if (sharedPreference.getFiltered()){
            sharedPreference.setPreviousTitle(sharedPreference.getPreviousTitle() + " | " + title);
        } else {
            sharedPreference.setPreviousTitle(title);
        }
        sharedPreference.setPrevFilter(type, key);
        displayLaunches();
        setTitle();
    }

    private void createCustomAnimation() {

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menu.getMenuIconView().setImageResource(menu.isOpened()
                        ? R.drawable.ic_sort : R.drawable.ic_close);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menu.setIconToggleAnimatorSet(set);
    }

    //Required for DateRange Dialogue that returns data from the dialogue.
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        monthOfYear = monthOfYear + 1;
        monthOfYearEnd = monthOfYearEnd + 1;

        if (monthOfYear == 0){
            monthOfYear = 1;
        }
        if (monthOfYearEnd == 0){
            monthOfYearEnd = 1;
        }
        String daydatestart = dayOfMonth < 10 ? "0"+dayOfMonth : ""+dayOfMonth;
        String monthdatestart = monthOfYear < 10 ? "0"+monthOfYear : ""+monthOfYear;
        String daydateend = dayOfMonthEnd < 10 ? "0"+dayOfMonthEnd : ""+dayOfMonthEnd;
        String monthdayend = monthOfYearEnd < 10 ? "0"+monthOfYearEnd : ""+monthOfYearEnd;

        start_date = year + "-" + monthdatestart + "-" + daydatestart;
        end_date = yearEnd + "-" + monthdayend + "-" + daydateend;

        if (sharedPreference.getFiltered()){
            this.sharedPreference.setPreviousTitle(sharedPreference.getPreviousTitle() + " | " + formatDatesForTitle(start_date) + " - " + formatDatesForTitle(end_date));
        } else {
            this.sharedPreference.setPreviousTitle(formatDatesForTitle(start_date) + " - " + formatDatesForTitle(end_date));
            this.sharedPreference.setFiltered(true);
        }

        setTitle();
        adapter.clear();
        fetchData();
    }
    public void getDateRange() {
        start_date = this.sharedPreference.getStartDate();
        end_date = this.sharedPreference.getEndDate();
    }

    public void getDefaultDateRange() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        this.start_date = this.sharedPreference.getStartDate();
        this.end_date = String.valueOf(formattedDate);
        this.sharedPreference.resetPreviousTitle();
        setTitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.previous_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_date) {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                    PreviousLaunchesFragment.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getActivity().getFragmentManager(), "DatePicker");
            return true;
        }
        if (id == R.id.action_refresh) {
            adapter.clear();
            this.sharedPreference.setFiltered(false);
            getDefaultDateRange();
            fetchData();
            return true;
        }

        if (id == R.id.return_home){
            mRecyclerView.scrollToPosition(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        final List<Launch> filteredModelList = filter(rocketLaunches, query);
        if (query.length() > 3){
            if (!BuildConfig.DEBUG){
                Answers.getInstance().logSearch(new SearchEvent()
                        .putQuery(query));
            }
        }
        adapter.animateTo(filteredModelList);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.scrollToPosition(0);
            }
        }, 500);
//        mRecyclerView.scrollToPosition(0);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Launch> filter(List<Launch> models, String query) {
        query = query.toLowerCase();

        final List<Launch> filteredModelList = new ArrayList<>();
        for (Launch model : models) {
            final String name = model.getName().toLowerCase();
            final String rocketName = model.getRocket().getName().toLowerCase();
            final String locationName = model.getLocation().getName().toLowerCase();
            String missionName;

            //If pad and agency exist add it to location, otherwise get whats always available
            if (model.getLocation().getPads().size() > 0 && model.getLocation().getPads().
                    get(0).getAgencies().size() > 0){
                missionName = model.getLocation().getPads().get(0).getAgencies().get(0).getName() + " " + (model.getRocket().getName());
            } else {
                missionName = model.getRocket().getName();
            }
            missionName = missionName.toLowerCase();

            if (rocketName.contains(query) || locationName.contains(query) || missionName.contains(query) || name.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onResume() {
        Timber.d("OnResume!");
        super.onResume();
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        this.sharedPreference.setFiltered(false);
        getDefaultDateRange();
        fetchData();
    }

    private void fabSlideOut() {
        menu.animate().translationX(menu.getWidth() + 250).setInterpolator(new AccelerateInterpolator(1)).start();
    }

    private void fabSlideIn() {
        menu.animate().translationX(0).setInterpolator(new DecelerateInterpolator(4)).start();
    }

    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.getLocalizedMessage();
            Timber.e("Error getting mChildFragmentManager field %s", e.getLocalizedMessage());
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                e.getLocalizedMessage();
                Timber.e("Error setting mChildFragmentManager field %s", e.getLocalizedMessage());
            }
        }
    }
}
