package me.calebjones.spacelaunchnow.ui.fragment.vehicles;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.VehicleAdapter;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.Launcher;
import me.calebjones.spacelaunchnow.content.models.LauncherResponse;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.ui.activity.LauncherDetailActivity;
import me.calebjones.spacelaunchnow.utils.CustomFragment;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import me.calebjones.spacelaunchnow.utils.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class LauncherFragment extends CustomFragment {

    private ListPreferences sharedPreference;
    private VehicleAdapter adapter;
    private android.content.SharedPreferences SharedPreferences;
    private GridLayoutManager layoutManager;
    private List<Launcher> items = new ArrayList<>();
    private Context context;
    private View view;
    private RecyclerView mRecyclerView;
    public SparseArray<Bitmap> photoCache = new SparseArray<Bitmap>(1);
    private int defaultBackgroundcolor;
    private static final int SCALE_DELAY = 30;

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
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
        } else {
            layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(recyclerRowClickListener);
        loadJSON();
        return view;
    }

    private void loadJSON(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Strings.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<LauncherResponse> call = request.getLaunchers();
        call.enqueue(new Callback<LauncherResponse>() {
            @Override
            public void onResponse(Call<LauncherResponse> call, Response<LauncherResponse> response) {

                LauncherResponse jsonResponse = response.body();
                items = new ArrayList<>(Arrays.asList(jsonResponse.getItem()));
                adapter.addItems(items);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<LauncherResponse> call, Throwable t) {
                Timber.e(t.getMessage());
            }
        });
    }

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {

            Gson gson = new Gson();
            String jsonItem = gson.toJson(items.get(position));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Timber.d("Starting Activity at %s", position);

                Intent detailIntent = new Intent(getActivity(), LauncherDetailActivity.class);
                detailIntent.putExtra("position", position);
                detailIntent.putExtra("family", items.get(position).getName());
                detailIntent.putExtra("agency", items.get(position).getAgency());
                detailIntent.putExtra("json", jsonItem);

                ImageView coverImage = (ImageView) v.findViewById(R.id.picture);
                ((ViewGroup) coverImage.getParent()).setTransitionGroup(false);
                photoCache.put(position, coverImage.getDrawingCache());

                // Setup the transition to the detail activity
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        new Pair<View, String>(coverImage, "cover" + position));

                startActivity(detailIntent, options.toBundle());
            } else {
                Intent intent = new Intent(getActivity(), LauncherDetailActivity.class);
                intent.putExtra("family", items.get(position).getName());
                intent.putExtra("agency", items.get(position).getAgency());
                intent.putExtra("json", jsonItem);
                startActivity(intent);
            }
        }
    };
}
