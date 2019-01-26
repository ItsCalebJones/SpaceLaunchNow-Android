package me.calebjones.spacelaunchnow.spacestation.detail.fragments.expeditions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.responses.base.ExpeditionResponse;
import me.calebjones.spacelaunchnow.spacestation.R;
import me.calebjones.spacelaunchnow.spacestation.R2;
import me.calebjones.spacelaunchnow.spacestation.detail.SpacestationDetailViewModel;
import me.calebjones.spacelaunchnow.spacestation.detail.adapter.ActiveExpeditionItem;
import me.calebjones.spacelaunchnow.spacestation.detail.adapter.ListItem;
import me.calebjones.spacelaunchnow.spacestation.detail.adapter.SpacestationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpacestationExpeditionFragment extends BaseFragment {

    @BindView(R2.id.launcher)
    CircleImageView launcher;
    @BindView(R2.id.textView)
    TextView textView;
    @BindView(R2.id.no_active_expeditions)
    CoordinatorLayout noActiveExpeditions;
    @BindView(R2.id.active_recycler_view)
    RecyclerView activeRecyclerView;
    @BindView(R2.id.spacestation_past_title)
    TextView spacestationPastTitle;
    @BindView(R2.id.spacestaion_past_subtitle)
    TextView spacestaionPastSubtitle;
    @BindView(R2.id.past_card_view)
    MaterialCardView pastCardView;
    @BindView(R2.id.past_expedition_recyclerview)
    RecyclerView pastExpeditionRecyclerview;
    private SpacestationDetailViewModel mViewModel;
    private Unbinder unbinder;
    private SpacestationAdapter adapter;
    private SpacestationAdapter pastAdapter;
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
        adapter = new SpacestationAdapter(context);
        activeRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        activeRecyclerView.setAdapter(adapter);
        pastAdapter = new SpacestationAdapter(context);
        pastExpeditionRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        pastExpeditionRecyclerview.setAdapter(pastAdapter);
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
        if (spacestation != null && spacestation.getActiveExpeditions() != null) {
            List<ListItem> items = new ArrayList<>();
            for (Expedition expedition : spacestation.getActiveExpeditions()) {
                ActiveExpeditionItem item = new ActiveExpeditionItem(expedition);
                items.add(item);
            }
            adapter.clear();
            adapter.addItems(items);
        }
        pastCardView.setVisibility(View.GONE);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        DataClient.getInstance().getExpeditions(100, 0, null, null, spacestation.getId(), formattedDate, new Callback<ExpeditionResponse>() {
            @Override
            public void onResponse(Call<ExpeditionResponse> call, Response<ExpeditionResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        List<Expedition> pastExpeditions = response.body().getExpeditions();
                        List<ListItem> items = new ArrayList<>();
                        for (Expedition expedition : pastExpeditions) {
                            ActiveExpeditionItem item = new ActiveExpeditionItem(expedition);
                            items.add(item);
                        }
                        if (items.size() > 0) {
                            pastAdapter.clear();
                            pastAdapter.addItems(items);
                            pastCardView.setVisibility(View.VISIBLE);
                        } else {
                            pastCardView.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ExpeditionResponse> call, Throwable t) {
                pastCardView.setVisibility(View.GONE);
            }
        });
    }
}
