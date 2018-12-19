package me.calebjones.spacelaunchnow.ui.main.vehicles.launcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.ArrayList;
import java.util.List;

import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to the UpcomingLaunchesFragment
 */
public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {

    public int position;
    private Context mContext;
    private List<Agency> agencies = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private boolean night = false;
    private RequestOptions requestOptions;
    private int palette;
    private int color;

    public VehicleAdapter(Context context) {
        agencies = new ArrayList();
        this.mContext = context;

        requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .centerCrop();
    }

    public void addItems(List<Agency> items) {
        if (this.agencies == null) {
            this.agencies = items;
        } else if (this.agencies.size() == 0) {
            this.agencies.addAll(items);
        } else {
            this.agencies.clear();
            this.agencies.addAll(items);
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
        Agency launcher = agencies.get(i);
        Timber.v("onBindViewHolder %s", launcher.getName());

        GlideApp.with(mContext)
                .load(launcher.getImageUrl())
                .into(holder.picture);
        holder.subTitle.setText(launcher.getType());
        holder.name.setText(launcher.getName());
    }

    @Override
    public int getItemCount() {
        return agencies.size();
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
