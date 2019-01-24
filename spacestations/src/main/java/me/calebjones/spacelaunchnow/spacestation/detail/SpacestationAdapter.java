package me.calebjones.spacelaunchnow.spacestation.detail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.content.LaunchStatusUtil;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;
import me.calebjones.spacelaunchnow.spacestation.detail.expeditions.DockedVehicleItem;
import me.calebjones.spacelaunchnow.spacestation.detail.expeditions.ListItem;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class SpacestationAdapter extends RecyclerView.Adapter<SpacestationAdapter.ViewHolder> {
    public int position;
    private List<ListItem> items;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static ListPreferences sharedPreference;
    private final ListPreloader.PreloadSizeProvider sizeProvider = new ViewPreloadSizeProvider();
    private SimpleDateFormat sdf;
    private SimpleDateFormat df;
    private int color;

    public SpacestationAdapter(Context context) {

    }

    public void addItems(List<ListItem> items) {

        if (this.items != null) {
            this.items.addAll(items);
        } else {
            this.items = new ArrayList<>();
            this.items.addAll(items);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getListItemType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = null;
        switch (type) {
            case ListItem.TYPE_DOCKED_VEHICLE:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_docked_vehicle, viewGroup, false);
                return new ViewHolderDockedVehicle(view);
            case ListItem.TYPE_DOCKING_EVENT:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_docking_event, viewGroup, false);
                return new ViewHolderDockingEvent(view);
            case ListItem.TYPE_ACTIVE_EXPEDITION:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_active_expedition, viewGroup, false);
                return new ViewHolderActiveExpedition(view);
            case ListItem.TYPE_PAST_EXPEDITION:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_past_expedition, viewGroup, false);
                return new ViewHolderActiveExpedition(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        ListItem item = items.get(pos);
        viewHolder.bindType(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(ListItem item);
    }

    public class ViewHolderDockedVehicle extends ViewHolder {
        private final TextView mTextView;

        public ViewHolderDockedVehicle(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.txtView);
        }

        public void bindType(ListItem item) {
            mTextView.setText(((DockedVehicleItem) item).getId());
        }
    }
}

