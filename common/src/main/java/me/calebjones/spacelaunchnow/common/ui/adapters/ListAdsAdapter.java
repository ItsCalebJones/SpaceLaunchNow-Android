package me.calebjones.spacelaunchnow.common.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
public class ListAdsAdapter extends RecyclerView.Adapter {
    public int position;
    private RealmList<LaunchList> launchList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static ListPreferences sharedPreference;
    private final ListPreloader.PreloadSizeProvider sizeProvider = new ViewPreloadSizeProvider();
    private SimpleDateFormat sdf;
    private SimpleDateFormat df;
    private int color;
    private ColorDrawable colorDrawable;

    // The list of Native ads and menu items.
    private List<Object> mRecyclerViewItems = new ArrayList<>();

    public static final int ITEMS_PER_AD = 8;

    // A menu item view type.
    private static final int CONTENT_TYPE = 0;

    private static final int AD_TYPE = 1;

    public ListAdsAdapter(Context context, boolean night) {
        rightNow = Calendar.getInstance();
        launchList = new RealmList<>();
        sharedPreference = ListPreferences.getInstance(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
        if (sharedPref.getBoolean("local_time", true)) {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
        } else {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy zzz");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        if (sharedPref.getBoolean("24_hour_mode", false)) {
            df = Utils.getSimpleDateFormatForUI("EEEE, MMMM dd, yyyy - HH:mm");
        } else {
            df = Utils.getSimpleDateFormatForUI("EEEE, MMMM dd, yyyy - hh:mm a zzz");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        this.night = night;
        if (night) {
            color = ContextCompat.getColor(mContext, R.color.material_color_white);
            colorDrawable = new ColorDrawable(color);
        } else {
            color = ContextCompat.getColor(mContext, R.color.material_color_black);
            colorDrawable = new ColorDrawable(color);
        }
    }

    public void addItems(List<LaunchList> launchList) {
        mRecyclerViewItems = new ArrayList<>();

        if (this.launchList == null) {
            this.launchList = new RealmList<>();
        }
        this.launchList.addAll(launchList);

        mRecyclerViewItems.addAll(launchList);
        addNativeAdView();
        loadBannerAds();
        this.notifyDataSetChanged();
    }

    public void clear() {
        launchList.clear();
        notifyDataSetChanged();
    }

    private void addNativeAdView() {

        // Loop through the items array and place a new Native Express ad in every ith position in
        // the items List.,
        for (int i = 0; i <= mRecyclerViewItems.size(); i += ITEMS_PER_AD) {
//            final UnifiedNativeAdView adView = new UnifiedNativeAdView(mContext);
//            mRecyclerViewItems.add(i, adView);

            final AdView adView = new AdView(mContext);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId("ca-app-pub-9824528399164059/9959827876");
            mRecyclerViewItems.add(i, adView);
        }

    }

    /**
     * Sets up and loads the banner ads.
     */
    private void loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(0);
    }

    /**
     * Loads the banner ads in the items list.
     */
    private void loadBannerAd(final int index) {

        if (index >= mRecyclerViewItems.size()) {
            return;
        }

        Object item = mRecyclerViewItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad"
                    + " ad.");
        }

