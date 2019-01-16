package me.calebjones.spacelaunchnow.common.content.wear;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.base.BaseManager;
import me.calebjones.spacelaunchnow.common.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.common.utils.transformations.SaturationTransformation;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.common.GlideApp;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.data.models.Constants.*;

public class WearWatchfaceManager extends BaseManager {

    private Context context;
    private static final String WEAR_APP_CAPABILITY = "verify_remote_launch_spacelaunchnow_wear_app";

    public WearWatchfaceManager(Context context) {
        super(context);
        this.context = context;
    }

    // Create a data map and put data in it
    public void updateWear() {
        try {
            CapabilityInfo capabilityInfo = Tasks.await(
                    Wearable.getCapabilityClient(context).getCapability(
                            WEAR_APP_CAPABILITY, CapabilityClient.FILTER_REACHABLE));

            Set<Node> connectedNodes = capabilityInfo.getNodes();
            if (isWearAppConnected(connectedNodes)) checkLaunch();
        } catch (ExecutionException | InterruptedException  e) {
            if (!e.getLocalizedMessage().contains("Wearable.API is not available on this device")) {
                Timber.e(e);
            }
        }
    }



    private boolean isWearAppConnected(Set<Node> nodes) {
        return nodes.size() > 0;
    }

    // TODO Need to fix this for SLN 3.0
    private void checkLaunch() {
        mRealm = Realm.getDefaultInstance();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            RealmResults<Launch> launches = QueryBuilder.buildSwitchQuery(context, mRealm);
            Launch launch = launches.first();
//            Launch launch = mRealm.where(Launch.class).greaterThan("net", new Date()).findAllSorted("net").first();
            if (launch != null && launch.getName() != null && launch.getNet() != null) {
                Timber.v("Sending data to wear: %s", launch.getName());

                boolean dynamic = sharedPref.getBoolean("supporter_dynamic_background", false);
                boolean modify = sharedPref.getBoolean("wear_background_blur", true);
                if (dynamic) {
                    if (launch.getRocket().getConfiguration().getName() != null) {
                        if (launch.getRocket().getConfiguration().getImageUrl() != null && launch.getRocket().getConfiguration().getImageUrl().length() > 0 && !launch.getRocket().getConfiguration().getImageUrl().contains("placeholder")) {
                            Timber.v("Sending image %s", launch.getRocket().getConfiguration().getImageUrl());
                            sendDataToWear(launch.getRocket().getConfiguration().getImageUrl(), launch, modify);
                        } else {
                            String query;
                            if (launch.getRocket().getConfiguration().getName().contains("Space Shuttle")) {
                                query = "Space Shuttle";
                            } else {
                                query = launch.getRocket().getConfiguration().getName();
                            }

                            LauncherConfig launchVehicle = mRealm.where(LauncherConfig.class)
                                    .contains("name", query)
                                    .findFirst();
                            if (launchVehicle != null && launchVehicle.getImageUrl() != null && launchVehicle.getImageUrl().length() > 0) {
                                Timber.v("Sending image %s", launchVehicle.getImageUrl());
                                sendDataToWear(launchVehicle.getImageUrl(), launch, modify);
                                Timber.d("Glide Loading: %s %s", launchVehicle.getName(), launchVehicle.getImageUrl());

                            } else {
                                sendDataToWear(context.getString(R.string.default_wear_image), launch, modify);
                            }
                        }
                    } else {
                        sendDataToWear(context.getString(R.string.default_wear_image), launch, modify);
                    }
                } else {
                    sendDataToWear(context.getString(R.string.default_wear_image), launch, modify);
                }
            }
        } catch (IndexOutOfBoundsException error) {
            Timber.e(error);
        }
        mRealm.close();
    }

    private void sendDataToWear(String image, final Launch launch, boolean modify) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Timber.v("Connected to Google API Client");

        int blur = sharedPreferences.getInt("BLUR_WEAR", DEFAULT_BLUR) + 1;
        int radius = sharedPreferences.getInt("RADIUS_WEAR", DEFAULT_RADIUS) + 1;
        int dim = sharedPreferences.getInt("DIM_WEAR", DEFAULT_DIM) + 1;
        int grey = sharedPreferences.getInt("GREY_WEAR", DEFAULT_GREY) + 1;

        final PutDataMapRequest putImageReq = PutDataMapRequest.create("/nextLaunch");
        PutDataMapRequest putConfigReq = PutDataMapRequest.create("/config");
        putConfigReq.getDataMap().putBoolean(HOUR_KEY, sharedPreferences.getBoolean("wear_hour_mode", false));
        putConfigReq.getDataMap().putBoolean(UTC_KEY, sharedPreferences.getBoolean("wear_display_utc", true));
        putConfigReq.getDataMap().putLong("time", new Date().getTime());

        PutDataRequest putConfigDataReq = putConfigReq.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(putConfigDataReq);



            /*
             * brightness value ranges from -1.0 to 1.0, with 0.0 as the normal level
             */
        float dimFloat = (float) (dim - 50) / 100;
        float satFloat = (float) grey / 100;
        Timber.v("Blur %s - Radius %s - Dim %sf - Saturation %sf", blur, radius, dimFloat, satFloat);

        if (modify) {
            try {
                if (radius == 25) radius = 24;
                MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                        new SaturationTransformation(context, satFloat),
                        new BrightnessFilterTransformation(dimFloat),
                        new BlurTransformation(radius, blur)
//                        new BlurTransformation(radius)
                );
                Bitmap resource = GlideApp.with(context)
                        .asBitmap()
                        .load(image)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .apply(RequestOptions.bitmapTransform(multi))
                        .submit(300, 300)
                        .get();


                Asset asset = createAssetFromBitmap(resource);
                int i = (int) (new Date().getTime()/1000);
                putImageReq.getDataMap().putString(NAME_KEY, launch.getName());
                putImageReq.getDataMap().putLong(TIME_KEY, (int) launch.getNet().getTime()/1000);
                putImageReq.getDataMap().putLong(DATE_KEY, launch.getNet().getTime());
                putImageReq.getDataMap().putLong("time", new Date().getTime());
                putImageReq.getDataMap().putAsset(BACKGROUND_KEY, asset);
                putImageReq.getDataMap().putBoolean(SUPPORTER_KEY, SupporterHelper.isSupporter());
                PutDataRequest putDataReq = putImageReq.asPutDataRequest();
                putImageReq.getDataMap().putLong("time", new Date().getTime());
                Wearable.getDataClient(context).putDataItem(putDataReq);
                Timber.v("Data sent to wearable.");
            } catch (InterruptedException | ExecutionException e) {
                Timber.e(e);
            }
        } else {
            try {
                Bitmap resource = Glide.with(context)
                        .asBitmap()
                        .load(image)
                        .submit(300, 300)
                        .get();


                Asset asset = createAssetFromBitmap(resource);
                putImageReq.getDataMap().putString(NAME_KEY, launch.getName());
                putImageReq.getDataMap().putLong(TIME_KEY, (int) launch.getNet().getTime()/1000);
                putImageReq.getDataMap().putLong(DATE_KEY, launch.getNet().getTime());
                putImageReq.getDataMap().putLong("time", new Date().getTime());
                putImageReq.getDataMap().putAsset(BACKGROUND_KEY, asset);
                putImageReq.getDataMap().putBoolean(SUPPORTER_KEY, SupporterHelper.isSupporter());
                PutDataRequest putDataReq = putImageReq.asPutDataRequest();
                putImageReq.getDataMap().putLong("time", new Date().getTime());
                Wearable.getDataClient(context).putDataItem(putDataReq);
                Timber.v("Data sent to wearable.");

            } catch (InterruptedException | ExecutionException e) {
                Timber.e(e);
            }
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}