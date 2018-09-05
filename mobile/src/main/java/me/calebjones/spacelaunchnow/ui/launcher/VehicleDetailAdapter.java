package me.calebjones.spacelaunchnow.ui.launcher;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.main.LauncherConfig;
import me.calebjones.spacelaunchnow.ui.imageviewer.FullscreenImageActivity;
import me.calebjones.spacelaunchnow.ui.launches.launcher.LauncherLaunchActivity;
import me.calebjones.spacelaunchnow.utils.GlideApp;
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
                holder.vehicleSpecsHeight.setText(mContext.getString(R.string.height));
            }


            if (backgroundColor != 0) {
                holder.titleContainer.setBackgroundColor(backgroundColor);
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

            if (holder.infoButton.getVisibility() == View.GONE && holder.wikiButton.getVisibility() == View.GONE) {
                holder.button_layout.setVisibility(View.GONE);
            } else {
                holder.button_layout.setVisibility(View.VISIBLE);
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
        public ImageView vehicleImage;
        public View vehicleSpecView, vehicleContainer, button_layout;
        public TextView vehicleName;
        public TextView vehicleFamily;
        public TextView vehicleSpecsHeight;
        public TextView vehicleSpecsDiameter;
        public TextView vehicleSpecsStages;
        public TextView vehicleSpecsLeo;
        public TextView vehicleSpecsGto;
        public TextView vehicleSpecsLaunchMass;
        public TextView vehicleSpecsThrust;
        public View titleContainer;
        public AppCompatButton infoButton;
        public AppCompatButton wikiButton;
        public TextView vehicleDescription;
        public AppCompatButton launchesButton;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            vehicleContainer = view.findViewById(R.id.vehicle_container);
            vehicleSpecView = view.findViewById(R.id.vehicle_spec_view);
            vehicleImage = view.findViewById(R.id.item_icon);
            vehicleName = view.findViewById(R.id.item_title);
            vehicleFamily = view.findViewById(R.id.text_subtitle);
            vehicleSpecsStages = view.findViewById(R.id.launch_vehicle_specs_stages);
            vehicleSpecsHeight = view.findViewById(R.id.launch_vehicle_specs_height);
            vehicleSpecsDiameter = view.findViewById(R.id.launch_vehicle_specs_diameter);
            vehicleSpecsLeo = view.findViewById(R.id.launch_vehicle_specs_leo);
            vehicleSpecsGto = view.findViewById(R.id.launch_vehicle_specs_gto);
            vehicleSpecsLaunchMass = view.findViewById(R.id.launch_vehicle_specs_launch_mass);
            vehicleSpecsThrust = view.findViewById(R.id.launch_vehicle_specs_thrust);
            vehicleDescription = view.findViewById(R.id.launch_vehicle_description);
            button_layout = view.findViewById(R.id.button_layout);
            titleContainer = view.findViewById(R.id.text_container);
            infoButton = view.findViewById(R.id.infoButton);
            wikiButton = view.findViewById(R.id.wikiButton);
            launchesButton = view.findViewById(R.id.launcher_launches);

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
                case R.id.item_icon:
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
