package me.calebjones.spacelaunchnow.ui.main.next;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.core.app.ShareCompat;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.ui.views.DialogAdapter;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.common.youtube.models.VideoListItem;
import me.calebjones.spacelaunchnow.data.models.main.Landing;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.VidURL;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherStage;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.common.ui.views.CountDownTimer;
import me.calebjones.spacelaunchnow.common.ui.views.custom.CountDownView;
import timber.log.Timber;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> implements SectionIndexer {

    public int position;
    private RealmList<Launch> launchList;
    private Context context;


    public CardAdapter(Context context) {
        launchList = new RealmList<>();
        this.context = context;
    }



    public void addItems(List<Launch> launchList) {
        if (this.launchList != null) {
            this.launchList.addAll(launchList);
        } else {
            this.launchList = new RealmList<>();
            this.launchList.addAll(launchList);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        if (launchList != null) {
            launchList.clear();
            this.notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "StringFormatMatches"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        Launch launchItem = launchList.get(i);
        if (launchItem.isValid()) {
            Timber.i("Binding %s", launchItem.getName());
            String title;
            try {
                if (launchItem.isValid()) {
                    if (launchItem.getRocket().getConfiguration() != null) {
                        if (launchItem.getLaunchServiceProvider() != null) {
                            String providerName;

                            // if the name is too long lets use the abbrev name if its useable
                            if (launchItem.getLaunchServiceProvider().getName().length() > 15
                                    && launchItem.getLaunchServiceProvider().getAbbrev() != null
                                    && launchItem.getLaunchServiceProvider().getAbbrev().length() > 0) {
                                providerName = launchItem.getLaunchServiceProvider().getAbbrev();
                            } else {
                                providerName = launchItem.getLaunchServiceProvider().getName();
                            }
                            title = providerName + " | " + (launchItem.getRocket().getConfiguration().getName());
                        }  else {
                            title = launchItem.getRocket().getConfiguration().getName();
                        }
                    } else if (launchItem.getName() != null) {
                        title = launchItem.getName();
                    } else {
                        Timber.e("Error - launch item is effectively null.");
                        title = context.getString(R.string.error_unknown_launch);
                    }

                    holder.title.setText(title);

                    //Retrieve missionType
                    if (launchItem.getMission() != null) {
                        Utils.setCategoryIcon(holder.categoryIcon, launchItem.getMission().getTypeName());
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_unknown_white);
                    }

                    holder.landingView.setVisibility(View.INVISIBLE);
                    if (launchItem.getRocket().getLauncherStage() != null && launchItem.getRocket().getLauncherStage().size() >= 1) {
                        if (launchItem.getRocket().getLauncherStage().size() > 1) {
                            boolean landingAttempt = false;
                            StringBuilder stagesText = new StringBuilder();
                            for (LauncherStage stage : launchItem.getRocket().getLauncherStage()) {
                                if (stage.getLanding() != null) {
                                    if (stage.getLanding().getLandingLocation() != null) {
                                        landingAttempt = true;
                                        stagesText.append(stage.getLanding().getLandingLocation().getAbbrev()).append(" ");
                                    }
                                }
                            }
                            if (landingAttempt) {
                                holder.landingView.setVisibility(View.VISIBLE);
                                holder.landing.setText(stagesText.toString());
                            }
                        } else if (launchItem.getRocket().getLauncherStage().size() == 1) {
                            if (launchItem.getRocket().getLauncherStage().first().getLanding() != null) {
                                Landing landing = launchItem.getRocket().getLauncherStage().first().getLanding();
                                if (landing.getLandingLocation() != null) {
                                    holder.landingView.setVisibility(View.VISIBLE);
                                    holder.landing.setText(landing.getLandingLocation().getAbbrev());
                                }
                            }
                        }
                    }

                    if (launchItem.getRocket().getConfiguration().getImageUrl() != null) {
                        holder.launchImage.setVisibility(View.VISIBLE);
                        GlideApp.with(context)
                                .load(launchItem.getRocket().getConfiguration().getImageUrl())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.launchImage);
                    } else if (launchItem.getLaunchServiceProvider() != null && launchItem.getLaunchServiceProvider().getImageUrl() != null) {
                        holder.launchImage.setVisibility(View.VISIBLE);
                        GlideApp.with(context)
                                .load(launchItem.getLaunchServiceProvider().getImageUrl())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.launchImage);
                    } else if (launchItem.getLaunchServiceProvider() != null && launchItem.getLaunchServiceProvider().getLogoUrl() != null) {
                        holder.launchImage.setVisibility(View.VISIBLE);
                        GlideApp.with(context)
                                .load(launchItem.getLaunchServiceProvider().getLogoUrl())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.launchImage);
                    } else {
                        holder.launchImage.setVisibility(View.GONE);
                    }

                    holder.countDownView.setLaunch(launchItem);

//                    holder.launchDateCompact.setText(Utils.getStatusBasedDateTimeFormat(launchItem.getNet(),
//                            launchItem.getStatus(),
//                            context));
                    holder.launchDateCompact.setText(
                            Utils.getSimpleDateTimeFormatForUIWithPrecision(
                                    launchItem.getNetPrecision()).format(launchItem.getNet()));

                    if (launchItem.getVidURLs() != null) {
                        if (launchItem.getVidURLs().size() == 0) {
                            holder.watchButton.setVisibility(View.GONE);
                        } else {
                            holder.watchButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.watchButton.setVisibility(View.GONE);
                    }


                    if (launchItem.getMission() != null) {
                        holder.contentMission.setText(launchItem.getMission().getName());
                        String description = launchItem.getMission().getDescription();
                        if (description.length() > 0) {
                            holder.contentMissionDescription.setText(description);
                        }
                    } else {
                        String[] separated = launchItem.getName().split(" \\| ");
                        try {
                            if (separated.length > 0 && separated[1].length() > 4) {
                                holder.contentMission.setText(separated[1].trim());
                            } else {
                                holder.contentMission.setText("Unknown Mission");
                            }
                        } catch (ArrayIndexOutOfBoundsException exception) {
                            holder.contentMission.setText("Unknown Mission");
                        }
                    }

                    //If pad and agency_menu exist add it to location, otherwise get whats always available
                    if (launchItem.getPad().getLocation() != null) {
                        holder.location.setText(launchItem.getPad().getLocation().getName());
                    } else {
                        holder.location.setText("");
                    }
                }
            } catch (NullPointerException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {

        if (position >= getItemCount()) {
            position = getItemCount() - 1;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.categoryIcon)
        ImageView categoryIcon;
        @BindView(R.id.launch_rocket)
        TextView title;
        @BindView(R.id.location)
        TextView location;
        @BindView(R.id.launch_date_compact)
        TextView launchDateCompact;
        @BindView(R.id.launch_image)
        ImageView launchImage;
        @BindView(R.id.watchButton)
        View watchButton;
        @BindView(R.id.shareButton)
        View shareButton;
        @BindView(R.id.exploreButton)
        Button exploreButton;
        @BindView(R.id.content_mission)
        TextView contentMission;
        @BindView(R.id.content_mission_description)
        TextView contentMissionDescription;
        @BindView(R.id.landing_icon)
        ImageView landingIcon;
        @BindView(R.id.landing)
        TextView landing;
        @BindView(R.id.landing_pill)
        View landingView;
        @BindView(R.id.background)
        View titleBackground;
        @BindView(R.id.countdown_layout)
        CountDownView countDownView;
        public View layout;
        public CountDownTimer timer;
        public Disposable var;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            layout = view;
            ButterKnife.bind(this, view);

            shareButton.setOnClickListener(this);
            exploreButton.setOnClickListener(this);
            watchButton.setOnClickListener(this);
            titleBackground.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            Timber.d("onClick at %s", position);

            final Launch launch = launchList.get(position);
            if (launch != null && launch.isValid()) {
                switch (v.getId()) {
                    case R.id.watchButton:
                        Timber.d("Watch: %s", launch.getVidURLs().size());
                        Analytics.getInstance().sendButtonClicked("Watch Button - Opening Dialogue");
                        if (launch.getVidURLs().size() > 0) {
                            final DialogAdapter adapter = new DialogAdapter((index, item, longClick) -> {
                                try {
                                    if (longClick) {
                                        Intent sendIntent1 = new Intent();
                                        sendIntent1.setAction(Intent.ACTION_SEND);
                                        sendIntent1.putExtra(Intent.EXTRA_TEXT, item.getVideoURL().toString()); // Simple text and URL to share
                                        sendIntent1.setType("text/plain");
                                        context.startActivity(sendIntent1);
                                    } else {
                                        Uri watchUri = Uri.parse(item.getVideoURL().toString());
                                        Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                        context.startActivity(i);
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    Timber.e(e);
                                    Toast.makeText(context, "Ops, an error occurred.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            for (VidURL s : launch.getVidURLs()) {
                                //Do your stuff here
                                adapter.add(new VideoListItem.Builder(context)
                                        .content(s.getName())
                                        .videoURL(s.getUrl())
                                        .build());
                            }

                            MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                                    .title(R.string.source)
                                    .content(R.string.long_press_share)
                                    .adapter(adapter, null)
                                    .negativeText(R.string.cancel);
                            builder.show();
                        }
                        break;
                    case R.id.exploreButton:
                        Timber.d("Explore: %s", launchList.get(position).getId());
                        Analytics.getInstance().sendButtonClicked("Explore Button", launch.getName());

                        Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
                        exploreIntent.putExtra("TYPE", "launch");
                        exploreIntent.putExtra("launchID", launch.getId());
                        context.startActivity(exploreIntent);
                        break;
                    case R.id.shareButton:
                        ShareCompat.IntentBuilder.from((Activity) context)
                                .setType("text/plain")
                                .setChooserTitle("Share: " + launch.getName())
                                .setText(launch.getSlug())
                                .startChooser();
                        Analytics.getInstance().sendLaunchShared("Explore Button", launch.getName() + "-" + launch.getId().toString());
                        break;
                    case R.id.background:
                        Timber.d("Explore: %s", launchList.get(position).getId());
                        Analytics.getInstance().sendButtonClicked("Title Card", launch.getName());

                        Intent titleIntent = new Intent(context, LaunchDetailActivity.class);
                        titleIntent.putExtra("TYPE", "launch");
                        titleIntent.putExtra("launchID", launch.getId());
                        context.startActivity(titleIntent);
                        break;
                }
            }
        }
    }
}