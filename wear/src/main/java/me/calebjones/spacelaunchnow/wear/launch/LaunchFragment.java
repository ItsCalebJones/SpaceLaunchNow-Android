package me.calebjones.spacelaunchnow.wear.launch;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.wear.R;
import me.calebjones.spacelaunchnow.wear.common.DividerItemDecoration;

public class LaunchFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private LaunchAdapter launchAdapter;

    public LaunchFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_launch, container, false);
        ButterKnife.bind(this, rootView);
        launchAdapter = new LaunchAdapter(getContext(), getArguments() != null ? getArguments().getInt("category") : 0);
        recyclerView.setAdapter(launchAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    public void updateCategories(int category) {
        launchAdapter.setCategory(category);
    }

}
