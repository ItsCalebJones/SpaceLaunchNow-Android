package me.calebjones.spacelaunchnow.ui.launchdetail.fragments.mission;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.Group;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.main.Landing;
import me.calebjones.spacelaunchnow.data.models.main.Launcher;
import me.calebjones.spacelaunchnow.data.models.main.Stage;
import me.calebjones.spacelaunchnow.ui.launches.launcher.LauncherLaunchActivity;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class StageInformationAdapter extends RecyclerView.Adapter<StageInformationAdapter.ViewHolder> {
    public int position;
    private List<Stage> launcherList;
    private Context context;

    public StageInformationAdapter(List<Stage> launchers, Context context) {
        launcherList = launchers;
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
        Stage stage = launcherList.get(position);
        if (stage.getLauncher() != null) {
            Launcher launcher = stage.getLauncher();
            holder.viewCoreLaunches.setVisibility(View.VISIBLE);
            holder.viewCoreLaunches.setText(String.format("View %s Launches", stage.getLauncher().getSerialNumber()));
            holder.viewCoreLaunches.setOnClickListener(v -> {
                Intent launches = new Intent(context, LauncherLaunchActivity.class);
                launches.putExtra("serialNumber", launcher.getSerialNumber());
                context.startActivity(launches);
            });
            holder.coreInformation.setText(String.format("%s Information", launcher.getSerialNumber()));
            if (stage.getType() != null) {
                holder.coreInformationSubtitle.setText(String.format("First Stage - %s", stage.getType()));
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

        if (stage.getLanding() != null) {
            holder.landingGroup.setVisibility(View.VISIBLE);
            Landing landing = stage.getLanding();

            if (landing.getAttempt() == null) {
                holder.flightProven.setImageResource(R.drawable.ic_question_mark);
            } else if (landing.getAttempt()) {
                holder.flightProven.setImageResource(R.drawable.ic_checkmark);
            } else if (!landing.getAttempt()) {
                holder.flightProven.setImageResource(R.drawable.ic_failed);
            }

            if (landing.getSuccess() == null) {
                holder.flightProven.setImageResource(R.drawable.ic_question_mark);
            } else if (landing.getSuccess()) {
                holder.flightProven.setImageResource(R.drawable.ic_checkmark);
            } else if (!landing.getSuccess()) {
                holder.flightProven.setImageResource(R.drawable.ic_failed);
            }

            holder.landingDescription.setText(landing.getDescription());
            holder.landingDescription.setOnClickListener(v -> {
                if (!holder.landingDescription.isExpanded()) {
                    holder.landingDescription.expand();
                }
            });

            holder.landingType.setText(landing.getLandingType().getName());
            holder.landingLocation.setText(landing.getLandingLocation().getName());

            holder.landingMore.setOnClickListener((View v) -> {
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title("Additional Landing Information")
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
            holder.landingGroup.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return launcherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.core_information)
        TextView coreInformation;
        @BindView(R.id.core_information_subtitle)
        TextView coreInformationSubtitle;
        @BindView(R.id.serial_number_title)
        TextView serialNumberTitle;
        @BindView(R.id.serial_number_text)
        TextView serialNumberText;
        @BindView(R.id.status_title)
        TextView statusTitle;
        @BindView(R.id.status_text)
        TextView statusText;
        @BindView(R.id.previous_title)
        TextView previousTitle;
        @BindView(R.id.previous_text)
        TextView previousText;
        @BindView(R.id.flight_proven)
        ImageView flightProven;
        @BindView(R.id.flight_proven_title)
        TextView flightProvenTitle;
        @BindView(R.id.landing_attempt_title)
        TextView landingAttemptTitle;
        @BindView(R.id.attempt_icon)
        ImageView attemptIcon;
        @BindView(R.id.landing_success_title)
        TextView landingSuccessTitle;
        @BindView(R.id.success_icon)
        ImageView successIcon;
        @BindView(R.id.landing_description)
        ExpandableTextView landingDescription;
        @BindView(R.id.landing_type)
        TextView landingType;
        @BindView(R.id.landing_location)
        TextView landingLocation;
        @BindView(R.id.landing_more)
        Button landingMore;
        @BindView(R.id.view_core_launches)
        Button viewCoreLaunches;
        @BindView(R.id.landing_group)
        Group landingGroup;
        @BindView(R.id.details)
        TextView details;


        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

//        @OnClick(R.id.read_more)
//        public void onReadMoreClicked() {
//            if (landingLocationDescriptionExpandableLayout.isExpanded()) {
//                readMore.setText("Read More");
//            } else {
//                readMore.setText("Close");
//            }
//            landingLocationDescriptionExpandableLayout.toggle();
//            landingTypeDescriptionExpandableLayout.toggle();
//        }
    }
}
