package me.calebjones.spacelaunchnow.ui.launcher;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.ui.imageviewer.FullscreenImageActivity;
import me.calebjones.spacelaunchnow.ui.launches.launcher.LauncherLaunchActivity;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;

public class VehicleDetailAdapter extends RecyclerView.Adapter<VehicleDetailAdapter.ViewHolder> {

    public int position;
    private Context mContext;
    private Activity activity;
    private List<LauncherConfig> items;
    private RequestOptions requestOptions;
    private int backgroundColor = 0;

    public VehicleDetailAdapter(Context context, Activity activity) {
        items = new ArrayList<>();
        requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .centerCrop();
        mContext = context;
        this.activity = activity;
    }

    public void addItems(List<LauncherConfig> items) {
        if (this.items != null) {
            this.items.addAll(items);
        } else {
            this.items = new RealmList<>();
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vehicle_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        LauncherConfig launchVehicle = items.get(holder.getAdapterPosition());

        if (launchVehicle != null) {

            holder.launchesButton.setText(String.format(mContext.getString(R.string.view_rocket_launches), launchVehicle.getFullName()));
            holder.vehicleSpecView.setVisibility(View.VISIBLE);
            if (launchVehicle.getDescription() != null && launchVehicle.getDescription().length() > 0) {
                holder.vehicleDescription.setVisibility(View.VISIBLE);
                holder.vehicleDescription.setText(launchVehicle.getDescription());
            } else {
                holder.vehicleDescription.setVisibility(View.GONE);
            }

            if (launchVehicle.getLength() != null) {
                holder.vehicleSpecsHeight.setText(String.format(mContext.getString(R.string.height_full), launchVehicle.getLength()));
            } else {
                holder.vehicleSpecsHeight.setText(mContext.getString(R.string.height));
            }

            if (launchVehicle.getDiameter() != null) {
                holder.vehicleSpecsDiameter.setText(String.format(mContext.getString(R.string.diameter_full), launchVehicle.getDiameter()));
            } else {
                holder.vehicleSpecsDiameter.setText(mContext.getString(R.string.diameter));
            }

            if (launchVehicle.getMaxStage() != null) {
                holder.vehicleSpecsStages.setText(String.format(mContext.getString(R.string.stage_full), launchVehicle.getMaxStage()));
            } else {
                holder.vehicleSpecsStages.setText(mContext.getString(R.string.stages));
            }

            if (launchVehicle.getLeoCapacity() != null) {
                holder.vehicleSpecsLeo.setText(String.format(mContext.getString(R.string.mass_leo_full), launchVehicle.getLeoCapacity()));
            } else {
                holder.vehicleSpecsLeo.setText(mContext.getString(R.string.mass_to_leo));
            }

            if (launchVehicle.getGtoCapacity() != null) {
                holder.vehicleSpecsGto.setText(String.format(mContext.getString(R.string.mass_gto_full), launchVehicle.getGtoCapacity()));
            } else {
                holder.vehicleSpecsGto.setText(mContext.getString(R.string.mass_to_gto));
            }

            if (launchVehicle.getLaunchMass() != null) {
                holder.vehicleSpecsLaunchMass.setText(String.format(mContext.getString(R.string.mass_launch_full), launchVehicle.getLaunchMass()));
            } else {
                holder.vehicleSpecsLaunchMass.setText(mContext.getString(R.string.mass_at_launch));
            }

            if (launchVehicle.getToThrust() != null) {
                holder.vehicleSpecsThrust.setText(String.format(mContext.getString(R.string.thrust_full), launchVehicle.getToThrust()));
            } else {
                holder.vehicleSpecsThrust.setText(mContext.getString(R.string.height));
            }


            if (backgroundColor != 0) {
                holder.vehicleName.setBackgroundColor(backgroundColor);
                holder.vehicleFamily.setBackgroundColor(backgroundColor);
            }
            if (launchVehicle.getImageUrl() != null && launchVehicle.getImageUrl().length() > 0) {
                holder.vehicleImage.setVisibility(View.VISIBLE);
                GlideApp.with(mContext)
                        .load(launchVehicle.getImageUrl())
                        .apply(requestOptions)
                        .into(holder.vehicleImage);
            } else {

                holder.vehicleImage.setVisibility(View.GONE);
            }
            holder.vehicleName.setText(launchVehicle.getFullName());
            holder.vehicleFamily.setText(launchVehicle.getFamily());

            if (launchVehicle.getInfoUrl() != null
                    && launchVehicle.getInfoUrl().length() > 0
                    && !launchVehicle.getInfoUrl().contains("null")) {
                holder.infoButton.setVisibility(View.VISIBLE);
            } else {
                holder.infoButton.setVisibility(View.GONE);
            }


            if (launchVehicle.getWikiUrl() != null
                    && launchVehicle.getWikiUrl().length() > 0
                    && !launchVehicle.getWikiUrl().contains("null")) {
                holder.wikiButton.setVisibility(View.VISIBLE);
            } else {
                holder.wikiButton.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateColor(int color) {

        backgroundColor = color;

        backgroundColor = color;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageView)
        ImageView vehicleImage;
        @BindView(R.id.launch_vehicle_title)
        TextView vehicleName;
        @BindView(R.id.launch_vehicle)
        TextView vehicleFamily;
        @BindView(R.id.launch_vehicle_description)
        TextView vehicleDescription;

        @BindView(R.id.launch_vehicle_specs_height)
        TextView vehicleSpecsHeight;
        @BindView(R.id.launch_vehicle_specs_diameter)
        TextView vehicleSpecsDiameter;
        @BindView(R.id.launch_vehicle_specs_stages)
        TextView vehicleSpecsStages;
        @BindView(R.id.launch_vehicle_specs_leo)
        TextView vehicleSpecsLeo;
        @BindView(R.id.launch_vehicle_specs_gto)
        TextView vehicleSpecsGto;
        @BindView(R.id.launch_vehicle_specs_launch_mass)
        TextView vehicleSpecsLaunchMass;
        @BindView(R.id.launch_vehicle_specs_thrust)
        TextView vehicleSpecsThrust;
        @BindView(R.id.vehicle_infoButton)
        AppCompatButton infoButton;
        @BindView(R.id.vehicle_wikiButton)
        AppCompatButton wikiButton;
        @BindView(R.id.vehicle_spec_view)
        Group vehicleSpecView;
        @BindView(R.id.launcher_launches)
        AppCompatButton launchesButton;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

            launchesButton.setOnClickListener(this);
            infoButton.setOnClickListener(this);
            wikiButton.setOnClickListener(this);
            vehicleImage.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.infoButton:
                    Utils.openCustomTab(activity, mContext, items.get(position).getInfoUrl());
                    break;
                case R.id.wikiButton:
                    Utils.openCustomTab(activity, mContext, items.get(position).getWikiUrl());
                    break;
                case R.id.imageView:
                    Intent animateIntent = new Intent(activity, FullscreenImageActivity.class);
                    animateIntent.putExtra("imageURL", items.get(position).getImageUrl());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        activity.startActivity(animateIntent, ActivityOptions.makeSceneTransitionAnimation(activity, vehicleImage, "imageCover").toBundle());
                    } else {
                        activity.startActivity(animateIntent);
                    }
                    break;
                case R.id.launcher_launches:
                    Intent launches = new Intent(activity, LauncherLaunchActivity.class);
                    launches.putExtra("launcherId", items.get(position).getId());
                    launches.putExtra("launcherName", items.get(position).getName());
                    activity.startActivity(launches);
            }
        }
    }
}
