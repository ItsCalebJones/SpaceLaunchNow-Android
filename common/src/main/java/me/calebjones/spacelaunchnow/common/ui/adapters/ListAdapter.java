package me.calebjones.spacelaunchnow.common.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public int position;
    private RealmList<LaunchList> launchList;
    private Context mContext;
    private final ListPreloader.PreloadSizeProvider sizeProvider = new ViewPreloadSizeProvider();
    private int color;

    public ListAdapter(Context context, boolean night) {
        launchList = new RealmList<>();
        mContext = context;

        if (night){
            color = ContextCompat.getColor(mContext, R.color.material_color_white);
        } else {
            color = ContextCompat.getColor(mContext, R.color.material_color_black);
        }
    }

    public void addItems(List<LaunchList> launchList) {

        if (this.launchList == null) {
            this.launchList = new RealmList<>();
        }
        this.launchList.addAll(launchList);
        this.notifyDataSetChanged();
    }

    public void clear() {
        launchList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v("onCreate ViewHolder.");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.launch_list_item, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rHolder, int i) {
        final LaunchList launchItem = launchList.get(i);

        String[] title;
        String launchDate;

        position = i;

        ListAdapter.ViewHolder holder = (ListAdapter.ViewHolder) rHolder;

        //Retrieve missionType
        if (launchItem.getImage() != null) {
            GlideApp.with(mContext)
                    .load(launchItem.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(holder.categoryIcon);
        } else {
            if (launchItem.getMission() != null) {

                GlideApp.with(mContext)
                        .load(Utils.getCategoryIcon(launchItem.getMissionType()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .transform(new ColorFilterTransformation(color))
                        .into(holder.categoryIcon);

            } else {
                GlideApp.with(mContext)
                        .load(R.drawable.ic_unknown)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .transform(new ColorFilterTransformation(color))
                        .into(holder.categoryIcon);
            }
        }
//        holder.launch_date.setText(Utils.getStatusBasedDateFormat(launchItem.getNet(), launchItem.getStatus()));
        holder.launch_date.setText(Utils.getSimpleDateFormatForUIWithPrecision(launchItem.getNetPrecision()).format(launchItem.getNet()));

        //If pad and agency exist add it to location, otherwise get whats always available
        if (launchItem.getLocation() != null) {
            holder.location.setText(launchItem.getLocation());
        } else {
            holder.location.setText(mContext.getString(R.string.click_for_info));
        }

        if (launchItem.getName() != null) {
            title = launchItem.getName().split("\\|");
            try {
                if (title.length > 0) {
                    holder.title.setText(title[1].trim());
                    holder.mission.setText(title[0].trim());
                } else {
                    holder.title.setText(launchItem.getName());
                    if (launchItem.getMission() != null) {
                        holder.title.setText(launchItem.getMission());
                    }
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                holder.title.setText(launchItem.getName());
                if (launchItem.getMission() != null) {
                    holder.title.setText(launchItem.getMission());
                }

            }
        }

        if (launchItem.getLanding() != null) {
            holder.landingCard.setVisibility(View.VISIBLE);
            holder.landingLocation.setText(launchItem.getLanding());
            holder.landingCard.setCardBackgroundColor(LaunchStatusUtil.getLandingStatusColor(mContext, launchItem.getLandingSuccess()));
        } else {
            holder.landingCard.setVisibility(View.GONE);
        }

        if (launchItem.getOrbit() != null) {
            holder.orbitCard.setVisibility(View.VISIBLE);
            holder.orbitName.setText(launchItem.getOrbit());
        } else {
            holder.orbitCard.setVisibility(View.GONE);
        }

        holder.status.setText(launchItem.getStatus().getAbbrev());
        holder.status.setOnClickListener(v -> {
            Toast.makeText(mContext, launchItem.getStatus().getDescription(), Toast.LENGTH_LONG).show();
        });
        holder.statusCard.setCardBackgroundColor(LaunchStatusUtil.getLaunchStatusColor(mContext, launchItem.getStatus().getId()));
        holder.rootView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LaunchDetailActivity.class);
            intent.putExtra("TYPE", "launch");
            intent.putExtra("launchID", launchItem.getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.status)
        TextView status;
        @BindView(R2.id.status_pill_mini)
        CardView statusCard;
        @BindView(R2.id.landing)
        TextView landingLocation;
        @BindView(R2.id.landing_pill_mini)
        CardView landingCard;
        @BindView(R2.id.launcher_name)
        TextView orbitName;
        @BindView(R2.id.launcher_pill_mini)
        CardView orbitCard;
        @BindView(R2.id.launch_rocket)
        TextView title;
        @BindView(R2.id.location)
        TextView location;
        @BindView(R2.id.launch_date)
        TextView launch_date;
        @BindView(R2.id.mission)
        TextView mission;
        @BindView(R2.id.categoryIcon)
        ImageView categoryIcon;
        @BindView(R2.id.rootview)
        View rootView;


        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

    }

}