        final AdView adView = (AdView) item;

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(
                new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        // The previous banner ad loaded successfully, call this method again to
                        // load the next ad in the items list.
                        loadBannerAd(index + ITEMS_PER_AD);
                    }
                });

        // Load the banner ad.
        adView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public int getItemViewType(int position) {
        return (position % ITEMS_PER_AD == 0) ? AD_TYPE : CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Timber.v("onCreate ViewHolder.");
        switch (viewType) {
            case CONTENT_TYPE:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.launch_list_item, viewGroup, false);
                return new ContentViewHolder(view);
            case AD_TYPE:

            default:
                View bannerLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.banner_ad_container,
                        viewGroup, false);
                return new AdViewHolder(bannerLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        this.position = position;

        switch (viewType) {
            case CONTENT_TYPE: {
                final LaunchList launchItem = (LaunchList) mRecyclerViewItems.get(position);
                final ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                String[] title;
                String launchDate;
                //Retrieve missionType
                if (launchItem.getImage() != null) {
                    GlideApp.with(mContext)
                            .load(launchItem.getImage())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .circleCrop()
                            .into(contentViewHolder.categoryIcon);
                } else {
                    if (launchItem.getMission() != null) {

                        GlideApp.with(mContext)
                                .load(Utils.getCategoryIcon(launchItem.getMissionType()))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .circleCrop()
                                .transform(new ColorFilterTransformation(color))
                                .into(contentViewHolder.categoryIcon);

                    } else {
                        GlideApp.with(mContext)
                                .load(R.drawable.ic_unknown)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .circleCrop()
                                .transform(new ColorFilterTransformation(color))
                                .into(contentViewHolder.categoryIcon);
                    }
                }

                if (launchItem.getStatus() != null && launchItem.getStatus().getId() == 2) {
                    //Get launch date
                    launchDate = sdf.format(launchItem.getNet());

                    contentViewHolder.launch_date.setText(launchDate);
                } else {
                    launchDate = sdf.format(launchItem.getNet());
                    contentViewHolder.launch_date.setText(launchDate);
                }

                //If pad and agency exist add it to location, otherwise get whats always available
                if (launchItem.getLocation() != null) {
                    contentViewHolder.location.setText(launchItem.getLocation());
                } else {
                    contentViewHolder.location.setText(mContext.getString(R.string.click_for_info));
                }

                if (launchItem.getName() != null) {
                    title = launchItem.getName().split("\\|");
                    try {
                        if (title.length > 0) {
                            contentViewHolder.title.setText(title[1].trim());
                            contentViewHolder.mission.setText(title[0].trim());
                        } else {
                            contentViewHolder.title.setText(launchItem.getName());
                            if (launchItem.getMission() != null) {
                                contentViewHolder.title.setText(launchItem.getMission());
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException exception) {
                        contentViewHolder.title.setText(launchItem.getName());
                        if (launchItem.getMission() != null) {
                            contentViewHolder.title.setText(launchItem.getMission());
                        }

                    }
                }

                if (launchItem.getLanding() != null) {
                    contentViewHolder.landingCard.setVisibility(View.VISIBLE);
                    contentViewHolder.landingLocation.setText(launchItem.getLanding());
                    contentViewHolder.landingCard.setCardBackgroundColor(LaunchStatusUtil.getLandingStatusColor(mContext, launchItem.getLandingSuccess()));
                } else {
                    contentViewHolder.landingCard.setVisibility(View.GONE);
                }

                if (launchItem.getOrbit() != null) {
                    contentViewHolder.orbitCard.setVisibility(View.VISIBLE);
                    contentViewHolder.orbitName.setText(launchItem.getOrbit());
                } else {
                    contentViewHolder.orbitCard.setVisibility(View.GONE);
                }

                contentViewHolder.status.setText(launchItem.getStatus().getName());
                contentViewHolder.statusCard.setCardBackgroundColor(LaunchStatusUtil.getLaunchStatusColor(mContext, launchItem.getStatus().getId()));
                break;
            }

            case AD_TYPE: {

            }

            default: {

//                final AdViewHolder adViewHolder = (AdViewHolder) holder;
//
//                AdLoader.Builder builder = new AdLoader.Builder(mContext, "ca-app-pub-9824528399164059/1085124416");
//
//                builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
//                    @Override
//                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
//
//                        adViewHolder.mNativeAppInstallAdView.setImageView(adViewHolder.mAdImage);
//                        adViewHolder.mNativeAppInstallAdView.setIconView(adViewHolder.mAdIcon);
//                        adViewHolder.mNativeAppInstallAdView.setHeadlineView(adViewHolder.mAdHeadline);
//                        adViewHolder.mNativeAppInstallAdView.setBodyView(adViewHolder.mAdBody);
//                        adViewHolder.mNativeAppInstallAdView.setCallToActionView(adViewHolder.mAdButton);
//
//                        ((TextView) adViewHolder.mNativeAppInstallAdView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
//                        ((TextView) adViewHolder.mNativeAppInstallAdView.getBodyView()).setText(nativeAppInstallAd.getBody());
//                        ((Button) adViewHolder.mNativeAppInstallAdView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
//                        ((ImageView) adViewHolder.mNativeAppInstallAdView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon().getDrawable());
//
//                        List<NativeAd.Image> images = nativeAppInstallAd.getImages();
//
//                        if (images.size() > 0) {
//                            ((ImageView) adViewHolder.mNativeAppInstallAdView.getImageView()).setImageDrawable(images.get(0).getDrawable());
//                        }
//
//                        // Assign native ad object to the native view.
//                        adViewHolder.mNativeAppInstallAdView.setNativeAd(nativeAppInstallAd);
//
//                        adViewHolder.mAdParentView.removeAllViews();
//                        adViewHolder.mAdParentView.addView(adViewHolder.mNativeAppInstallAdView);
//                    }
//                });
//
//                adViewHolder.mNativeAppInstallAdView.setVisibility(View.INVISIBLE);
//
//                AdLoader adLoader = builder.withAdListener(new AdListener() {
//
//                    @Override
//                    public void onAdLoaded() {
//                        Timber.e("loaded native ad");
//                        adViewHolder.mNativeAppInstallAdView.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(int errorCode) {
//                    }
//                }).build();
//
//                adLoader.loadAd(new AdRequest.Builder().build());
//                break;

                AdViewHolder bannerHolder = (AdViewHolder) holder;
                AdView adView = (AdView) mRecyclerViewItems.get(position);
                ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                // The AdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // AdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled AdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the banner ad to the ad view.
                adCardView.addView(adView);

            }

        }

    }


    public class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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


        //Add content to the card
        public ContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            categoryIcon.setOnClickListener(this);
            title.setOnClickListener(this);
            location.setOnClickListener(this);
            launch_date.setOnClickListener(this);
            mission.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final LaunchList launch = launchList.get(getAdapterPosition());

            Intent intent = new Intent(mContext, LaunchDetailActivity.class);
            intent.putExtra("TYPE", "launch");
            intent.putExtra("launchID", launch.getId());
            mContext.startActivity(intent);
        }
    }

