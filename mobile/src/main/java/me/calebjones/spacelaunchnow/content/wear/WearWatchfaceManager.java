package me.calebjones.spacelaunchnow.content.wear;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.services.BaseManager;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.spacelaunchnow.RocketDetail;
import me.calebjones.spacelaunchnow.utils.GlideApp;
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
        mRealm = Realm.getDefaultInstance();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            RealmResults<Launch> launches = QueryBuilder.buildSwitchQuery(context, mRealm);
            Launch launch = launches.first();
//            Launch launch = mRealm.where(Launch.class).greaterThan("net", new Date()).findAllSorted("net").first();
            if (launch != null && launch.getName() != null && launch.getNetstamp() != null) {
                Timber.v("Sending data to wear: %s", launch.getName());

                boolean dynamic = sharedPref.getBoolean("supporter_dynamic_background", false);
                boolean modify = sharedPref.getBoolean("wear_background_blur", true);
                if (dynamic) {
                    if (launch.getRocket().getName() != null) {
                        if (launch.getRocket().getImageURL() != null && launch.getRocket().getImageURL().length() > 0 && !launch.getRocket().getImageURL().contains("placeholder")) {
                            Timber.v("Sending image %s", launch.getRocket().getImageURL());
                            sendDataToWear(launch.getRocket().getImageURL(), launch, modify);
                        } else {
                            String query;
                            if (launch.getRocket().getName().contains("Space Shuttle")) {
                                query = "Space Shuttle";
                            } else {
                                query = launch.getRocket().getName();
                            }

                            RocketDetail launchVehicle = mRealm.where(RocketDetail.class)
                                    .contains("name", query)
                                    .findFirst();
                            if (launchVehicle != null && launchVehicle.getImageURL() != null && launchVehicle.getImageURL().length() > 0) {
                                Timber.v("Sending image %s", launchVehicle.getImageURL());
                                sendDataToWear(launchVehicle.getImageURL(), launch, modify);
                                Timber.d("Glide Loading: %s %s", launchVehicle.getName(), launchVehicle.getImageURL());

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
            Crashlytics.logException(error);
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
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                Timber.e(e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .load(image)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .apply(RequestOptions.bitmapTransform(multi))
                        .submit(300, 300)
                        .get();


                Asset asset = createAssetFromBitmap(resource);
                putImageReq.getDataMap().putString(NAME_KEY, launch.getName());
                putImageReq.getDataMap().putInt(TIME_KEY, launch.getNetstamp());
                putImageReq.getDataMap().putLong(DATE_KEY, launch.getNet().getTime());
                putImageReq.getDataMap().putLong("time", new Date().getTime());
                putImageReq.getDataMap().putAsset(BACKGROUND_KEY, asset);
                PutDataRequest putDataReq = putImageReq.asPutDataRequest();
                putImageReq.getDataMap().putLong("time", new Date().getTime());
                Wearable.getDataClient(context).putDataItem(putDataReq);
                Timber.v("Data sent to wearable.");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
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
                putImageReq.getDataMap().putInt(TIME_KEY, launch.getNetstamp());
                putImageReq.getDataMap().putLong(DATE_KEY, launch.getNet().getTime());
                putImageReq.getDataMap().putLong("time", new Date().getTime());
                putImageReq.getDataMap().putAsset(BACKGROUND_KEY, asset);
                PutDataRequest putDataReq = putImageReq.asPutDataRequest();
                putImageReq.getDataMap().putLong("time", new Date().getTime());
                Wearable.getDataClient(context).putDataItem(putDataReq);
                Timber.v("Data sent to wearable.");

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
