package me.calebjones.spacelaunchnow.ui.main.next;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.app.ShareCompat;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.crashlytics.android.Crashlytics;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.util.DialogAdapter;
import me.calebjones.spacelaunchnow.data.models.main.Landing;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.Stage;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.views.CountDownTimer;
import me.calebjones.spacelaunchnow.utils.views.custom.CountDownView;
import me.calebjones.spacelaunchnow.utils.youtube.YouTubeAPIHelper;
import me.calebjones.spacelaunchnow.utils.youtube.models.Video;
import me.calebjones.spacelaunchnow.utils.youtube.models.VideoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Adapts UpcomingLaunch data to the LaunchFragment
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> implements SectionIndexer {

    public int position;
    private RealmList<Launch> launchList;
    private Context context;
    private SimpleDateFormat fullDate;
    private SimpleDateFormat shortDate;


    public CardAdapter(Context context) {
        launchList = new RealmList<>();
        this.context = context;

        if (DateFormat.is24HourFormat(context)) {
            fullDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy HH:mm zzz");
        } else {
            fullDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy h:mm a zzz");
        }
        fullDate.toLocalizedPattern();
        shortDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy");
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
        Timber.i("Binding %s", launchItem.getName());
        String title;
        try {
            if (launchItem.isValid()) {
                if (launchItem.getRocket().getConfiguration() != null) {
                    if (launchItem.getRocket().getConfiguration().getLaunchServiceProvider() != null) {
                        title = launchItem.getRocket().getConfiguration().getLaunchServiceProvider().getName() + " | " + (launchItem.getRocket().getConfiguration().getName());
                    } else {
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
                    Utils.setCategoryIcon(holder.categoryIcon, launchItem.getMission().getTypeName(), true);
                } else {
                    holder.categoryIcon.setImageResource(R.drawable.ic_unknown_white);
                }

                holder.landingView.setVisibility(View.INVISIBLE);
                if (launchItem.getRocket().getFirstStage() != null && launchItem.getRocket().getFirstStage().size() >= 1) {
                    if (launchItem.getRocket().getFirstStage().size() > 1) {
                        StringBuilder stagesText = new StringBuilder();
                        for (Stage stage : launchItem.getRocket().getFirstStage()) {
                            if (stage.getLanding().getLandingLocation() != null) {
                                stagesText.append(stage.getLanding().getLandingLocation().getAbbrev()).append(" ");
                            }
                        }
                        holder.landingView.setVisibility(View.VISIBLE);
                        holder.landing.setText(stagesText.toString());
                    } else if (launchItem.getRocket().getFirstStage().size() == 1) {
                        if (launchItem.getRocket().getFirstStage().first().getLanding() != null) {
                            Landing landing = launchItem.getRocket().getFirstStage().first().getLanding();
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
                } else if (launchItem.getRocket().getConfiguration().getLaunchServiceProvider() != null && launchItem.getRocket().getConfiguration().getLaunchServiceProvider().getImageUrl() != null) {
                    holder.launchImage.setVisibility(View.VISIBLE);
                    GlideApp.with(context)
                            .load(launchItem.getRocket().getConfiguration().getLaunchServiceProvider().getImageUrl())
                            .placeholder(R.drawable.placeholder)
                            .into(holder.launchImage);
                } else if (launchItem.getRocket().getConfiguration().getLaunchServiceProvider() != null && launchItem.getRocket().getConfiguration().getLaunchServiceProvider().getLogoUrl() != null) {
                    holder.launchImage.setVisibility(View.VISIBLE);
                    GlideApp.with(context)
                            .load(launchItem.getRocket().getConfiguration().getLaunchServiceProvider().getLogoUrl())
                            .placeholder(R.drawable.placeholder)
                            .into(holder.launchImage);
                } else {
                    holder.launchImage.setVisibility(View.GONE);
                }

                holder.countDownView.setLaunch(launchItem);

                //Get launch date
                if (launchItem.getStatus().getId() == 2) {

                    if (launchItem.getNet() != null) {
                        //Get launch date


                        Date date = launchItem.getNet();
                        String launchTime = shortDate.format(date);
                        if (launchItem.getTbddate()) {
                            launchTime = launchTime + " " + context.getString(R.string.unconfirmed);
                        }
                        holder.launchDateCompact.setText(launchTime);
                    }
                } else {
                    Date date = launchItem.getNet();
                    holder.launchDateCompact.setText(fullDate.format(date));
                }

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
            Crashlytics.logException(e);
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
            Intent sendIntent = new Intent();

            Date date = launch.getNet();
            String launchDate = fullDate.format(date);

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
                                    sendIntent1.putExtra(Intent.EXTRA_TEXT, launch.getVidURLs().get(index).getVal()); // Simple text and URL to share
                                    sendIntent1.setType("text/plain");
                                    context.startActivity(sendIntent1);
                                    Analytics.getInstance().sendButtonClickedWithURL("Watch Button - URL Long Clicked", launch.getVidURLs().get(index).getVal());
                                } else {
                                    Uri watchUri = Uri.parse(launch.getVidURLs().get(index).getVal());
                                    Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                    context.startActivity(i);
                                    Analytics.getInstance().sendButtonClickedWithURL("Watch Button - URL", launch.getVidURLs().get(index).getVal());
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                Timber.e(e);
                                Toast.makeText(context, "Ops, an error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        for (RealmStr s : launch.getVidURLs()) {
                            //Do your stuff here
                            try {
                                URI uri = new URI(s.getVal());

                                String name;
                                YouTubeAPIHelper youTubeAPIHelper = new YouTubeAPIHelper(context, context.getResources().getString(R.string.GoogleMapsKey));
                                if (uri.getHost().contains("youtube")){
                                    name = "YouTube";
                                    String youTubeURL = getYouTubeID(s.getVal());
                                    if (youTubeURL.contains("spacex/live")){
                                        adapter.add(new MaterialSimpleListItem.Builder(context)
                                                .content("YouTube - SpaceX Livestream")
                                                .build());
                                    } else {
                                        youTubeAPIHelper.getVideoById(youTubeURL,
                                                new Callback<VideoResponse>() {
                                            @Override
                                            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                                                if (response.isSuccessful()) {
                                                    if (response.body() != null) {
                                                        List<Video> videos = response.body().getVideos();
                                                        if (videos.size() > 0) {
                                                            try {
                                                                adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                        .content(videos.get(0).getSnippet().getTitle())
                                                                        .build());
                                                            } catch (Exception e){
                                                                adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                        .content(name)
                                                                        .build());
                                                            }
                                                        } else {
                                                            adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                    .content(name)
                                                                    .build());
                                                        }
                                                    } else {
                                                        adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                .content(name)
                                                                .build());
                                                    }
                                                } else {
                                                    adapter.add(new MaterialSimpleListItem.Builder(context)
                                                            .content(name)
                                                            .build());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<VideoResponse> call, Throwable t) {
                                                adapter.add(new MaterialSimpleListItem.Builder(context)
                                                        .content(name)
                                                        .build());
                                            }
                                        });
                                    }
                                } else {
                                    name = uri.getHost();
                                    adapter.add(new MaterialSimpleListItem.Builder(context)
                                            .content(name)
                                            .build());
                                }

                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
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
                    String message;
                    if (launch.getVidURLs().size() > 0) {
                        if (launch.getPad().getLocation() != null) {

                            message = launch.getName() + " launching from "
                                    + launch.getPad().getLocation().getName() + "\n\n"
                                    + launchDate;
                        } else if (launch.getPad().getLocation() != null) {
                            message = launch.getName() + " launching from "
                                    + launch.getPad().getLocation().getName() + "\n\n"
                                    + launchDate;
                        } else {
                            message = launch.getName()
                                    + "\n\n"
                                    + launchDate;
                        }
                    } else {
                        if (launch.getPad().getLocation() != null) {

                            message = launch.getName() + " launching from "
                                    + launch.getPad().getLocation().getName() + "\n\n"
                                    + launchDate;
                        } else if (launch.getPad().getLocation() != null) {
                            message = launch.getName() + " launching from "
                                    + launch.getPad().getLocation().getName() + "\n\n"
                                    + launchDate;
                        } else {
                            message = launch.getName()
                                    + "\n\n"
                                    + launchDate;
                        }
                    }
                    ShareCompat.IntentBuilder.from((Activity) context)
                            .setType("text/plain")
                            .setChooserTitle("Share: " + launch.getName())
                            .setText(String.format("%s\n\nWatch Live: %s", message, launch.getSlug()))
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

    private String getYouTubeID(String vidURL) {
        final String regex = "(youtu\\.be\\/|youtube\\.com\\/(watch\\?(.*&)?v=|(embed|v)\\/|c\\/))([a-zA-Z0-9_-]{11}|[a-zA-Z].*)";
        final Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(vidURL);
        Timber.v("Checking for match of %s", vidURL);
        if (matcher.find() && (matcher.group(1) != null || matcher.group(2) != null) && matcher.group(5) != null) {
            return matcher.group(5);
        }
        return null;
    }
}
