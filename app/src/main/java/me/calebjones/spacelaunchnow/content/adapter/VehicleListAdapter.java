package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.RocketDetails;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import timber.log.Timber;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.ViewHolder> {

    public int position;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private DatabaseManager databaseManager;
    private List<Rocket> items = new ArrayList<Rocket>();
    private static SharedPreference sharedPreference;
    private RocketDetails launchVehicle;
    private int defaultBackgroundcolor;
    private static final int SCALE_DELAY = 30;
    private int lastPosition = -1;

    public VehicleListAdapter(Context context) {
        rightNow = Calendar.getInstance();
        items = new ArrayList();
        sharedPreference = SharedPreference.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
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
        sharedPreference = SharedPreference.getInstance(mContext);
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
        //TODO Update JSON with each Space Shuttle
        if (item.getName().contains("Space Shuttle")){
            query = "Space Shuttle";
        } else {
            query = item.getName();
        }
        launchVehicle = databaseManager.getLaunchVehicle(query);

        if (launchVehicle != null){
            holder.vehicle_spec_view.setVisibility(View.VISIBLE);
            holder.launch_vehicle_description.setVisibility(View.VISIBLE);
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

        Glide.with(mContext)
                .load(item.getImageURL())
                .error(R.drawable.placeholder)
                .into(holder.item_icon);

        holder.item_title.setText(item.getName());

        if (item.getInfoURL() != null){
            holder.infoButton.setVisibility(View.GONE);
        } else {
            holder.infoButton.setVisibility(View.VISIBLE);
        }
        if (item.getWikiURL() != null){
            holder.wikiButton.setVisibility(View.GONE);
        } else {
            holder.wikiButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView item_icon;
        public View vehicle_spec_view, vehicle_container;
        public TextView item_title,launch_vehicle_specs_height,
                launch_vehicle_specs_diameter,launch_vehicle_specs_stages,launch_vehicle_specs_leo,
                launch_vehicle_specs_gto,launch_vehicle_specs_launch_mass, launch_vehicle_specs_thrust
                ,infoButton, wikiButton,launch_vehicle_description;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            vehicle_container = view.findViewById(R.id.vehicle_container);
            vehicle_spec_view = view.findViewById(R.id.vehicle_spec_view);
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
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            Timber.d("%s clicked.", getPosition());
        }
    }
}