//    public class AdViewHolder extends RecyclerView.ViewHolder {
//
//        CardView mAdParentView;
//        NativeAppInstallAdView mNativeAppInstallAdView;
//        ImageView mAdImage;
//        ImageView mAdIcon;
//        TextView mAdHeadline;
//        TextView mAdBody;
//        Button mAdButton;
//
//        public AdViewHolder(View itemView) {
//            super(itemView);
//            mAdParentView = (CardView) itemView.findViewById(R.id.adCardView);
//            mNativeAppInstallAdView = (NativeAppInstallAdView) itemView.findViewById(R.id.nativeAppInstallAdView);
//            mAdImage = (ImageView) itemView.findViewById(R.id.appinstall_image);
//            mAdIcon = (ImageView) itemView.findViewById(R.id.appinstall_app_icon);
//            mAdHeadline = (TextView) itemView.findViewById(R.id.appinstall_headline);
//            mAdBody = (TextView) itemView.findViewById(R.id.appinstall_body);
//            mAdButton = (Button) itemView.findViewById(R.id.appinstall_call_to_action);
//
//        }
//
//    }

    /**
     * The {@link AdViewHolder} class.
     */
    public class AdViewHolder extends RecyclerView.ViewHolder {

        AdViewHolder(View view) {
            super(view);
        }
    }


}

