package me.calebjones.spacelaunchnow.content.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.natives.RocketDetails;
import me.calebjones.spacelaunchnow.content.models.legacy.Rocket;
import me.calebjones.spacelaunchnow.ui.activity.FullscreenImageActivity;
import me.calebjones.spacelaunchnow.utils.Utils;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.ViewHolder> {

    public int position;
    private Context mContext;
    private Activity activity;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private DatabaseManager databaseManager;
    private List<Rocket> items = new ArrayList<Rocket>();
    private static ListPreferences sharedPreference;
    private RocketDetails launchVehicle;
    private int defaultBackgroundcolor;
    private static final int SCALE_DELAY = 30;
    private int lastPosition = -1;

    public VehicleListAdapter(Context context, Activity activity) {
        rightNow = Calendar.getInstance();
        items = new ArrayList();
        sharedPreference = ListPreferences.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
        this.activity = activity;
    }

    public void addItems(List<Rocket> items) {
        if (this.items == null) {
            this.items = items;
        } else {
            this.items.addAll(items);
        }
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreference = ListPreferences.getInstance(mContext);
        databaseManager = new DatabaseManager(mContext);

        if (sharedPreference.getNightMode()) {
            m_theme = R.layout.dark_vehicle_list_item;
            defaultBackgroundcolor = ContextCompat.getColor(mContext, R.color.colorAccent);
        } else {
            m_theme = R.layout.light_vehicle_list_item;
            defaultBackgroundcolor = ContextCompat.getColor(mContext, R.color.darkAccent);
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Rocket item = items.get(i);

        launchVehicle = new RocketDetails();

        String query;
        if (item.getName().contains("Space Shuttle")){
            query = "Space Shuttle";
        } else {
            query = item.getName();
        }
        launchVehicle = databaseManager.getLaunchVehicle(query);

        if (launchVehicle != null){
            holder.vehicle_spec_view.setVisibility(View.VISIBLE);
            if (launchVehicle.getDescription() != null && launchVehicle.getDescription().length() > 0) {
                holder.launch_vehicle_description.setVisibility(View.VISIBLE);
                holder.launch_vehicle_description.setText(launchVehicle.getDescription());
            } else {
                holder.launch_vehicle_description.setVisibility(View.GONE);
            }
            holder.launch_vehicle_specs_height.setText(String.format("Height: %s Meters", launchVehicle.getLength()));
            holder.launch_vehicle_specs_diameter.setText(String.format("Diameter: %s Meters", launchVehicle.getDiameter()));
            holder.launch_vehicle_specs_stages.setText(String.format("Stages: %d", launchVehicle.getMaxStage()));
            holder.launch_vehicle_specs_leo.setText(String.format("Payload to LEO: %s kg", launchVehicle.getLEOCapacity()));
            holder.launch_vehicle_specs_gto.setText(String.format("Payload to GTO: %s kg", launchVehicle.getGTOCapacity()));
            holder.launch_vehicle_specs_launch_mass.setText(String.format("Mass at Launch: %s Tons", launchVehicle.getLaunchMass()));
            holder.launch_vehicle_specs_thrust.setText(String.format("Thrust at Launch: %s kN", launchVehicle.getTOThrust()));
        } else {
            holder.launch_vehicle_description.setVisibility(View.GONE);
            holder.vehicle_spec_view.setVisibility(View.GONE);
        }

        if (item.getImageURL().length() == 0){
            holder.fab.setVisibility(View.INVISIBLE);
        } else {
            holder.fab.setVisibility(View.VISIBLE);
        }

        Glide.with(mContext)
                .load(item.getImageURL())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.item_icon);

        holder.item_title.setText(item.getName());

        if (launchVehicle != null){
            if(launchVehicle.getInfoURL().length() > 0) {
                items.get(i).setInfoURL(launchVehicle.getInfoURL());
                holder.infoButton.setVisibility(View.VISIBLE);
            } else if (item.getInfoURL() != null && !item.getInfoURL().contains("null")) {
                if (item.getInfoURL().length() > 0) {
                    holder.infoButton.setVisibility(View.VISIBLE);
                } else {
                    holder.infoButton.setVisibility(View.GONE);
                }
            }
        }  else if (item.getInfoURL() != null && !item.getInfoURL().contains("null")){
            if(item.getInfoURL().length() > 0){
                holder.infoButton.setVisibility(View.VISIBLE);
            }  else {
                holder.infoButton.setVisibility(View.GONE);
            }
        } else {
            holder.infoButton.setVisibility(View.GONE);
        }

        if (launchVehicle != null){
            if(launchVehicle.getWikiURL().length() > 0) {
                items.get(i).setWikiURL(launchVehicle.getWikiURL());
                holder.wikiButton.setVisibility(View.VISIBLE);
            }   else if (item.getWikiURL() != null && !item.getWikiURL().contains("null")){
                if(item.getWikiURL().length() > 0){
                    holder.wikiButton.setVisibility(View.VISIBLE);
                }  else {
                    holder.wikiButton.setVisibility(View.GONE);
                }
            }
        }  else if (item.getWikiURL() != null && !item.getWikiURL().contains("null")){
            if(item.getWikiURL().length() > 0){
                holder.wikiButton.setVisibility(View.VISIBLE);
            }  else {
                holder.wikiButton.setVisibility(View.GONE);
            }
        } else {
            holder.infoButton.setVisibility(View.GONE);
        }

        if (holder.infoButton.getVisibility() == View.GONE && holder.wikiButton.getVisibility() == View.GONE){
            holder.button_layout.setVisibility(View.GONE);
        } else {
            holder.button_layout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView item_icon;
        public View vehicle_spec_view, vehicle_container, button_layout;
        public FloatingActionButton fab;
        public TextView item_title,launch_vehicle_specs_height,
                launch_vehicle_specs_diameter,launch_vehicle_specs_stages,launch_vehicle_specs_leo,
                launch_vehicle_specs_gto,launch_vehicle_specs_launch_mass, launch_vehicle_specs_thrust
                ,infoButton, wikiButton,launch_vehicle_description;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            fab = (FloatingActionButton) view.findViewById(R.id.vehicle_fab);
            vehicle_container = view.findViewById(R.id.vehicle_container);
            vehicle_spec_view = view.findViewById(R.id.vehicle_spec_view);
            button_layout = view.findViewById(R.id.button_layout);
            item_icon = (ImageView) view.findViewById(R.id.item_icon);
            item_title = (TextView) view.findViewById(R.id.item_title);
            launch_vehicle_specs_stages = (TextView) view.findViewById(R.id.launch_vehicle_specs_stages);
            launch_vehicle_specs_height = (TextView) view.findViewById(R.id.launch_vehicle_specs_height);
            launch_vehicle_specs_diameter = (TextView) view.findViewById(R.id.launch_vehicle_specs_diameter);
            launch_vehicle_specs_leo = (TextView) view.findViewById(R.id.launch_vehicle_specs_leo);
            launch_vehicle_specs_gto = (TextView) view.findViewById(R.id.launch_vehicle_specs_gto);
            launch_vehicle_specs_launch_mass = (TextView) view.findViewById(R.id.launch_vehicle_specs_launch_mass);
            launch_vehicle_specs_thrust = (TextView) view.findViewById(R.id.launch_vehicle_specs_thrust);
            launch_vehicle_description = (TextView) view.findViewById(R.id.launch_vehicle_description);
            infoButton = (TextView) view.findViewById(R.id.infoButton);
            wikiButton = (TextView) view.findViewById(R.id.wikiButton);

            infoButton.setOnClickListener(this);
            wikiButton.setOnClickListener(this);
            fab.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.infoButton:
                    Utils.openCustomTab(activity, mContext, items.get(position).getInfoURL());
                    break;
                case R.id.wikiButton:
                    Utils.openCustomTab(activity, mContext, items.get(position).getWikiURL());
                    break;
                case R.id.vehicle_fab:
                    Toast.makeText(activity, items.get(position).getName() + " | Work in progress!", Toast.LENGTH_SHORT);

                    Intent animateIntent = new Intent(activity, FullscreenImageActivity.class);
                    animateIntent.putExtra("imageURL", items.get(position).getImageURL());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        activity.startActivity(animateIntent, ActivityOptions.makeSceneTransitionAnimation(activity, item_icon, "imageCover").toBundle());
                    } else {
                        activity.startActivity(animateIntent);
                    }
                    break;
            }
        }
    }
}
