package me.calebjones.spacelaunchnow.content.services;

import android.content.Context;
import android.content.Intent;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

import me.calebjones.spacelaunchnow.content.DataSaver;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LaunchNotificationReceiver extends NotificationExtenderService {

    private boolean running = false;
    private DataSaver dataSaver = new DataSaver(this);
    private Context context = this;

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read properties from result.
        Timber.i("Received Result - App in Focus: %s Payload: %s", receivedResult.isAppInFocus, receivedResult.payload);

        if (receivedResult.payload != null) {
            OSNotificationPayload payload = receivedResult.payload;
            JSONObject data = payload.additionalData;
            try {
                int launchID = Integer.valueOf(data.getString("launchID"));
                String background = data.getString("background");

                if (background.contains("true")) {
                    DataClient.getInstance().getLaunchById(launchID, false, new Callback<LaunchResponse>() {
                        @Override
                        public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                            if (response.isSuccessful()) {
                                dataSaver.saveLaunchesToRealm(response.body().getLaunches());
                                dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, true, call));
                                startService(new Intent(context, NextLaunchTracker.class));
                                setRunning(false);
                            } else {
                                startService(new Intent(context, NextLaunchTracker.class));
                                dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, false, call, ErrorUtil.parseLibraryError(response)));
                                setRunning(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<LaunchResponse> call, Throwable t) {
                            startService(new Intent(context, NextLaunchTracker.class));
                            dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, false, call, t.getLocalizedMessage()));
                            setRunning(false);
                        }
                    });

                    //Wait for async task to finish.
                    while (running) {
                        try {
                            Timber.v("Waiting for response...");
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                } else {
                    // Not a background payload, likely no additional data, show the notification
                    return false;
                }
            } catch (JSONException e) {
                // Error parsing additional data,  trigger a sync.
                Timber.e(e);
                startService(new Intent(context, NextLaunchTracker.class));
                return false;
            }

        // Payload is null, likely no additional data, show the notification
        } else {
            return false;
        }
    }

    private void setRunning(boolean bool){
        running = bool;
    }
}
