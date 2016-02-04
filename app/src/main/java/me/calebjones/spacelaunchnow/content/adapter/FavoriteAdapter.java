package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import timber.log.Timber;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    public int position;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private List<Launch> items = new ArrayList<Launch>();
    private static SharedPreference sharedPreference;
    private OnItemClickListener onItemClickListener;

    public FavoriteAdapter(Context context) {
        rightNow = Calendar.getInstance();
        items = new ArrayList();
        sharedPreference = SharedPreference.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
    }

    public void addItems(List<Launch> items) {
        if (this.items == null) {
            this.items = items;
        } else {
            this.items.addAll(items);
        }
    }

    public void removeAll() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (position < items.size()) {
            Timber.v("Remove item: %s", items.get(position).getName());
            sharedPreference.removeFavLaunch(items.get(position));
            items.remove(position);
            notifyItemRemoved(position);
        } else {
            Timber.e("ERROR: Tried to remove a item that was out of bounds.");
        }
    }

    public Launch get(int position){
        return items.get(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreference = SharedPreference.getInstance(mContext);

        if (sharedPreference.getNightMode()) {
            m_theme = R.layout.dark_content_fav_item;
        } else {
            m_theme = R.layout.light_content_fav_item;
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Launch item = items.get(i);

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
        df.toLocalizedPattern();

        holder.launch_rocket.setText(item.getName());

        //If location is available then see if pad and agency informaiton is avaialble.
        if (item.getLocation().getName() != null) {
            if (item.getLocation().getPads().size() > 0 && item.getLocation().getPads().
                    get(0).getAgencies().size() > 0) {

                //Get the first CountryCode
                String country = item.getLocation().getPads().
                        get(0).getAgencies().get(0).getCountryCode();
                country = (country.substring(0, 2));

                //Get the location remove the pad information
                String location = item.getLocation().getName() + " " + country;
                item.getLocation().getPads().
                        get(0).getAgencies().get(0).getCountryCode();
                location = (location.substring(location.indexOf(", ") + 2));

                holder.location.setText(location);
            } else {
                holder.location.setText(item.getLocation().getName()
                        .substring(item.getLocation().getName().indexOf(", ") + 2));
            }
        }

        //If timestamp is available calculate date.
        if (item.getWsstamp() > 0) {
            long longdate = item.getWsstamp();
            longdate = longdate * 1000;
            final Date date = new Date(longdate);
            holder.launch_date.setText(df.format(date));

        } else {
            Date date = new Date(item.getWindowstart());
            holder.launch_date.setText(df.format(date));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView launch_rocket, location, launch_date;
        private OnItemClickListener onItemClickListener;

        //Add content to the card
        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);

            this.onItemClickListener = onItemClickListener;
            launch_rocket = (TextView) view.findViewById(R.id.launch_rocket);
            location = (TextView) view.findViewById(R.id.location);
            launch_date = (TextView) view.findViewById(R.id.launch_date);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            Timber.d("%s clicked.", position);
            onItemClickListener.onClick(v, getAdapterPosition());
        }
    }
}
