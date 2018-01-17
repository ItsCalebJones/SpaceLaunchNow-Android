package me.calebjones.spacelaunchnow.content.services;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;

import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import timber.log.Timber;


public class ListenerServiceFromWear extends WearableListenerService {

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    @Override
    public void onMessageReceived (MessageEvent messageEvent){
        Timber.v("Received!");
        byte[] arr = messageEvent.getData();
        ByteBuffer wrapped = ByteBuffer.wrap(arr);
        int num = wrapped.getInt();

        Intent exploreIntent = new Intent(getApplicationContext(), LaunchDetailActivity.class);
        exploreIntent.putExtra("TYPE", "launch");
        exploreIntent.putExtra("launchID", num);
        startActivity(exploreIntent);
    }
}
