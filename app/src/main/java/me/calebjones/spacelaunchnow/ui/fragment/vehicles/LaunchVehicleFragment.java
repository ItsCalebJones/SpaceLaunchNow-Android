package me.calebjones.spacelaunchnow.ui.fragment.vehicles;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.VehicleAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.GridItem;
import me.calebjones.spacelaunchnow.ui.activity.VehicleDetailActivity;
import me.calebjones.spacelaunchnow.utils.CustomFragment;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import timber.log.Timber;

public class LaunchVehicleFragment extends CustomFragment {

    private SharedPreference sharedPreference;
    private VehicleAdapter adapter;
    private android.content.SharedPreferences SharedPreferences;
    private GridLayoutManager layoutManager;
    private List<GridItem> items = new ArrayList<GridItem>();
    private Context context;
    private View view;
    private RecyclerView mRecyclerView;
    public SparseArray<Bitmap> photoCache = new SparseArray<Bitmap>(1);
    private int defaultBackgroundcolor;
    private static final int SCALE_DELAY = 30;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = SharedPreference.getInstance(getActivity().getApplication());
        adapter = new VehicleAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int m_theme;
        context = getActivity().getApplication();

        sharedPreference = SharedPreference.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            m_theme = R.style.DarkTheme_NoActionBar;
        } else {
            m_theme = R.style.LightTheme_NoActionBar;
        }
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        Context context = new ContextThemeWrapper(getActivity(), m_theme);
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
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        adapter.setOnItemClickListener(recyclerRowClickListener);

        items.add(new GridItem("Soyuz", "Roscosmos","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944167/soyuz_rjwr7s.jpg"));
        items.add(new GridItem("Falcon", "SpaceX", "http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944166/falcon_xtbyia.jpg"));
        items.add(new GridItem("Proton", "Khrunichev" ,"http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944166/proton_yaojbm.jpg"));
        items.add(new GridItem("Delta", "United Launch Alliance" ,"http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944166/delta_lbwmgy.jpg"));
        items.add(new GridItem("Ariane", "Ariancespace & ESA", "http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944166/ariane_qouki7.jpg"));
        items.add(new GridItem("Space Shuttle", "NASA","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944167/shuttle_vc2xnw.jpg"));
        items.add(new GridItem("Long March", "China Academy of Space Technology","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944166/long_ywtbw6.jpg"));
        items.add(new GridItem("Atlas", "Lockheed Martin","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944166/atlas_ahqjya.jpg"));
        items.add(new GridItem("PSLV", "Indian Space Research Organisation","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944167/pslv_vjcdww.jpg"));
        items.add(new GridItem("Vega", "Arianespace","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944167/vega_tnkdms.jpg"));
        items.add(new GridItem("Zenit", "Yuzhnoye Design Bureau","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944167/zenit_hxl4gd.jpg"));

        adapter.addItems(items);

        return view;
    }

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Timber.d("Starting Activity at %s", position);

                Intent detailIntent = new Intent(getActivity(), VehicleDetailActivity.class);
                detailIntent.putExtra("position", position);
                detailIntent.putExtra("family", items.get(position).getName());
                detailIntent.putExtra("agency", items.get(position).getAgency());

                ImageView coverImage = (ImageView) v.findViewById(R.id.picture);
                ((ViewGroup) coverImage.getParent()).setTransitionGroup(false);
                photoCache.put(position, coverImage.getDrawingCache());

                // Setup the transition to the detail activity
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        new Pair<View, String>(coverImage, "cover" + position));

                startActivity(detailIntent, options.toBundle());
            } else {
                Intent intent = new Intent(getActivity(), VehicleDetailActivity.class);
                intent.putExtra("family", items.get(position).getName());
                intent.putExtra("agency", items.get(position).getAgency());
                startActivity(intent);
            }
        }
    };
}
