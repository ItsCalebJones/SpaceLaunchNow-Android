package me.calebjones.spacelaunchnow.common.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;
import timber.log.Timber;

public class ExpeditionAdapter extends RecyclerView.Adapter<ExpeditionAdapter.ViewHolder> {
    public int position;
    private RealmList<Expedition> expeditionList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static ListPreferences sharedPreference;
    private final ListPreloader.PreloadSizeProvider sizeProvider = new ViewPreloadSizeProvider();
    private SimpleDateFormat sdf;
    private SimpleDateFormat df;
    private int color;

    public ExpeditionAdapter(Context context, boolean night) {
        rightNow = Calendar.getInstance();
        expeditionList = new RealmList<>();
        sharedPreference = ListPreferences.getInstance(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
        if (sharedPref.getBoolean("local_time", true)) {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
        } else {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy zzz");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        if (sharedPref.getBoolean("24_hour_mode", false)) {
            df = Utils.getSimpleDateFormatForUI("EEEE, MMMM dd, yyyy - HH:mm");
        } else {
            df = Utils.getSimpleDateFormatForUI("EEEE, MMMM dd, yyyy - hh:mm a zzz");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        this.night = night;
        if (night){
            color = ContextCompat.getColor(mContext, R.color.material_color_white);
        } else {
            color = ContextCompat.getColor(mContext, R.color.material_color_black);
        }
    }

    public void addItems(List<Expedition> expeditionList) {

        if (this.expeditionList != null) {
            this.expeditionList.addAll(expeditionList);
        } else {
            this.expeditionList = new RealmList<>();
            this.expeditionList.addAll(expeditionList);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        expeditionList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ExpeditionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v("onCreate ViewHolder.");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_active_expedition,
                viewGroup, false);
        return new ExpeditionAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ExpeditionAdapter.ViewHolder holder, int i) {
        Expedition expedition = expeditionList.get(i);
        holder.title.setText(expedition.getName());
        if (expedition.getStart() != null) {
            holder.start.setVisibility(View.VISIBLE);
            holder.start.setText(String.format(mContext.getString(R.string.start_fill), Utils.getSimpleDateFormatForUI("MMMM dd, yyyy").format(expedition.getStart())));
        } else {
            holder.start.setVisibility(View.GONE);
        }

        if (expedition.getEnd() != null) {
            holder.end.setVisibility(View.VISIBLE);
            holder.end.setText(String.format(mContext.getString(R.string.end_fill), Utils.getSimpleDateFormatForUI("MMMM dd, yyyy").format(expedition.getEnd())));
        } else {
            holder.end.setVisibility(View.GONE);
        }

        holder.crewRecyler.setLayoutManager(new LinearLayoutManager(mContext));
        holder.crewRecyler.setAdapter(new CrewAdapter(mContext, expedition.getCrew()));
    }

    @Override
    public int getItemCount() {
        return expeditionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView crewRecyler;
        private TextView title;
        private TextView subtitle;
        private TextView start;
        private TextView end;

        public ViewHolder(View itemView) {
            super(itemView);

            crewRecyler = itemView.findViewById(R.id.crew_recycler_view);
            title = itemView.findViewById(R.id.spacestation_active_title);
            subtitle = itemView.findViewById(R.id.spacestaion_active_subtitle);
            start = itemView.findViewById(R.id.start_date);
            end = itemView.findViewById(R.id.end_date);
        }
    }
}
