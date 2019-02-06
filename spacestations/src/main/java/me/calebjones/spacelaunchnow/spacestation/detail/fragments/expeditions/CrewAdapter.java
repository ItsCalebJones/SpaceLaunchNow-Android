package me.calebjones.spacelaunchnow.spacestation.detail.fragments.expeditions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.AstronautFlight;
import me.calebjones.spacelaunchnow.spacestation.R;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.ViewHolder> {

    public int position;
    private Context context;
    private List<AstronautFlight> astronauts;

    public CrewAdapter(Context context, List<AstronautFlight> astronauts) {
        this.context = context;
        this.astronauts = astronauts;
    }

    public void addItems(List<AstronautFlight> items) {
        astronauts = items;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spacestation_astronaut_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        AstronautFlight item = astronauts.get(i);
        holder.title.setText(item.getAstronaut().getName());
        holder.subtitle.setText(item.getRole());
        GlideApp.with(context)
                .load(item.getAstronaut().getProfileImage())
                .thumbnail(0.5f)
                .circleCrop()
                .into(holder.icon);

    }

    @Override
    public int getItemCount() {
        return astronauts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView title;
        private TextView subtitle;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.astronaut_icon);
            title = view.findViewById(R.id.astronaut_title);
            subtitle = view.findViewById(R.id.astronaut_subtitle);
        }
    }
}

