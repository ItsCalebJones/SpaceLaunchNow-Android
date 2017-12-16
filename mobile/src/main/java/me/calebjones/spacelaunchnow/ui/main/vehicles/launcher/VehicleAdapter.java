package me.calebjones.spacelaunchnow.ui.main.vehicles.launcher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.Launcher;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to the UpcomingLaunchesFragment
 */
public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {

    public int position;
    private Context mContext;
    private List<Launcher> launchers = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private int palette;
    private RequestOptions requestOptions;

    public VehicleAdapter(Context context) {
        launchers = new ArrayList();
        this.mContext = context;

        if (ListPreferences.getInstance(mContext).isNightModeActive(mContext)) {
            palette = GlidePalette.Profile.MUTED_DARK;
        } else {
            palette = GlidePalette.Profile.VIBRANT;
        }

        requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .centerCrop();
    }

    public void addItems(List<Launcher> items) {
        if (this.launchers == null) {
            this.launchers = items;
        } else if (this.launchers.size() == 0) {
            this.launchers.addAll(items);
        } else {
            this.launchers.clear();
            this.launchers.addAll(items);
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
        Launcher launcher = launchers.get(i);
        Timber.v("onBindViewHolder %s", launcher.getName());

        GlideApp.with(mContext)
                .load(launcher.getImageURL())
                .apply(requestOptions)
                .listener(GlidePalette.with(launcher.getImageURL())
                        .use(palette)
                        .intoBackground(holder.textContainer, GlidePalette.Swatch.RGB)
                        .crossfade(true))
                .into(holder.picture);
        holder.subTitle.setText(launcher.getName());
        holder.name.setText(launcher.getAgency());
    }

    @Override
    public int getItemCount() {
        return launchers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View grid_root;
        public ImageView picture;
        public TextView name;
        public TextView subTitle;
        public View textContainer;
        private OnItemClickListener onItemClickListener;

        //Add content to the card
        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);

            this.onItemClickListener = onItemClickListener;
            grid_root = view.findViewById(R.id.grid_root);
            picture = view.findViewById(R.id.picture);
            name = view.findViewById(R.id.text);
            subTitle = view.findViewById(R.id.text_subtitle);
            textContainer = view.findViewById(R.id.text_container);
            grid_root.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            Timber.d("%s clicked.", getAdapterPosition());
            onItemClickListener.onClick(v, getAdapterPosition());
        }
    }


}
