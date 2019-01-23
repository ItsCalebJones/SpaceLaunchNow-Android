package me.calebjones.spacelaunchnow.spacestation.detail.expeditions;

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
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.spacestation.R;
import me.calebjones.spacelaunchnow.spacestation.R2;
import me.calebjones.spacelaunchnow.spacestation.detail.SpacestationDetailViewModel;
import me.spacelaunchnow.astronauts.R;
import me.spacelaunchnow.astronauts.R2;

public class SpacestationExpeditionFragment extends BaseFragment {

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;

    private SpacestationDetailViewModel mViewModel;
    private Unbinder unbinder;
    private LinearLayoutManager linearLayoutManager;
    private ExpeditionAdapter adapter;
    private Context context;

    public static SpacestationExpeditionFragment newInstance() {
        return new SpacestationExpeditionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spacestation_expedition_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        linearLayoutManager = new LinearLayoutManager(context);
        adapter = new ExpeditionAdapter(context, getCyanea().isDark());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setAdapter(adapter);
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
        if (spacestation != null && spacestation.getActiveExpeditions() != null){
            adapter.clear();
            adapter.addItems(spacestation.getActiveExpeditions());
        }
    }
}
