package me.calebjones.spacelaunchnow.ui.fragment.vehicles;


import android.app.ActivityOptions;
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
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.OrbiterAdapter;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.natives.Orbiter;
import me.calebjones.spacelaunchnow.content.responses.base.OrbiterResponse;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.ui.activity.OrbiterDetailActivity;
import me.calebjones.spacelaunchnow.ui.fragment.CustomFragment;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import me.calebjones.spacelaunchnow.content.interfaces.APIRequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class OrbiterFragment extends CustomFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListPreferences sharedPreference;
    private android.content.SharedPreferences SharedPreferences;
    private Context context;
    private View view;
    private OrbiterAdapter adapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager layoutManager;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Orbiter> items = new ArrayList<Orbiter>();
    public static SparseArray<Bitmap> photoCache = new SparseArray<Bitmap>(1);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = ListPreferences.getInstance(getContext());
        adapter = new OrbiterAdapter(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int m_theme;

        this.context = getActivity().getApplicationContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.DarkTheme_NoActionBar;
        } else {
            m_theme = R.style.LightTheme_NoActionBar;
        }
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        Context context = new ContextThemeWrapper(getActivity().getApplicationContext(), m_theme);
        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(context);

        super.onCreateView(inflater, container, savedInstanceState);

        LayoutInflater lf = getActivity().getLayoutInflater();
        view = lf.inflate(R.layout.fragment_launch_vehicles, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.gridview);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.vehicle_coordinator);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
        } else {
            layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
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
        loadJSON();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void loadJSON(){
        Timber.v("Loading orbiters...");
        showLoading();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Strings.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIRequestInterface request = retrofit.create(APIRequestInterface.class);
        Call<OrbiterResponse> call = request.getOrbiter();
        call.enqueue(new Callback<OrbiterResponse>() {
            @Override
            public void onResponse(Call<OrbiterResponse> call, Response<OrbiterResponse> response) {
                if (response.isSuccessful()) {
                    OrbiterResponse jsonResponse = response.body();
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
            public void onFailure(Call<OrbiterResponse> call, Throwable t) {
                Timber.e(t.getMessage());
                hideLoading();
                Snackbar.make(coordinatorLayout, t.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void hideLoading(){
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showLoading(){
        if (!swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {

            Gson gson = new Gson();
            String jsonItem = gson.toJson(items.get(position));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Timber.d("Starting Activity at %s", position);

                Intent detailIntent = new Intent(getActivity(), OrbiterDetailActivity.class);
                detailIntent.putExtra("position", position + 1);
                detailIntent.putExtra("family", items.get(position).getName());
                detailIntent.putExtra("agency", items.get(position).getAgency());
                detailIntent.putExtra("json", jsonItem);

                ImageView coverImage = (ImageView) v.findViewById(R.id.picture);
                ((ViewGroup) coverImage.getParent()).setTransitionGroup(false);
                photoCache.put(position, coverImage.getDrawingCache());

                // Setup the transition to the detail activity
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        new Pair<View, String>(coverImage, "cover" + position + 1));

                startActivity(detailIntent, options.toBundle());
            } else {
                Intent intent = new Intent(getActivity(), OrbiterDetailActivity.class);
                intent.putExtra("family", items.get(position).getName());
                intent.putExtra("agency", items.get(position).getAgency());
                intent.putExtra("json", jsonItem);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onRefresh() {
        loadJSON();
    }

}
