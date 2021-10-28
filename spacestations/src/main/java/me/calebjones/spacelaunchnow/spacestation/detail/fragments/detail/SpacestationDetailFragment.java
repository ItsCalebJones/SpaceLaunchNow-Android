package me.calebjones.spacelaunchnow.spacestation.detail.fragments.detail;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.prefs.ThemeHelper;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.spacestation.R;
import me.calebjones.spacelaunchnow.spacestation.R2;
import me.calebjones.spacelaunchnow.spacestation.detail.SpacestationDetailViewModel;

public class SpacestationDetailFragment extends BaseFragment {

    @BindView(R2.id.spacestation_detail_title)
    TextView spacestationDetailTitle;
    @BindView(R2.id.spacestaion_detail_subtitle)
    TextView spacestaionDetailSubtitle;
    @BindView(R2.id.specs)
    RecyclerView specsRecyclerView;
    @BindView(R2.id.description)
    TextView descriptionView;
    @BindView(R2.id.founded)
    TextView foundedView;
    @BindView(R2.id.deorbited)
    TextView deorbitedView;
    @BindView(R2.id.card_view)
    CardView cardView;
    @BindView(R2.id.owners_recycler)
    RecyclerView ownersRecycler;
    @BindView(R2.id.spacestation_owner_title)
    TextView spacestationOwnerTitle;
    @BindView(R2.id.spacestaion_owner_subtitle)
    TextView spacestaionOwnerSubtitle;
    private SpacestationDetailViewModel mViewModel;
    private SpacestationSpecAdapter adapter;
    private SpacestationOwnerAdapter ownerAdapter;
    private Context context;
    private Unbinder unbinder;
    private GridLayoutManager layoutManager;

    public static SpacestationDetailFragment newInstance() {
        return new SpacestationDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spacestation_detail_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new SpacestationSpecAdapter();
        ownerAdapter = new SpacestationOwnerAdapter(context);
        layoutManager = new GridLayoutManager(context, 2);
        ownersRecycler.setLayoutManager(new LinearLayoutManager(context));
        ownersRecycler.setAdapter(ownerAdapter);
        ownersRecycler.addItemDecoration(new SimpleDividerItemDecoration(context));
        specsRecyclerView.setLayoutManager(layoutManager);
        specsRecyclerView.setAdapter(adapter);
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

        if (spacestation.getDeorbited() != null) {
            deorbitedView.setVisibility(View.VISIBLE);
            String deorbited = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy").format(spacestation.getDeorbited());
            deorbitedView.setText(String.format(getString(R.string.deorbited), deorbited));
        } else {
            deorbitedView.setVisibility(View.GONE);
        }

        if (spacestation.getFounded() != null) {
            foundedView.setVisibility(View.VISIBLE);
            String founded = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy").format(spacestation.getFounded());
            foundedView.setText(String.format(getString(R.string.founded), founded));
        } else {
            foundedView.setVisibility(View.GONE);
        }

        spacestaionDetailSubtitle.setText(String.format(getString(R.string.status), spacestation.getStatus().getName()));
        descriptionView.setText(spacestation.getDescription());
        int color = ThemeHelper.getIconColor(getActivity());
        List<SpacestationSpecItem> specs = new ArrayList<>();
        if (spacestation.getHeight() != null) {
            specs.add(new SpacestationSpecItem(getString(R.string.height_plain),
                    String.format(getString(R.string.height_unit), spacestation.getHeight().toString()),
                    new IconicsDrawable(context)
                            .color(color)
                            .icon(GoogleMaterial.Icon.gmd_swap_vert)
                            .sizeDp(24)));
        }

        if (spacestation.getWidth() != null) {
            specs.add(new SpacestationSpecItem(getString(R.string.width_plain),
                    String.format(getString(R.string.width_unit), spacestation.getWidth().toString()),
                    new IconicsDrawable(context)
                            .color(color)
                            .icon(GoogleMaterial.Icon.gmd_swap_horiz)
                            .sizeDp(24)));
        }

        if (spacestation.getMass() != null) {
            specs.add(new SpacestationSpecItem(getString(R.string.mass_plain),
                    String.format(getString(R.string.mass_unit), spacestation.getMass().toString()),
                    new IconicsDrawable(context)
                            .color(color)
                            .icon(FontAwesome.Icon.faw_weight)
                            .sizeDp(24)));
        }

        if (spacestation.getVolume() != null) {
            specs.add(new SpacestationSpecItem(getString(R.string.volume_plain),
                    String.format(getString(R.string.volume_unit), spacestation.getVolume().toString()),
                    new IconicsDrawable(context)
                            .color(color)
                            .icon(FontAwesome.Icon.faw_cubes)
                            .sizeDp(24)));
        }
        if (spacestation.getVolume() != null) {
            specs.add(new SpacestationSpecItem(getString(R.string.crew_plain),
                    String.format("%s", spacestation.getOnboardCrew()),
                    new IconicsDrawable(context)
                            .color(color)
                            .icon(FontAwesome.Icon.faw_user_astronaut)
                            .sizeDp(24)));
        }
        if (spacestation.getVolume() != null) {
            specs.add(new SpacestationSpecItem(getString(R.string.orbit),
                    String.format("%s", spacestation.getOrbit()),
                    new IconicsDrawable(context)
                            .color(color)
                            .icon(FontAwesome.Icon.faw_globe)
                            .sizeDp(24)));
        }
        adapter.addItems(specs);
        ownerAdapter.addItems(spacestation.getOwners());
        spacestationDetailTitle.setText(String.format(getString(R.string.details_fill), spacestation.getName()));
        spacestaionOwnerSubtitle.setText(String.format(getString(R.string.total_fill), spacestation.getOwners().size()));

    }
}
