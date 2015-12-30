package me.calebjones.spacelaunchnow.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.content.adapter.PreviousLaunchAdapter;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.loader.PreviousLaunchLoader;
import me.calebjones.spacelaunchnow.utils.EndlessRecyclerOnScrollListener;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousLaunchesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, DatePickerDialog.OnDateSetListener {

    private View view;
    private RecyclerView mRecyclerView;
    private PreviousLaunchAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SlideInBottomAnimationAdapter animatorAdapter;
    private String CurrentURL;
    private FloatingActionButton agency;
    private FloatingActionButton vehicle;
    private FloatingActionButton country;
    private FloatingActionButton menu;
    private List<Launch> mModels;
    private int mScrollPosition;

    public PreviousLaunchesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        // keep the fragment and all its data across screen rotation
        setRetainInstance(true);

        LayoutInflater lf = getActivity().getLayoutInflater();

        CurrentURL = "https://launchlibrary.net/1.1.1/launch/1990-01-01/%s?sort=desc&limit=20";

        view = lf.inflate(R.layout.fragment_launches, container, false);

        final FloatingActionButton agency = (FloatingActionButton) view.findViewById(R.id.agency);
        final FloatingActionButton vehicle = (FloatingActionButton) view.findViewById(R.id.vehicle);
        final FloatingActionButton country = (FloatingActionButton) view.findViewById(R.id.country);
        final FloatingActionMenu menu = (FloatingActionMenu) view.findViewById(R.id.menu);

        menu.setClosedOnTouchOutside(true);

        agency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title("Select an Agency")
                        .items(R.array.agencies)
                        .positiveColorRes(R.color.colorAccentDark)
                        .buttonRippleColorRes(R.color.colorAccentLight)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                Calendar c = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = df.format(c.getTime());

                                switch (which) {
                                    case 0:
                                        CurrentURL = "https://launchlibrary.net/1.1.1/launch/1990-01-01/%s/NASA?sort=desc&limit=20";
                                        break;
                                    case 1:
                                        CurrentURL = "https://launchlibrary.net/1.1.1/launch/1990-01-01/%s/SpaceX?sort=desc&limit=20";
                                        break;
                                }
                                PreviousLaunchLoader loader = new PreviousLaunchLoader() {
                                    @Override
                                    protected void onPreExecute() {
                                        launchArrayList = new ArrayList<>();
                                        if (adapter.getItemCount() != 0) {
                                            adapter.removeAll();
                                        }
                                    }

                                    @Override
                                    protected void onPostExecute(List<Launch> result) {
                                        /* Download complete. Lets update UI */
                                        if (result != null) {
                                            List<Launch> thisList = result;
                                            Log.d(LaunchApplication.TAG, "PreviousLaunchFragment"
                                                    + result.get(0).getName() + " "
                                                    + result.size());
                                            adapter.addItems(thisList);
                                            adapter.notifyDataSetChanged();
                                            mRecyclerView.smoothScrollToPosition(0);
                                        } else
                                            Log.e(LaunchApplication.TAG, "Failed to fetch data!");
                                    }
                                };
                                loader.execute(String.format(CurrentURL, String.valueOf(formattedDate)));
                                menu.toggle(false);
                                return true;
                            }
                        })
                        .positiveText("Filter")
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
                        .buttonRippleColorRes(R.color.colorAccentLight)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                Calendar c = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = df.format(c.getTime());

                                switch (which) {
                                    case 0:
                                        CurrentURL = "https://launchlibrary.net/1.1.1/launch/" +
                                                "1990-01-01/%s/?sort=desc&limit=20" +
                                                "&rocketid=1&rocketid=2";
                                        break;
                                    case 1:
                                        CurrentURL = "https://launchlibrary.net/1.1.1/launch/" +
                                                "1990-01-01/%s/?sort=desc&limit=20&rocketid=4";
                                        break;
                                    case 2:
                                        CurrentURL = "https://launchlibrary.net/1.1.1/launch/" +
                                                "1990-01-01/%s/?sort=desc&limit=20&rocketid=3" +
                                                "&rocketid=6&rocketid=35&rocketid=87";
                                        break;
                                    case 3:
                                        CurrentURL = "https://launchlibrary.net/1.1.1/launch/" +
                                                "1990-01-01/%s/?sort=desc&limit=20&rocketid=2" +
                                                "&rocketid=10&rocketid=26&rocketid=11&rocketid=37";
                                        break;
                                }
                                PreviousLaunchLoader loader = new PreviousLaunchLoader() {
                                    @Override
                                    protected void onPreExecute() {
                                        launchArrayList = new ArrayList<>();
                                        if (adapter.getItemCount() != 0) {
                                            adapter.removeAll();
                                        }
                                    }

                                    @Override
                                    protected void onPostExecute(List<Launch> result) {
                                        /* Download complete. Lets update UI */
                                        if (result != null) {
                                            List<Launch> thisList = result;
                                            Log.d(LaunchApplication.TAG, "PreviousLaunchFragment" + result.get(0).getName() + " " + result.size());
                                            adapter.addItems(thisList);
                                            adapter.notifyDataSetChanged();
                                            mRecyclerView.smoothScrollToPosition(0);
                                        } else
                                            Log.e(LaunchApplication.TAG, "Failed to fetch data!");
                                    }
                                };
                                loader.execute(String.format(CurrentURL, String.valueOf(formattedDate)));
                                menu.toggle(false);
                                return true;
                            }
                        })
                        .positiveText("Filter")
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int topRowVerticalPostion = (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ? 0 : mRecyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(dx == 0 && topRowVerticalPostion >= 0);
            }
        });
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        CurrentURL = String.format("https://launchlibrary.net/1.1.1/launch/1990-01-01/%s?sort=desc&limit=20", String.valueOf(formattedDate));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                PreviousLaunchLoader loader = new PreviousLaunchLoader() {
                    @Override
                    protected void onPreExecute() {
                        launchArrayList = new ArrayList<>();
                        Log.d(LaunchApplication.TAG, "List Size: " + String.valueOf(launchArrayList.size()) + " Adapter:" + adapter.getItemCount());
                        if (adapter.getItemCount() != 0) {
                            launchArrayList.clear();
                        }
                    }

                    @Override
                    protected void onPostExecute(List<Launch> result) {
            /* Download complete. Lets update UI */
                        if (result != null) {
                            List<Launch> thisList = result;
                            Log.d(LaunchApplication.TAG, "List Size: " + String.valueOf(thisList.size()) + " Adapter:" + adapter.getItemCount());
                            adapter.addItems(thisList);
                            mModels = thisList;
                            adapter.notifyDataSetChanged();
                        } else Log.e(LaunchApplication.TAG, "Failed to fetch data!");
                    }
                };
                loader.execute(CurrentURL + "&offset=" + current_page);
            }
        });

                /*Set up Pull to refresh*/
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        ((MainActivity) getActivity()).setActionBarTitle("Previous Launches");

        return view;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String daydatestart = dayOfMonth < 10 ? "0"+dayOfMonth : ""+dayOfMonth;
        String monthdatestart = monthOfYear < 10 ? "0"+monthOfYear : ""+monthOfYear;
        String daydateend = dayOfMonthEnd < 10 ? "0"+dayOfMonthEnd : ""+dayOfMonthEnd;
        String monthdayend = monthOfYearEnd < 10 ? "0"+monthOfYearEnd : ""+monthOfYearEnd;

        String fromdate = year + "-" + monthdatestart + "-" + daydatestart;
        String todate = yearEnd + "-" + monthdayend + "-" + daydateend;
        CurrentURL = String.format("https://launchlibrary.net/1.1.1/launch/%s/%s?sort=desc&limit=20", fromdate, todate);

        PreviousLaunchLoader loader = new PreviousLaunchLoader() {
            @Override
            protected void onPreExecute() {
                launchArrayList = new ArrayList<>();
                if (adapter.getItemCount() != 0) {
                    adapter.removeAll();
                }
            }

            @Override
            protected void onPostExecute(List<Launch> result) {
            /* Download complete. Lets update UI */
                if (result != null) {
                    List<Launch> thisList = result;
                    adapter.removeAll();
                    adapter.addItems(thisList);
                    mModels = thisList;
                    adapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(0);
                } else Log.e(LaunchApplication.TAG, "Failed to fetch data!");
            }
        };
        loader.execute(CurrentURL);
        Log.d("The Jones Theory", CurrentURL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_search, menu);

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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        final List<Launch> filteredModelList = filter(mModels, query);
        adapter.animateTo(filteredModelList);
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
            if (rocketName.contains(query) || locationName.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onResume() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        super.onResume();
        if (mRecyclerView.getAdapter() == null) {
            PreviousLaunchLoader loader = new PreviousLaunchLoader() {
                @Override
                protected void onPreExecute() {
                    launchArrayList = new ArrayList<>();
                    CircularProgressView progressView = (CircularProgressView) view.findViewById(R.id.progress_View);
                    progressView.setVisibility(View.VISIBLE);
                    progressView.startAnimation();
                }

                @Override
                protected void onPostExecute(List<Launch> result) {
            /* Download complete. Lets update UI */
                    if (result != null) {
                        List<Launch> thisList = result;
                        Log.d(LaunchApplication.TAG, "PreviousLaunchFragment" + result.get(0).getName() + " " + result.size());
                        CircularProgressView progressView = (CircularProgressView) view.findViewById(R.id.progress_View);
                        progressView.setVisibility(View.GONE);
                        adapter = new PreviousLaunchAdapter(getActivity(), thisList);
                        adapter.addItems(thisList);
                        mModels = thisList;
                        mRecyclerView.setAdapter(adapter);
                    } else Log.e(LaunchApplication.TAG, "Failed to fetch data!");
                }
            };
            loader.execute(String.format("https://launchlibrary.net/1.1.1/launch/1990-01-01/%s?sort=desc&limit=20", String.valueOf(formattedDate)));
        }
    }

    @Override
    public void onRefresh() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        PreviousLaunchLoader loader = new PreviousLaunchLoader() {
            @Override
            protected void onPreExecute() {
                launchArrayList = new ArrayList<>();
                if (adapter.getItemCount() != 0) {
                    adapter.removeAll();
                }
            }

            @Override
            protected void onPostExecute(List<Launch> result) {
            /* Download complete. Lets update UI */
                if (result != null) {
                    List<Launch> thisList = result;
                    mSwipeRefreshLayout.setRefreshing(false);
                    CircularProgressView progressView = (CircularProgressView) view.findViewById(R.id.progress_View);
                    progressView.setVisibility(View.GONE);
                    adapter.removeAll();
                    adapter.addItems(thisList);
                    mModels = thisList;
                    adapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(0);
                } else Log.e(LaunchApplication.TAG, "Failed to fetch data!");
            }
        };
        loader.execute(String.format("https://launchlibrary.net/1.1.1/launch/1990-01-01/%s?sort=desc&limit=20", String.valueOf(formattedDate)));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("curChoice", layoutManager.onSaveInstanceState());
    }
}
