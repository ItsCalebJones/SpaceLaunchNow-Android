package me.calebjones.spacelaunchnow.spacestation.detail.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;
import me.calebjones.spacelaunchnow.spacestation.R2;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.spacestation.R;
import me.calebjones.spacelaunchnow.spacestation.detail.SpacestationDetailViewModel;
import me.calebjones.spacelaunchnow.spacestation.detail.adapter.DockedVehicleItem;
import me.calebjones.spacelaunchnow.spacestation.detail.adapter.ListItem;
import me.calebjones.spacelaunchnow.spacestation.detail.adapter.SpacestationAdapter;

public class SpacestationDockedVehiclesFragment extends BaseFragment {

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.stateful_view)
    SimpleStatefulLayout simpleStatefulLayout;

    private SpacestationDetailViewModel mViewModel;
    private Unbinder unbinder;
    private LinearLayoutManager linearLayoutManager;
    private SpacestationAdapter adapter;
    private Context context;

    public static SpacestationDockedVehiclesFragment newInstance() {
        return new SpacestationDockedVehiclesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spacestation_docking_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        linearLayoutManager = new LinearLayoutManager(context);
        adapter = new SpacestationAdapter(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        simpleStatefulLayout.showProgress();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(SpacestationDetailViewModel.class);
        // update UI
        mViewModel.getSpacestation().observe(this, this::setSpacestation);
    }

    private void setSpacestation(Spacestation spacestation) {
        if (spacestation != null && spacestation.getDockedVehicles() != null) {
            List<ListItem> items = new ArrayList<>();
            for (SpacecraftStage spacecraftStage : spacestation.getDockedVehicles()) {
                DockedVehicleItem item = new DockedVehicleItem(spacecraftStage);
                items.add(item);
            }
            adapter.clear();
            adapter.addItems(items);
        }
        if (adapter.getItemCount() > 0) {
            simpleStatefulLayout.showContent();
        } else {
            simpleStatefulLayout.showEmpty();
        }
    }
}
