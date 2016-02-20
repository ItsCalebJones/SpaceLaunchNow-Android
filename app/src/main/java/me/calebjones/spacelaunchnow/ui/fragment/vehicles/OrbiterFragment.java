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

import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.OrbiterAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.GridItem;
import me.calebjones.spacelaunchnow.ui.activity.OrbiterDetailActivity;
import me.calebjones.spacelaunchnow.utils.CustomFragment;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import timber.log.Timber;

public class OrbiterFragment extends CustomFragment {

    private SharedPreference sharedPreference;
    private android.content.SharedPreferences SharedPreferences;
    private Context context;
    private View view;
    private OrbiterAdapter adapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager layoutManager;
    private List<GridItem> items = new ArrayList<GridItem>();
    public static SparseArray<Bitmap> photoCache = new SparseArray<Bitmap>(1);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = SharedPreference.getInstance(getContext());
        adapter = new OrbiterAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int m_theme;

        this.context = getActivity().getApplicationContext();

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

        items.add(new GridItem("Soyuz", "Russian Federal Space Agency ","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944174/soyuz_snfim6.jpg"));
        items.add(new GridItem("Shenzhou", "Chinese National Manned Space Program","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944173/shenzhou_vzayjm.jpg"));
        items.add(new GridItem("Dragon", "SpaceX","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944174/dragon_q9cxq9.jpg"));
        items.add(new GridItem("Orion", "National Aeronautics and Space Administration (NASA)","http://res.cloudinary.com/dnkkbfy3m/image/upload/v1454944173/orion_sgl9rs.jpg"));

        adapter.addItems(items);
        return view;
    }

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Timber.d("Starting Activity at %s", position);

                Intent detailIntent = new Intent(getActivity(), OrbiterDetailActivity.class);
                detailIntent.putExtra("position", position + 1);
                detailIntent.putExtra("family", items.get(position).getName());
                detailIntent.putExtra("agency", items.get(position).getAgency());

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
                startActivity(intent);
            }
        }
    };

}
