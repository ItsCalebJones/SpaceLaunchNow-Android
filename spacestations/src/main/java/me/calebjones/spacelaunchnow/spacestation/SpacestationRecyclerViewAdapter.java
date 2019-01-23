package me.calebjones.spacelaunchnow.spacestation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;


public class SpacestationRecyclerViewAdapter extends RecyclerView.Adapter<SpacestationRecyclerViewAdapter.ViewHolder> {


    private List<Spacestation> spacestations;
    private Context context;

    public SpacestationRecyclerViewAdapter(Context context) {
        spacestations = new ArrayList<>();
        this.context = context;
    }

    public void addItems(List<Spacestation> spacestations) {
        this.spacestations = spacestations;
        this.notifyDataSetChanged();
    }

    public void clear() {
        spacestations = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spacestation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = spacestations.get(position);
        holder.spacestationTitle.setText(holder.mItem.getName());
        holder.spacestationSubtitle.setText(holder.mItem.getOwners().first().getName());
        holder.spacestationDescription.setText(holder.mItem.getDescription());

        GlideApp.with(context)
                .load(holder.mItem.getImageUrl())
                .fitCenter()
                .into(holder.spacestationImage);
    }

    @Override
    public int getItemCount() {
        return spacestations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView spacestationImage;
        private TextView spacestationTitle;
        private TextView spacestationSubtitle;
        private TextView spacestationDescription;
        private AppCompatButton button;
        public Spacestation mItem;

        public ViewHolder(View view) {
            super(view);
            spacestationImage = view.findViewById(R.id.spacestation_image);
            spacestationTitle = view.findViewById(R.id.spacestation_title);
            spacestationSubtitle = view.findViewById(R.id.spacestaion_subtitle);
            spacestationDescription = view.findViewById(R.id.spacestation_description);
            button = view.findViewById(R.id.spacestation_button);
            button.setOnClickListener(v -> {
//            Astronaut astronaut = astronauts.get(position);
//            Intent exploreIntent = new Intent(context, SpacestationDetailsActivity.class);
//            exploreIntent.putExtra("astronautId", astronaut.getId());
//            context.startActivity(exploreIntent);
            });
        }

    }
}
