package me.calebjones.spacelaunchnow.wear.complications;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;
import android.support.wearable.complications.ComplicationProviderService;
import android.support.wearable.complications.ComplicationText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import me.calebjones.spacelaunchnow.wear.content.ComplicationContentManager;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.data.models.Constants.BACKGROUND_KEY;

public class BackgroundImageComplicationProvider extends ComplicationProviderService implements ComplicationContentManager.ContentCallback  {

    private ComplicationData complicationData;
    private ComplicationManager complicationManager;
    private ComplicationContentManager contentManager;
    private int complicationId;
    private int dataType;
    private DataClient dataClient;

    @Override
    public void onComplicationUpdate(final int complicationId, int dataType, final ComplicationManager complicationManager) {
        this.dataType = dataType;
        this.complicationId = complicationId;
        this.complicationManager = complicationManager;
        contentManager = new ComplicationContentManager(getApplicationContext(), this);

        dataClient = Wearable.getDataClient(getApplicationContext());
        dataClient.getDataItems().addOnCompleteListener(new OnCompleteListener<DataItemBuffer>() {
            @Override
            public void onComplete(@NonNull Task<DataItemBuffer> task) {
                if (task.isSuccessful()) {
                    DataItemBuffer dataItems = task.getResult();
                    for (DataItem item : dataItems) {
                        if (item.getUri().getPath().equals("/nextLaunch")) {
                            DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                            if (dataMap.containsKey(BACKGROUND_KEY)) {
                                Timber.v("Retrieving Asset...");
                                Asset profileAsset = dataMap.getAsset(BACKGROUND_KEY);
                                Task<DataClient.GetFdForAssetResponse> assetInputStream = Wearable.getDataClient(getApplicationContext()).getFdForAsset(profileAsset);
                                assetInputStream.addOnCompleteListener(new OnCompleteListener<DataClient.GetFdForAssetResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataClient.GetFdForAssetResponse> task) {
                                        if (task.isSuccessful()){
                                            InputStream assetInputStream = task.getResult().getInputStream();
                                            if (assetInputStream != null) {
                                                Bitmap bitmap = BitmapFactory.decodeStream(assetInputStream);
                                                Icon icon = Icon.createWithBitmap(bitmap);
                                                complicationData = new ComplicationData.Builder(ComplicationData.TYPE_LARGE_IMAGE)
                                                        .setLargeImage(icon)
                                                        .build();
                                            }
                                        }
                                        if (complicationData != null) {
                                            complicationManager.updateComplicationData(complicationId, complicationData);

                                        } else {
                                            // If no data is sent, we still need to inform the ComplicationManager, so
                                            // the update job can finish and the wake lock isn't held any longer.
                                            complicationManager.noUpdateRequired(complicationId);
                                        }
                                    }
                                });
                            }
                        }
                    }

                }
            }
        });
    }

    @Override
    public void dataLoaded() {

    }

    @Override
    public void errorLoading(String error) {

    }
}
