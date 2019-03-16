package me.calebjones.spacelaunchnow.spacestation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.spacestation.detail.SpacestationDetailsActivity;


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
        holder.spacestationSubtitle.setText(holder.mItem.getType().getName());
        holder.spacestationDescription.setText(holder.mItem.getDescription());
        holder.orbitlPillText.setText(holder.mItem.getOrbit());
        holder.founded.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format((holder.mItem.getFounded())));
        if (holder.mItem.getDeorbited() != null) {
            holder.deorbited.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format((holder.mItem.getDeorbited())));
        } else {
            holder.deorbited.setText("N/A");
        }

        if (holder.mItem.getStatus() != null){
            holder.statusPillText.setText(holder.mItem.getStatus().getName());
            switch (holder.mItem.getStatus().getId()){
                case 1:
                    holder.statusPill.setCardBackgroundColor(ContextCompat.getColor(context, R.color.material_color_green_500));
                    break;
                case 2:
                    holder.statusPill.setCardBackgroundColor(ContextCompat.getColor(context, R.color.material_color_red_500));
                    break;
                case 3:
                    holder.statusPill.setCardBackgroundColor(ContextCompat.getColor(context, R.color.material_color_deep_orange_500));
                    break;
                default:
                    holder.statusPill.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cyanea_primary));
                    break;
            }
        }

        GlideApp.with(context)
                .load(holder.mItem.getImageUrl())
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
        private CardView orbitPill;
        private TextView orbitlPillText;
        private CardView statusPill;
        private TextView statusPillText;
        private TextView founded;
        private TextView deorbited;
        private AppCompatButton button;
        public Spacestation mItem;

        public ViewHolder(View view) {
            super(view);
            spacestationImage = view.findViewById(R.id.spacestation_image);
            spacestationTitle = view.findViewById(R.id.spacestation_title);
            spacestationSubtitle = view.findViewById(R.id.spacestaion_subtitle);
            spacestationDescription = view.findViewById(R.id.spacestation_description);
            orbitPill = view.findViewById(R.id.orbit_pill_layout);
            orbitlPillText = view.findViewById(R.id.orbit_text);
            statusPill = view.findViewById(R.id.status_pill_layout);
            statusPillText = view.findViewById(R.id.status_text);
            founded = view.findViewById(R.id.founded);
            deorbited = view.findViewById(R.id.deorbited);

            button = view.findViewById(R.id.spacestation_button);
            button.setOnClickListener(v -> {
            Spacestation spacestation = spacestations.get(getAdapterPosition());
            Intent exploreIntent = new Intent(context, SpacestationDetailsActivity.class);
            exploreIntent.putExtra("spacestationId", spacestation.getId());
            context.startActivity(exploreIntent);
            });
        }

    }
}
