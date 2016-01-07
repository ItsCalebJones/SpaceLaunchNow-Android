package me.calebjones.spacelaunchnow.ui.fragment;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.content.adapter.PreviousLaunchAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousLaunchesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, DatePickerDialog.OnDateSetListener {

    private View view;
    private RecyclerView mRecyclerView;
    private PreviousLaunchAdapter adapter;
    private String newURL;
    private FloatingActionMenu menu;
    private String start_date, end_date;
    private List<Launch> rocketLaunches;
    private SharedPreference sharedPreference;
    private SharedPreferences sharedPrefs;
    private int mScrollPosition;
    private FloatingActionButton agency, vehicle, country;
    private int mScrollOffset = 4;


    private StaggeredGridLayoutManager staggeredLayoutManager;
    private LinearLayoutManager layoutManager;
    private SlideInBottomAnimationAdapter animatorAdapter;

    public PreviousLaunchesFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = SharedPreference.getInstance(getContext());
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.rocketLaunches = new ArrayList();
        adapter = new PreviousLaunchAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        LayoutInflater lf = getActivity().getLayoutInflater();

        view = lf.inflate(R.layout.fragment_previous_launches, container, false);

        agency = (FloatingActionButton) view.findViewById(R.id.agency);
        vehicle = (FloatingActionButton) view.findViewById(R.id.vehicle);
        country = (FloatingActionButton) view.findViewById(R.id.country);
        menu = (FloatingActionMenu) view.findViewById(R.id.menu);
        menu.setTranslationX(menu.getWidth() + 250);

        if (getResources().getBoolean(R.bool.landscape)
                && getResources().getBoolean(R.bool.isTablet)) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_staggered);
            RecyclerView hideView = (RecyclerView) view.findViewById(R.id.recycler_view);
            if (mRecyclerView.getVisibility() != View.VISIBLE){
                hideView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            staggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            staggeredLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            mRecyclerView.setLayoutManager(staggeredLayoutManager);
            mRecyclerView.setAdapter(adapter);
        } else {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            RecyclerView hideView = (RecyclerView) view.findViewById(R.id.recycler_view_staggered);
            if (mRecyclerView.getVisibility() != View.VISIBLE){
                hideView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);
        }
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
            displayLaunches();
        } else {
            Timber.d("Previous Launch Fragment: Not First Boot.");
            this.rocketLaunches.clear();
            getDateRange();
            displayLaunches();
            setTitle();
        }
        setUpFab();
        return view;
    }

    private void setUpFab() {
        menu.setClosedOnTouchOutside(true);

        createCustomAnimation();

        agency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title("Select an Agency")
                        .content("")
                        .items(R.array.agencies)
                        .positiveColorRes(R.color.colorAccentDark)
                        .buttonRippleColorRes(R.color.colorAccentLight)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        newURL = "https://launchlibrary.net/1.1.1/launch/" + start_date + "/" + end_date + "/NASA?sort=desc&limit=200";
                                        adapter.clear();
                                        fetchDataFiltered(newURL, "NASA | " + formatDatesForTitle(start_date) + " " + formatDatesForTitle(end_date));
                                        break;
                                    case 1:
                                        newURL = "https://launchlibrary.net/1.1.1/launch/" + start_date + "/" + end_date + "/SpaceX?sort=desc&limit=200";
                                        adapter.clear();
                                        fetchDataFiltered(newURL, "SpaceX | " + formatDatesForTitle(start_date) + " " + formatDatesForTitle(end_date));
                                        break;
                                    case 2:
                                        newURL = "https://launchlibrary.net/1.1.1/launch/" + start_date + "/" + end_date + "/ROSCOSMOS?sort=desc&limit=200";
                                        adapter.clear();
                                        fetchDataFiltered(newURL, "SpaceX | " + formatDatesForTitle(start_date) + " " + formatDatesForTitle(end_date));
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
        });
        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title("Select a Launch Vehicle")
                        .items(R.array.vehicles)
                        .positiveColorRes(R.color.colorAccentDark)
                        .negativeColorRes(R.color.colorPrimaryLight)
                        .buttonRippleColorRes(R.color.colorAccentLight)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        newURL = "https://launchlibrary.net/1.1.1/launch/" + start_date + "/" + end_date + "/?sort=desc&limit=200&name=Falcon";
                                        adapter.clear();
                                        fetchDataFiltered(newURL, "Falcon | " + formatDatesForTitle(start_date) + " - " + formatDatesForTitle(end_date));
                                        break;
                                    case 1:
                                        newURL = "https://launchlibrary.net/1.1.1/launch/" + start_date + "/" + end_date + "/?sort=desc&limit=200&name=Proton";
                                        adapter.clear();
                                        fetchDataFiltered(newURL, "Proton | " + formatDatesForTitle(start_date) + " - " + formatDatesForTitle(end_date));
                                        break;
                                    case 2:
                                        newURL = "https://launchlibrary.net/1.1.1/launch/" + start_date + "/" + end_date + "/?sort=desc&limit=200&name=Soyuz";
                                        adapter.clear();
                                        fetchDataFiltered(newURL, "Soyuz | " + formatDatesForTitle(start_date) + " - " + formatDatesForTitle(end_date));
                                        break;
                                    case 3:
                                        newURL = "https://launchlibrary.net/1.1.1/launch/" + start_date + "/" + end_date + "/?sort=desc&limit=200&name=Atlas";
                                        adapter.clear();
                                        fetchDataFiltered(newURL, "Atlas | " + formatDatesForTitle(start_date) + " - " + formatDatesForTitle(end_date));
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
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Country", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTitle() {
        ((MainActivity) getActivity()).setActionBarTitle(this.sharedPreference.getPreviousTitle());
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

    public void displayLaunches() {
        this.rocketLaunches = this.sharedPreference.getLaunchesPrevious();
        filterData(this.rocketLaunches);
        //Animate the FAB's loading
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                fabSlideIn();
            }
        }, 750);
    }

    public void filterData(List<Launch> rocketLaunchList) {
        String text_to_filter = this.sharedPreference.getPreviousFilterText().toLowerCase();
        List<Launch> filteredModelList = new ArrayList();
        Iterator it = rocketLaunchList.iterator();
        while (it.hasNext()) {
            Launch rocketLaunch = (Launch) it.next();
            String launch_name = rocketLaunch.getName().toLowerCase();
            String location_name = rocketLaunch.getLocation().getName().toLowerCase();
            if (launch_name.contains(text_to_filter) || location_name.contains(text_to_filter)) {
                filteredModelList.add(rocketLaunch);
            }
        }
        adapter.clear();
        adapter.addItems(filteredModelList);
    }

    public void fetchData() {
        String url = "https://launchlibrary.net/1.1/launch/" + this.start_date + "/" + this.end_date + "?sort=desc&limit=" + this.sharedPrefs.getString("previous_value", "100");
        Timber.d("Sending Intent URL: %s");
        Intent intent = new Intent(getContext(), LaunchDataService.class);
        intent.putExtra("URL", url);
        intent.setAction(Strings.ACTION_GET_PREV_LAUNCHES);
        getContext().startService(intent);
    }

    public void fetchDataFiltered(String url, String filterTitle) {
        Timber.d("Sending Intent URL: %s", url);
        Intent intent = new Intent(getContext(), LaunchDataService.class);
        intent.putExtra("URL", url);
        intent.setAction(Strings.ACTION_GET_PREV_LAUNCHES);
        getContext().startService(intent);
        this.sharedPreference.setPreviousTitle(filterTitle);
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
        String daydatestart = dayOfMonth < 10 ? "0"+dayOfMonth : ""+dayOfMonth;
        String monthdatestart = monthOfYear < 10 ? "0"+monthOfYear : ""+monthOfYear;
        String daydateend = dayOfMonthEnd < 10 ? "0"+dayOfMonthEnd : ""+dayOfMonthEnd;
        String monthdayend = monthOfYearEnd < 10 ? "0"+monthOfYearEnd : ""+monthOfYearEnd;

        start_date = year + "-" + monthdatestart + "-" + daydatestart;
        end_date = yearEnd + "-" + monthdayend + "-" + daydateend;

        this.sharedPreference.setPreviousTitle(formatDatesForTitle(start_date) + " - " +formatDatesForTitle(end_date));

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
            getDefaultDateRange();
            fetchData();
            return true;
        }

        if (id == R.id.reset_filter){
            adapter.clear();
            getDefaultDateRange();
            fetchData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        final List<Launch> filteredModelList = filter(rocketLaunches, query);
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

            if (rocketName.contains(query) || locationName.contains(query) || missionName.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onResume() {
        setTitle();
        super.onResume();
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        getDefaultDateRange();
        fetchData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void fabSlideOut() {
        menu.animate().translationX(menu.getWidth() + 250).setInterpolator(new AccelerateInterpolator(1)).start();
    }

    private void fabSlideIn() {
        menu.animate().translationX(0).setInterpolator(new DecelerateInterpolator(4)).start();
    }
}
