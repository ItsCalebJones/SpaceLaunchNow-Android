package me.calebjones.spacelaunchnow.common.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class SpacestationAdapter extends RecyclerView.Adapter<SpacestationAdapter.ViewHolder> {
    public int position;
    private RealmList<Spacestation> spacestations;
    private Context mContext;

    public SpacestationAdapter(Context context) {
        spacestations = new RealmList<>();
        mContext = context;
    }

    public void addItems(List<Spacestation> spacestations) {

        if (this.spacestations != null) {
            this.spacestations.addAll(spacestations);
        } else {
            this.spacestations = new RealmList<>();
            this.spacestations.addAll(spacestations);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        spacestations.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v("onCreate ViewHolder.");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spacestation_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Spacestation item = spacestations.get(i);

        if (item.getImageUrl() != null) {
            GlideApp.with(mContext)
                    .load(item.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(holder.spacestationIcon);
        } else {
            GlideApp.with(mContext)
                    .load(R.drawable.ic_satellite)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(holder.spacestationIcon);
        }

        holder.spacestationName.setText(item.getName());
        holder.spacestationLocation.setText(item.getOrbit());
        holder.spacestationStatus.setText(item.getStatus().getName());
    }

    @Override
    public int getItemCount() {
        return spacestations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R2.id.spacestation_icon)
        ImageView spacestationIcon;
        @BindView(R2.id.spacestation_name)
        TextView spacestationName;
        @BindView(R2.id.spacestation_status)
        TextView spacestationStatus;
        @BindView(R2.id.spacestation_location)
        TextView spacestationLocation;
        @BindView(R2.id.rootview)
        ConstraintLayout rootview;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            rootview.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            Spacestation spacestation = spacestations.get(getAdapterPosition());
            Intent exploreIntent = null;
            try {
                exploreIntent = new Intent(mContext, Class.forName("me.calebjones.spacelaunchnow.spacestation.detail.SpacestationDetailsActivity"));
                exploreIntent.putExtra("spacestationId", spacestation.getId());
                mContext.startActivity(exploreIntent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}

