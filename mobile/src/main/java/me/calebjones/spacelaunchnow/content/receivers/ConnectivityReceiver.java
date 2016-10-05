package me.calebjones.spacelaunchnow.content.receivers;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.content.models.Strings.ACTION_CHECK_NEXT_LAUNCH_TIMER;

public class ConnectivityReceiver extends GcmTaskService {
    @Override
    public int onRunTask(TaskParams taskParams) {
        Timber.v("onRunTask");
        switch (taskParams.getTag()) {
            case ACTION_CHECK_NEXT_LAUNCH_TIMER:
                Timber.i(ACTION_CHECK_NEXT_LAUNCH_TIMER);
                // This is where useful work would go
                LaunchDataService.startActionUpdateNextLaunch(this);
                return GcmNetworkManager.RESULT_SUCCESS;
            default:
                return GcmNetworkManager.RESULT_FAILURE;
        }
    }
}
