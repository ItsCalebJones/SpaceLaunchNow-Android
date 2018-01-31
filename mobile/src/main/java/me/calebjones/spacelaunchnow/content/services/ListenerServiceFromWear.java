package me.calebjones.spacelaunchnow.content.services;

import android.content.Intent;

import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.ChannelClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;
import java.util.Set;

import me.calebjones.spacelaunchnow.content.jobs.SyncWearJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateWearJob;
import me.calebjones.spacelaunchnow.content.wear.WearWatchfaceManager;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterActivity;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import timber.log.Timber;


public class ListenerServiceFromWear extends WearableListenerService {

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String START_SUPPORTER_ACTIVITY_PATH = "/start-activity-supporter";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    @Override
    public void onMessageReceived (MessageEvent messageEvent){
        Timber.v("Received!");
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            byte[] arr = messageEvent.getData();
            ByteBuffer wrapped = ByteBuffer.wrap(arr);
            int num = wrapped.getInt();

            Intent exploreIntent = new Intent(getApplicationContext(), LaunchDetailActivity.class);
            exploreIntent.putExtra("TYPE", "launch");
            exploreIntent.putExtra("launchID", num);
            startActivity(exploreIntent);
        } else if (messageEvent.getPath().equals(START_SUPPORTER_ACTIVITY_PATH)){
            if(!SupporterHelper.isSupporter()) {
                Intent supporterIntent = new Intent(getApplicationContext(), SupporterActivity.class);
                startActivity(supporterIntent);
            } else {
                UpdateWearJob.scheduleJobNow();
            }
        }
    }

    @Override
    public void onCapabilityChanged (CapabilityInfo capabilityInfo){
        Set<Node> nodes = capabilityInfo.getNodes();
        if (nodes.size() > 0) {
            WearWatchfaceManager wearWatchfaceManager = new WearWatchfaceManager(getApplicationContext());
            wearWatchfaceManager.updateWear();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }

    @Override
    public void onChannelOpened (ChannelClient.Channel channel){
        Timber.v("Hello");
    }

    @Override
    public void onChannelClosed (ChannelClient.Channel channel, int closeReason, int appSpecificErrorCode){
        Timber.v("Hello");
    }
}
