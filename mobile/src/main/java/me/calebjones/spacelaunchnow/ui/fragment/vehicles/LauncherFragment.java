package me.calebjones.spacelaunchnow.ui.fragment.vehicles;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.VehicleAdapter;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.interfaces.APIRequestInterface;
import me.calebjones.spacelaunchnow.content.models.natives.Launcher;
import me.calebjones.spacelaunchnow.content.responses.base.LauncherResponse;
import me.calebjones.spacelaunchnow.ui.activity.LauncherDetailActivity;
import me.calebjones.spacelaunchnow.ui.fragment.CustomFragment;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LauncherFragment extends CustomFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListPreferences sharedPreference;
    private VehicleAdapter adapter;
    private android.content.SharedPreferences SharedPreferences;
    private GridLayoutManager layoutManager;
    private List<Launcher> items = new ArrayList<>();
    private static Context context;
    private View view;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    public SparseArray<Bitmap> photoCache = new SparseArray<Bitmap>(1);
    // The singleton HTTP client.
    public final OkHttpClient client = new OkHttpClient.Builder().build();
    private int defaultBackgroundcolor;
    private static final int SCALE_DELAY = 30;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = ListPreferences.getInstance(getActivity().getApplication());
        adapter = new VehicleAdapter(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int m_theme;
        context = getActivity().getApplicationContext();

        sharedPreference = ListPreferences.getInstance(context);

        m_theme = R.style.LightTheme_NoActionBar;
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        Context context = new ContextThemeWrapper(getActivity().getApplicationContext(), m_theme);
        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(context);

        super.onCreateView(inflater, container, savedInstanceState);

        LayoutInflater lf = getActivity().getLayoutInflater();
        view = lf.inflate(R.layout.fragment_launch_vehicles, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.vehicle_detail_list);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.vehicle_coordinator);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
        } else {
            layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        adapter.setOnItemClickListener(recyclerRowClickListener);
        mRecyclerView.setAdapter(adapter);
        Timber.v("Returning view.");
        return view;
    }

    @Override
    public void onResume() {
        Timber.v("onResume");
        loadJSON();
        super.onResume();
    }

    private void loadJSON() {
        Timber.v("Loading vehicles...");
        showLoading();

        APIRequestInterface request = getRetrofit().create(APIRequestInterface.class);
        Call<LauncherResponse> call = request.getLaunchers();
        call.enqueue(new Callback<LauncherResponse>() {
            @Override
            public void onResponse(Call<LauncherResponse> call, Response<LauncherResponse> response) {
                if (response.isSuccessful()) {
                    LauncherResponse jsonResponse = response.body();
                    items = new ArrayList<>(Arrays.asList(jsonResponse.getItem()));
                    adapter.addItems(items);
                } else {
                    try {
                        onFailure(call, new Throwable(response.errorBody().string()));
                    } catch (IOException e) {
                        onFailure(call, e);
                    }
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<LauncherResponse> call, Throwable t) {
                Timber.e(t.getMessage());
                hideLoading();
                Snackbar.make(coordinatorLayout, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void hideLoading() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showLoading() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {

            Gson gson = new Gson();
            String jsonItem = gson.toJson(items.get(position));

            Intent intent = new Intent(getActivity(), LauncherDetailActivity.class);
            intent.putExtra("family", items.get(position).getName());
            intent.putExtra("agency", items.get(position).getAgency());
            intent.putExtra("json", jsonItem);
            startActivity(intent);
        }

    };

    @Override
    public void onRefresh() {
        loadJSON();
    }
}
