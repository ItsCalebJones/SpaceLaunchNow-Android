package me.calebjones.spacelaunchnow.wear.launch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.wear.R;

public class LaunchViewHolder extends RecyclerView.ViewHolder {

    private LaunchAdapter launchAdapter;

    @BindView(R.id.launch_icon)
    ImageView launchIcon;
    @BindView(R.id.launch_name)
    TextView launchName;
    @BindView(R.id.launch_parent)
    View launchParent;

    public LaunchViewHolder(View view, LaunchAdapter launchAdapter) {
        super(view);
        ButterKnife.bind(this, view);
        this.launchAdapter = launchAdapter;
    }

    @OnClick(R.id.launch_parent)
    void launchClicked(){
        launchAdapter.onClick(getAdapterPosition());
    }

}
