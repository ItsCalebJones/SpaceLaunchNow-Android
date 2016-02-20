package me.calebjones.spacelaunchnow.content.adapter;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.GridItem;
import me.calebjones.spacelaunchnow.ui.activity.VehicleDetailActivity;
import me.calebjones.spacelaunchnow.utils.OnItemClickListener;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

/**
 * This adapter takes data from SharedPreference/LoaderService and applies it to the UpcomingLaunchesFragment
 */
public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {

    public int position;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private List<GridItem> items = new ArrayList<GridItem>();
    private static SharedPreference sharedPreference;
    private OnItemClickListener onItemClickListener;
    private int defaultBackgroundcolor;
    private static final int SCALE_DELAY = 30;
    private int lastPosition = -1;
    private boolean night;

    public VehicleAdapter(Context context) {
        rightNow = Calendar.getInstance();
        items = new ArrayList();
        sharedPreference = SharedPreference.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
    }

    public void addItems(List<GridItem> items) {
        if (this.items == null) {
            this.items = items;
        } else {
            this.items.addAll(items);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreference = SharedPreference.getInstance(mContext);

        if (sharedPreference.getNightMode()) {
            night = true;
            m_theme = R.layout.gridview_item;
            defaultBackgroundcolor = ContextCompat.getColor(mContext, R.color.colorAccent);
        } else {
            night = false;
            m_theme = R.layout.gridview_item;
            defaultBackgroundcolor = ContextCompat.getColor(mContext, R.color.darkAccent);
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final GridItem item = items.get(i);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Glide.with(mContext)
                    .load(item.getImageURL())
                    .asBitmap()
                    .fitCenter()
                    .into(new BitmapImageViewTarget(holder.picture) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);
                            setCellColors(bitmap, holder, position);
                            amimateCell(holder);
                        }
                    });
        } else {
            holder.grid_root.setScaleY(1);
            holder.grid_root.setScaleX(1);
            Glide.with(mContext)
                    .load(item.getImageURL())
                    .fitCenter()
                    .into(holder.picture);
        }

        holder.name.setText(item.getName());
    }

    private void amimateCell(ViewHolder holder) {

        int cellPosition = holder.getPosition();

        if (!holder.animated) {

            holder.animated = true;
            holder.grid_root.setScaleY(0);
            holder.grid_root.setScaleX(0);
            holder.grid_root.animate()
                    .scaleY(1).scaleX(1)
                    .setDuration(300)
                    .setStartDelay(SCALE_DELAY * cellPosition)
                    .start();
        }

    }

    public void setCellColors(Bitmap b, final ViewHolder viewHolder, final int position) {

        if (b != null) {
            Palette.generateAsync(b, new Palette.PaletteAsyncListener() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch;
                    if (night){
                        swatch = palette.getDarkMutedSwatch();
                    } else {
                        swatch = palette.getLightVibrantSwatch();
                    }

                    if (swatch != null) {

                        viewHolder.name.setTextColor(swatch.getTitleTextColor());
                        viewHolder.picture.setTransitionName("cover" + position);

                        Utils.animateViewColor(viewHolder.grid_root, defaultBackgroundcolor,
                                swatch.getRgb());

                    } else {

                        Timber.e("BookAdapter onGenerated - The swatch was null at: %s", position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View grid_root;
        public ImageView picture;
        public TextView name;
        private OnItemClickListener onItemClickListener;
        protected boolean animated = false;

        //Add content to the card
        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);

            this.onItemClickListener = onItemClickListener;
            grid_root = view.findViewById(R.id.grid_root);
            picture = (ImageView) view.findViewById(R.id.picture);
            name = (TextView) view.findViewById(R.id.text);
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
