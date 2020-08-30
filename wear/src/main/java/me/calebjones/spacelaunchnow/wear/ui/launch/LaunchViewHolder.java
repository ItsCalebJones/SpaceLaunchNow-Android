package me.calebjones.spacelaunchnow.wear.ui.launch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.wear.R;

public class LaunchViewHolder extends RecyclerView.ViewHolder {

    private LaunchAdapter launchAdapter;

    @BindView(R.id.categoryIcon)
    ImageView launchIcon;
    @BindView(R.id.launch_rocket)
    TextView launchRocket;
    @BindView(R.id.mission)
    TextView launchMission;
    @BindView(R.id.launch_date)
    TextView launchDate;
    @BindView(R.id.rootview)
    View rootView;

    public LaunchViewHolder(View view, LaunchAdapter launchAdapter) {
        super(view);
        ButterKnife.bind(this, view);
        this.launchAdapter = launchAdapter;
    }

    @OnClick(R.id.rootview)
    void launchClicked(){
        launchAdapter.onClick(getAdapterPosition());
    }

}
