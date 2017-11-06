package me.calebjones.spacelaunchnow.content.wear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.services.BaseManager;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.data.models.RocketDetails;
import me.calebjones.spacelaunchnow.utils.transformations.SaturationTransformation;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.data.models.Constants.*;

public class WearWatchfaceManager extends BaseManager {

    private Context context;

    public WearWatchfaceManager(Context context) {
        super(context);
        this.context = context;
    }

    // Create a data map and put data in it
    public void updateWear() {
        Realm realm = Realm.getDefaultInstance();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            Launch launch = realm.where(Launch.class).greaterThan("net", new Date()).findAllSorted("net").first();
        if (launch != null && launch.getName() != null && launch.getNetstamp() != null) {
            Timber.v("Sending data to wear: %s", launch.getName());

            boolean dynamic = sharedPref.getBoolean("supporter_dynamic_background", false);
            boolean modify = sharedPref.getBoolean("wear_background_blur", true);
            if (dynamic) {
                if (launch.getRocket().getName() != null) {
                    if (launch.getRocket().getImageURL() != null && launch.getRocket().getImageURL().length() > 0 && !launch.getRocket().getImageURL().contains("placeholder")) {
                        Timber.v("Sending image %s", launch.getRocket().getImageURL());
                        sendImageToWear(launch.getRocket().getImageURL(), launch, modify);
                    } else {
                        String query;
                        if (launch.getRocket().getName().contains("Space Shuttle")) {
                            query = "Space Shuttle";
                        } else {
                            query = launch.getRocket().getName();
                        }

                        RocketDetails launchVehicle = realm.where(RocketDetails.class)
                                .contains("name", query)
                                .findFirst();
                        if (launchVehicle != null && launchVehicle.getImageURL() != null && launchVehicle.getImageURL().length() > 0) {
                            Timber.v("Sending image %s", launchVehicle.getImageURL());
                            sendImageToWear(launchVehicle.getImageURL(), launch, modify);
                            Timber.d("Glide Loading: %s %s", launchVehicle.getLV_Name(), launchVehicle.getImageURL());

                        } else {
                            sendImageToWear(context.getString(R.string.default_wear_image), launch, modify);
                        }
                    }
                } else {
                    sendImageToWear(context.getString(R.string.default_wear_image), launch, modify);
                }
            } else {
                sendImageToWear(context.getString(R.string.default_wear_image), launch, modify);
            }
        }
        } catch (IndexOutOfBoundsException error){
            Crashlytics.logException(error);
        }
        realm.close();
    }

    private void sendImageToWear(String image, final Launch launch, boolean modify) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(10, TimeUnit.SECONDS);
        if (connectionResult.isSuccess()) {
            Timber.v("Connected to Google API Client");


            int blur = sharedPreferences.getInt("BLUR_WEAR", DEFAULT_BLUR) + 1;
            int radius = sharedPreferences.getInt("RADIUS_WEAR", DEFAULT_RADIUS) + 1;
            int dim = sharedPreferences.getInt("DIM_WEAR", DEFAULT_DIM) + 1;
            int grey = sharedPreferences.getInt("GREY_WEAR", DEFAULT_GREY) + 1;
            final boolean dynamicText = sharedPreferences.getBoolean("wear_text_dynamic", false);

            final PutDataMapRequest putImageReq = PutDataMapRequest.create("/nextLaunch");

            /*
             * brightness value ranges from -1.0 to 1.0, with 0.0 as the normal level
             */
            float dimFloat = (float) (dim - 50) / 100;
            float satFloat = (float) grey / 100;
            Timber.v("Blur %s - Radius %s - Dim %sf - Saturation %sf", blur, radius, dimFloat, satFloat);

            if (modify) {
                try {
                    Bitmap resource = Glide.with(context)
                            .load(image)
                            .asBitmap()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .transform(new SaturationTransformation(context, satFloat), new BlurTransformation(context, radius, blur), new BrightnessFilterTransformation(context, dimFloat))
                            .into(300, 300)
                            .get();

                    Asset asset = createAssetFromBitmap(resource);
                    putImageReq.getDataMap().putString(NAME_KEY, launch.getName());
                    putImageReq.getDataMap().putInt(TIME_KEY, launch.getNetstamp());
                    putImageReq.getDataMap().putLong(DATE_KEY, launch.getNet().getTime());
                    putImageReq.getDataMap().putLong("time", new Date().getTime());
                    putImageReq.getDataMap().putAsset(BACKGROUND_KEY, asset);
                    putImageReq.getDataMap().putBoolean(DYNAMIC_KEY, dynamicText);
                    PutDataRequest putDataReq = putImageReq.asPutDataRequest();
                    putImageReq.getDataMap().putLong("time", new Date().getTime());
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
                    Timber.v("Data sent to wearable.");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            } else {
                try {
                    Bitmap resource = Glide.with(context)
                            .load(image)
                            .asBitmap()
                            .transform(new BrightnessFilterTransformation(context, -.1f))
                            .into(300, 300)
                            .get();
                    Asset asset = createAssetFromBitmap(resource);
                    putImageReq.getDataMap().putString(NAME_KEY, launch.getName());
                    putImageReq.getDataMap().putInt(TIME_KEY, launch.getNetstamp());
                    putImageReq.getDataMap().putLong(DATE_KEY, launch.getNet().getTime());
                    putImageReq.getDataMap().putLong("time", new Date().getTime());
                    putImageReq.getDataMap().putAsset(BACKGROUND_KEY, asset);
                    putImageReq.getDataMap().putBoolean(DYNAMIC_KEY, dynamicText);
                    PutDataRequest putDataReq = putImageReq.asPutDataRequest();
                    putImageReq.getDataMap().putLong("time", new Date().getTime());
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
                    Timber.v("Data sent to wearable.");

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }
        } else {
            Timber.v("Failed to connect to Google API Client");
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
