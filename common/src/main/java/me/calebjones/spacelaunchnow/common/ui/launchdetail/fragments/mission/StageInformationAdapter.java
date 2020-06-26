package me.calebjones.spacelaunchnow.common.ui.launchdetail.fragments.mission;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.data.models.main.Landing;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.launcher.Launcher;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherStage;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.launches.launcher.LauncherLaunchActivity;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class StageInformationAdapter extends RecyclerView.Adapter<StageInformationAdapter.ViewHolder> {
    public int position;
    private List<LauncherStage> launcherList;
    private Context context;
    private Launch launch;

    public StageInformationAdapter(Launch launch, Context context) {
        this.launch = launch;
        launcherList = launch.getRocket().getLauncherStage();
        this.context = context;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v("onCreate ViewHolder.");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.core_information, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LauncherStage stage = launcherList.get(position);
        if (stage.getLauncher() != null) {
            Launcher launcher = stage.getLauncher();
            holder.viewCoreLaunches.setVisibility(View.VISIBLE);
            holder.viewCoreLaunches.setText(String.format(context.getString(R.string.view_x_launches), stage.getLauncher().getSerialNumber()));
            holder.viewCoreLaunches.setOnClickListener(v -> {
                Intent launches = new Intent(context, LauncherLaunchActivity.class);
                launches.putExtra("serialNumber", launcher.getSerialNumber());
                context.startActivity(launches);
            });

            if (launcher.getImageUrl() != null){
                GlideApp.with(context)
                        .load(launcher.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.coreImage);
            }
            holder.coreInformation.setText(String.format(context.getString(R.string.booster_information), launcher.getSerialNumber()));
            if (stage.getType() != null) {
                holder.coreInformationSubtitle.setText(String.format(context.getString(R.string.first_stage_x), stage.getType()));
            }
            holder.details.setText(launcher.getDetails());
            holder.serialNumberText.setText(stage.getLauncher().getSerialNumber());
            String cap = launcher.getStatus().substring(0, 1).toUpperCase() + stage.getLauncher().getStatus().substring(1);
            holder.statusText.setText(cap);
            holder.previousText.setText("");
            if (launcher.getFlightProven() == null) {
                holder.flightProven.setImageResource(R.drawable.ic_question_mark);
            } else if (launcher.getFlightProven()) {
                holder.flightProven.setImageResource(R.drawable.ic_checkmark);
            } else if (!launcher.getFlightProven()) {
                holder.flightProven.setImageResource(R.drawable.ic_failed);
            }
            holder.previousText.setText(String.format("%d", stage.getLauncher().getPreviousFlights()));
        } else {
            holder.viewCoreLaunches.setVisibility(View.GONE);
        }

        if (launch.getMission() != null && launch.getMission().getName() != null) {
            if (stage.getLanding() != null) {
                holder.landingInformationTitle.setText(String.format(context.getString(R.string.x_landing_information), launch.getMission().getName()));
            } else {
                holder.landingInformationTitle.setText("Landing Information Unavailable");
            }
        }
        
        holder.landingGroup.setVisibility(View.GONE);
        holder.landingLocationGroup.setVisibility(View.GONE);
        holder.landingTypeGroup.setVisibility(View.GONE);
        holder.landingMore.setVisibility(View.GONE);

        if (stage.getLanding() != null) {
            holder.landingGroup.setVisibility(View.VISIBLE);
            Landing landing = stage.getLanding();

            if (landing.getAttempt() == null) {
                holder.attemptIcon.setImageResource(R.drawable.ic_question_mark);
            } else if (landing.getAttempt()) {
                holder.attemptIcon.setImageResource(R.drawable.ic_checkmark);
            } else if (!landing.getAttempt()) {
                holder.attemptIcon.setImageResource(R.drawable.ic_failed);
            }

            if (landing.getSuccess() == null) {
                holder.successIcon.setImageResource(R.drawable.ic_question_mark);
            } else if (landing.getSuccess()) {
                holder.successIcon.setImageResource(R.drawable.ic_checkmark);
            } else if (!landing.getSuccess()) {
                holder.successIcon.setImageResource(R.drawable.ic_failed);
            }

            if (landing.getDescription().length() != 0) {
                holder.landingDescription.setText(landing.getDescription());
                holder.landingDescription.setOnClickListener(v -> {
                    if (!holder.landingDescription.isExpanded()) {
                        holder.landingDescription.expand();
                    }
                });
            } else {
                holder.landingDescription.setVisibility(View.GONE);
            }

            if (landing.getLandingType() != null && landing.getLandingType().getName() != null) {
                holder.landingType.setText(landing.getLandingType().getName());
                holder.landingLocationGroup.setVisibility(View.VISIBLE);
            }

            if (landing.getLandingLocation() != null && landing.getLandingLocation().getName() != null) {
                holder.landingLocation.setText(landing.getLandingLocation().getName());
                holder.landingTypeGroup.setVisibility(View.VISIBLE);
            }

            if (landing.getLandingLocation() != null
                    && landing.getLandingLocation().getName() != null
                    && landing.getLandingLocation().getDescription() != null
                    && landing.getLandingType() != null
                    && landing.getLandingType().getName() != null
                    && landing.getLandingType().getDescription() != null) {
                holder.landingMore.setVisibility(View.VISIBLE);
                holder.landingMore.setOnClickListener((View v) -> {
                    MaterialDialog dialog = new MaterialDialog.Builder(context)
                            .title(context.getString(R.string.additional_landing_information))
                            .customView(R.layout.landing_information, true)
                            .positiveText("Close")
                            .show();
                    View view = dialog.getCustomView();

                    TextView landingType = view.findViewById(R.id.landing_type);
                    TextView landingTypeDescription = view.findViewById(R.id.landing_type_description);
                    TextView landingLocation = view.findViewById(R.id.landing_location);
                    TextView landingLocationDescription = view.findViewById(R.id.landing_location_description);

                    landingType.setText(landing.getLandingType().getName());
                    landingTypeDescription.setText(landing.getLandingType().getDescription());
                    landingLocation.setText(landing.getLandingLocation().getName());
                    landingLocationDescription.setText(landing.getLandingLocation().getDescription());
                });
            } else {
                holder.landingMore.setVisibility(View.GONE);
            }

        } else {
            holder.landingGroup.setVisibility(View.GONE);
            holder.landingLocationGroup.setVisibility(View.GONE);
            holder.landingTypeGroup.setVisibility(View.GONE);
            holder.landingMore.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return launcherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.core_information)
        TextView coreInformation;
        @BindView(R2.id.core_information_subtitle)
        TextView coreInformationSubtitle;
        @BindView(R2.id.serial_number_title)
        TextView serialNumberTitle;
        @BindView(R2.id.serial_number_text)
        TextView serialNumberText;
        @BindView(R2.id.status_title)
        TextView statusTitle;
        @BindView(R2.id.status_text)
        TextView statusText;
        @BindView(R2.id.previous_title)
        TextView previousTitle;
        @BindView(R2.id.previous_text)
        TextView previousText;
        @BindView(R2.id.flight_proven)
        ImageView flightProven;
        @BindView(R2.id.flight_proven_title)
        TextView flightProvenTitle;
        @BindView(R2.id.landing_attempt_title)
        TextView landingAttemptTitle;
        @BindView(R2.id.attempt_icon)
        ImageView attemptIcon;
        @BindView(R2.id.landing_success_title)
        TextView landingSuccessTitle;
        @BindView(R2.id.success_icon)
        ImageView successIcon;
        @BindView(R2.id.landing_description)
        ExpandableTextView landingDescription;
        @BindView(R2.id.landing_type)
        TextView landingType;
        @BindView(R2.id.landing_type_title)
        TextView landingTypeTitle;
        @BindView(R2.id.landing_location)
        TextView landingLocation;
        @BindView(R2.id.landing_location_title)
        TextView landingLocationTitle;
        @BindView(R2.id.landing_more)
        Button landingMore;
        @BindView(R2.id.view_core_launches)
        AppCompatButton viewCoreLaunches;
        @BindView(R2.id.landing_group)
        Group landingGroup;
        @BindView(R2.id.landing_group_landingtype)
        Group landingLocationGroup;
        @BindView(R2.id.landing_group_landinglocation)
        Group landingTypeGroup;
        @BindView(R2.id.details)
        TextView details;
        @BindView(R2.id.landing_information_title)
        TextView landingInformationTitle;
        @BindView(R2.id.coreImage)
        ImageView coreImage;


        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
