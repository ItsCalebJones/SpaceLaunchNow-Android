package me.calebjones.spacelaunchnow.wear.complications;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.Set;

import me.calebjones.spacelaunchnow.wear.ui.launchdetail.LaunchDetail;
import timber.log.Timber;

public class ComplicationTapBroadcastReceiver extends BroadcastReceiver {
    private static final String EXTRA_PROVIDER_COMPONENT =
            "me.calebjones.spacelaunchnow.wearable.watchface.provider.action.PROVIDER_COMPONENT";
    private static final String EXTRA_COMPLICATION_ID =
            "me.calebjones.spacelaunchnow.wearable.watchface.provider.action.COMPLICATION_ID";
    private String nodeId;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Bundle extras = intent.getExtras();
//        ComponentName provider = extras.getParcelable(EXTRA_PROVIDER_COMPONENT);
//        int complicationId = extras.getInt(EXTRA_COMPLICATION_ID);
//
//        // Request an update for the complication that has just been tapped.
//        ProviderUpdateRequester requester = new ProviderUpdateRequester(context, provider);
//        requester.requestUpdate(complicationId);
        this.context = context;
        checkCompanionInstalled();
    }

    /**
     * Returns a pending intent, suitable for use as a tap intent, that causes a complication to be
     * toggled and updated.
     */
    static PendingIntent getToggleIntent(
            Context context, ComponentName provider, int complicationId) {
        Intent intent = new Intent(context, ComplicationTapBroadcastReceiver.class);
        intent.putExtra(EXTRA_PROVIDER_COMPONENT, provider);
        intent.putExtra(EXTRA_COMPLICATION_ID, complicationId);

        // Pass complicationId as the requestCode to ensure that different complications get
        // different intents.
        return PendingIntent.getBroadcast(
                context, complicationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void checkCompanionInstalled() {
        Task<CapabilityInfo> capabilityInfo = Wearable.getCapabilityClient(context).getCapability("start_activity", CapabilityClient.FILTER_REACHABLE);
        capabilityInfo.addOnCompleteListener(new OnCompleteListener<CapabilityInfo>() {
            @Override
            public void onComplete(@NonNull Task<CapabilityInfo> task) {
                if (task.isSuccessful()) {
                    nodeId = pickBestNodeId(task.getResult().getNodes());
                    sendMessage();
                } else {
                    if (task.getException() != null) Timber.e(task.getException());
                }
            }
        });
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private void sendMessage() {
        if (nodeId != null) {

            Task<Integer> sendTask = Wearable.getMessageClient(context).sendMessage(
                    nodeId, "/start-activity-supporter", null);

            sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    Timber.v("Successfully sent!");
                }
            });
            sendTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                @Override
                public void onComplete(@NonNull Task<Integer> task) {
                    Timber.v("Successfully Completed!");
                }
            });
        }
    }
}
