package me.calebjones.spacelaunchnow.common.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.AstronautFlight;

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
                .load(item.getAstronaut().getProfileImageThumbnail())
                .placeholder(R.drawable.placeholder)
                .thumbnail(0.5f)
                .circleCrop()
                .into(holder.icon);

    }

    @Override
    public int getItemCount() {
        if (astronauts!= null) {
            return astronauts.size();
        } else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View rootView;
        private ImageView icon;
        private TextView title;
        private TextView subtitle;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.astronaut_icon);
            title = view.findViewById(R.id.astronaut_title);
            subtitle = view.findViewById(R.id.astronaut_subtitle);
            rootView = view.findViewById(R.id.rootView);

            rootView.setOnClickListener(v -> {
                AstronautFlight astronautFlight = astronauts.get(getAdapterPosition());
                try {
                    Intent exploreIntent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.astronauts.detail.AstronautDetailsActivity"));
                    exploreIntent.putExtra("astronautId", astronautFlight.getAstronaut().getId());
                    context.startActivity(exploreIntent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

