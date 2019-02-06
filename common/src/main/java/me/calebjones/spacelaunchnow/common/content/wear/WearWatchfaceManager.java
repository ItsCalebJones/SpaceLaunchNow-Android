package me.calebjones.spacelaunchnow.common.content.wear;

import android.content.Context;

import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import me.calebjones.spacelaunchnow.common.base.BaseManager;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.data.models.Constants.SUPPORTER_KEY;

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
            if (isWearAppConnected(connectedNodes)) sendDataToWear();
        } catch (ExecutionException | InterruptedException e) {
            if (!e.getLocalizedMessage().contains("Wearable.API is not available on this device")) {
                Timber.e(e);
            }
        }
    }


    private boolean isWearAppConnected(Set<Node> nodes) {
        return nodes.size() > 0;
    }


    private void sendDataToWear() {
        final PutDataMapRequest putImageReq = PutDataMapRequest.create("/supporter");
        putImageReq.getDataMap().putBoolean(SUPPORTER_KEY, SupporterHelper.isSupporter());
        PutDataRequest putDataReq = putImageReq.asPutDataRequest();
        putImageReq.getDataMap().putLong("time", new Date().getTime());
        Wearable.getDataClient(context).putDataItem(putDataReq);
    }
}

