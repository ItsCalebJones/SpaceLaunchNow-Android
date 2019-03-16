package me.calebjones.spacelaunchnow.wear.services;

import android.content.ComponentName;
import android.support.wearable.complications.ProviderUpdateRequester;

import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import me.calebjones.spacelaunchnow.wear.complications.NextLaunchComplicationProvider;
import me.calebjones.spacelaunchnow.wear.utils.SupporterHelper;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.data.models.Constants.SUPPORTER_KEY;

public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onDataChanged (DataEventBuffer dataEvents){
        Timber.v("onDataChanged - ");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/supporter")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    if(dataMap.getBoolean(SUPPORTER_KEY)){
                        SupporterHelper.setSupporter(true);

                        new ProviderUpdateRequester(getApplicationContext(), new ComponentName(getApplicationContext(), NextLaunchComplicationProvider.class))
                                .requestUpdateAll();
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReceived (MessageEvent messageEvent){
        Timber.v("onMessageReceived - ");
    }

    @Override
    public void onCapabilityChanged (CapabilityInfo capabilityInfo){
        Timber.v("onCapabilityChanged - ");
    }
}
