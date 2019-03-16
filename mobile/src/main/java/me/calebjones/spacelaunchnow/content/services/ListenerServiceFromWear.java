package me.calebjones.spacelaunchnow.content.services;

import android.content.Intent;

import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.ChannelClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Set;

import me.calebjones.spacelaunchnow.common.content.jobs.UpdateWearJob;
import me.calebjones.spacelaunchnow.common.content.wear.WearWatchfaceManager;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterActivity;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import timber.log.Timber;


public class ListenerServiceFromWear extends WearableListenerService {

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String START_SUPPORTER_ACTIVITY_PATH = "/start-activity-supporter";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    @Override
    public void onMessageReceived (MessageEvent messageEvent){
        Timber.v("Received!");
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            final String message = new String(messageEvent.getData());
            Intent exploreIntent = new Intent(getApplicationContext(), LaunchDetailActivity.class);
            exploreIntent.putExtra("TYPE", "launch");
            exploreIntent.putExtra("launchID", message);
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
