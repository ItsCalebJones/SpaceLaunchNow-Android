package me.calebjones.spacelaunchnow.ui.main.vehicles.orbiter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.Orbiter;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to the UpcomingLaunchesFragment
 */
public class OrbiterAdapter extends RecyclerView.Adapter<OrbiterAdapter.ViewHolder> {

    public int position;
    private Context mContext;
    private List<Orbiter> orbiters = new ArrayList<Orbiter>();
    private OnItemClickListener onItemClickListener;
    private int palette;
    private RequestOptions requestOptions;

    public OrbiterAdapter(Context context) {
        orbiters = new ArrayList();
        this.mContext = context;

        if (ListPreferences.getInstance(mContext).isNightModeActive(mContext)) {
            palette = GlidePalette.Profile.MUTED_DARK;
        } else {
            palette = GlidePalette.Profile.VIBRANT_DARK;
        }

        requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .centerCrop();
    }

    public void addItems(List<Orbiter> items) {
        if (this.orbiters == null) {
            this.orbiters = items;
        } else if (this.orbiters.size() == 0) {
            this.orbiters.addAll(items);
        } else {
            this.orbiters.clear();
            this.orbiters.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridview_item, viewGroup, false);
        return new ViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Orbiter orbiter = orbiters.get(i);
        Timber.v("onBindViewHolder %s", orbiter.getName());

        GlideApp.with(mContext)
                .load(orbiter.getImageURL())
                .apply(requestOptions)
//                .listener(GlidePalette.with(orbiter.getImageURL())
//                        .use(palette)
//                        .intoBackground(holder.name, GlidePalette.Swatch.RGB)
//                        .crossfade(true))
                .into(holder.picture);
        holder.name.setText(orbiter.getName());
        holder.subTitle.setText(orbiter.getAgency());
    }

    @Override
    public int getItemCount() {
        return orbiters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View grid_root;
        public ImageView picture;
        public TextView name;
        public TextView subTitle;
        private OnItemClickListener onItemClickListener;
        protected boolean animated = false;

        //Add content to the card
        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);

            this.onItemClickListener = onItemClickListener;
            grid_root = view.findViewById(R.id.grid_root);
            picture = (ImageView) view.findViewById(R.id.picture);
            name = (TextView) view.findViewById(R.id.text);
            subTitle = view.findViewById(R.id.text_subtitle);
            grid_root.setOnClickListener(this);
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
