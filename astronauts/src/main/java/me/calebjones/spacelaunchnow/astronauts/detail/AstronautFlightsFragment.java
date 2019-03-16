package me.calebjones.spacelaunchnow.astronauts.detail;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.ui.adapters.ListAdapter;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.spacelaunchnow.astronauts.R;
import me.spacelaunchnow.astronauts.R2;

public class AstronautFlightsFragment extends BaseFragment {

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;

    private AstronautDetailViewModel mViewModel;
    private Unbinder unbinder;
    private LinearLayoutManager linearLayoutManager;
    private ListAdapter adapter;
    private Context context;

    public static AstronautFlightsFragment newInstance() {
        return new AstronautFlightsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.astronaut_flight_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        linearLayoutManager = new LinearLayoutManager(context);
        adapter = new ListAdapter(context, getCyanea().isDark());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(AstronautDetailViewModel.class);
        // update UI
        mViewModel.getAstronaut().observe(this, this::setAstronaut);
    }

    private void setAstronaut(Astronaut astronaut) {
        if (astronaut != null && astronaut.getFlights() != null){
            adapter.clear();
            adapter.addItems(astronaut.getFlights());
        }
    }
}
