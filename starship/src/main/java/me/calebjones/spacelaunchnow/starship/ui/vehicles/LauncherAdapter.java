package me.calebjones.spacelaunchnow.starship.ui.vehicles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.content.LaunchStatusUtil;
import me.calebjones.spacelaunchnow.data.models.main.launcher.Launcher;
import me.spacelaunchnow.starship.R;
import me.spacelaunchnow.starship.R2;

public class LauncherAdapter extends RecyclerView.Adapter<LauncherAdapter.ViewHolder> {

    private List<Launcher> roadClosureList;
    private Context context;

    public LauncherAdapter(Context context) {
        roadClosureList = new ArrayList<>();
        this.context = context;
    }

    public void addItems(List<Launcher> events) {
        this.roadClosureList = events;
        this.notifyDataSetChanged();
    }

    public void clear() {
        roadClosureList = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.launcher_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Launcher launcher = roadClosureList.get(position);

        if (launcher.getImageUrl() != null) {
            GlideApp.with(context)
                    .load(launcher.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
        }
        String status = "";
        if (launcher.getStatus() != null && launcher.getStatus().length() > 0) {
            status = launcher.getStatus().substring(0, 1).toUpperCase() + launcher.getStatus().substring(1).toLowerCase();
        }


        holder.title.setText(launcher.getLauncherConfig().getName() + " - " + launcher.getSerialNumber());
        if (status.toLowerCase().contains("active")) {
            holder.statusCard.setCardBackgroundColor(context.getResources().getColor(R.color.material_color_green_600));
        } else if (status.toLowerCase().contains("destroyed")) {
            holder.statusCard.setCardBackgroundColor(context.getResources().getColor(R.color.material_color_red_600));
        } else {
            holder.statusCard.setCardBackgroundColor(context.getResources().getColor(R.color.material_color_blue_grey_500));
        }
        holder.status.setText(status);
        holder.flights.setText("Flights: " + launcher.getPreviousFlights().toString());
        holder.description.setText(launcher.getDetails());
    }

    @Override
    public int getItemCount() {
        return roadClosureList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.title)
        TextView title;
        @BindView(R2.id.imageView)
        ImageView imageView;
        @BindView(R2.id.flights)
        TextView flights;
        @BindView(R2.id.status)
        TextView status;
        @BindView(R2.id.description)
        TextView description;
        @BindView(R2.id.status_pill_mini)
        CardView statusCard;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
