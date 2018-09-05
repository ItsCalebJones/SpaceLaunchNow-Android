package me.calebjones.spacelaunchnow.wear.ui.launchdetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.ConfirmationOverlay;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.wearable.intent.RemoteIntent;
import com.google.android.wearable.playstore.PlayStoreAvailability;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.RetrofitBuilder;
import me.calebjones.spacelaunchnow.data.networking.interfaces.SpaceLaunchNowService;
import me.calebjones.spacelaunchnow.wear.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

public class LaunchDetail extends WearableActivity implements SwipeRefreshLayout.OnRefreshListener, CapabilityClient.OnCapabilityChangedListener {

    @BindView(R.id.content_title)
    TextView launchTitle;
    @BindView(R.id.countdown_days)
    TextView countdownDays;
    @BindView(R.id.countdown_hours)
    TextView countdownHours;
    @BindView(R.id.countdown_minutes)
    TextView countdownMinutes;
    @BindView(R.id.countdown_seconds)
    TextView countdownSeconds;
    @BindView(R.id.countdown_layout)
    LinearLayout countdownLayout;
    @BindView(R.id.content_mission)
    TextView launchMission;
    @BindView(R.id.content_mission_description)
    TextView launchMissionDescription;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.TitleCard)
    LinearLayout titleCard;
    @BindView(R.id.launchStatus)
    TextView launchStatus;
    @BindView(R.id.countdown_view)
    LinearLayout countdownView;
    @BindView(R.id.explore_button)
    AppCompatButton exploreButton;
    private Launch launch;
    private Realm realm;
    private CountDownTimer timer;
    private String nodeId;
    private Retrofit retrofit;
    private Context context;

    public static final String START_ACTIVITY = "/start-activity";
    private static final String START_ACTIVITY_CAPABILITY = "start_activity";
    private static final String PLAY_STORE_APP_URI = "market://details?id=me.calebjones.spacelaunchnow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_detail);
        ButterKnife.bind(this);
        context = this;
        realm = Realm.getDefaultInstance();
        retrofit = RetrofitBuilder.getSpaceLaunchNowRetrofit(this.getString(R.string.sln_token));
        swipeRefresh.setOnRefreshListener(this);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        int launchID = 0;
        if (intent.getExtras() != null) {
            launchID = intent.getIntExtra("launchId", 0);
        }

        loadData(launchID);

        // Enables Always-on
        setAmbientEnabled();

        checkCompanionInstalled();
    }

    private void loadData(int launchID) {
        RealmResults<Launch> launches = realm.where(Launch.class).equalTo("id", launchID).findAll();
        if (launches.size() > 0){
            launch = launches.first();
            setupView(launch);
        } else {
            Intent cancelIntent = new Intent();
            setResult(RESULT_CANCELED, cancelIntent);
            finish();
        }
    }

    private void checkCompanionInstalled() {
        Task<CapabilityInfo> capabilityInfo = Wearable.getCapabilityClient(this).getCapability(START_ACTIVITY_CAPABILITY, CapabilityClient.FILTER_REACHABLE);
        capabilityInfo.addOnCompleteListener(new OnCompleteListener<CapabilityInfo>() {
            @Override
            public void onComplete(@NonNull Task<CapabilityInfo> task) {
                if (task.isSuccessful()) {
                    nodeId = pickBestNodeId(task.getResult().getNodes());
                    if (nodeId != null) {
                        exploreButton.setText("EXPLORE");
                    } else {
                        exploreButton.setText("GET PHONE APP");
                    }
                } else {
                    exploreButton.setVisibility(View.GONE);
                    if (task.getException() != null) Timber.e(task.getException());
                }
            }
        });
    }

    @Override
    protected  void onResume() {
        super.onResume();
        Wearable.getCapabilityClient(this).addListener(this, START_ACTIVITY_CAPABILITY);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Wearable.getCapabilityClient(this).removeListener(this);
    }

    @Override
    public void onRefresh() {
        final SpaceLaunchNowService request = retrofit.create(SpaceLaunchNowService.class);
        Call<Launch> call;
        call = request.getLaunchById(launch.getId(), "detailed");
        Timber.v("Calling - %s", call.request().url().url().toString());
        call.enqueue(new Callback<Launch>() {

            @Override
            public void onResponse(Call<Launch> call, Response<Launch> response) {
                if (response.isSuccessful()) {
                    Timber.v("Successful! - %s", call.request().url().url().toString());
                    launch = response.body();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.copyToRealmOrUpdate(launch);
                        }
                    });
                    loadData(launch.getId());
                } else {
                    try {
                        Toast.makeText(LaunchDetail.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(LaunchDetail.this, "Unknown error occurred.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Timber.e("Error: %s", response.errorBody());
                }
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Launch> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Timber.e(t.getLocalizedMessage());
            }

        });
    }

    private void setupView(Launch launch) {
        launchTitle.setText(launch.getLauncherConfig().getName());

        launchStatus.setText(getStatus(launch.getStatus().getId()));

        if (launch.getMission() != null) {
            launchMission.setText(launch.getMission().getName());
            launchMissionDescription.setText(launch.getMission().getDescription());
        }

        long future = launch.getNetstamp();
        future = future * 1000;
        long timeToFinish = future - System.currentTimeMillis();
        if (timeToFinish > 0) {
            timer = new CountDownTimer(timeToFinish, 1000) {
                StringBuilder time = new StringBuilder();

                @Override
                public void onFinish() {
                    Timber.v("Countdown finished.");

                    countdownDays.setText("- -");
                    countdownHours.setText("- -");
                    countdownMinutes.setText("- -");
                    countdownSeconds.setText("- -");
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    time.setLength(0);

                    // Calculate the Days/Hours/Mins/Seconds numerically.
                    long longDays = millisUntilFinished / 86400000;
                    long longHours = (millisUntilFinished / 3600000) % 24;
                    long longMins = (millisUntilFinished / 60000) % 60;
                    long longSeconds = (millisUntilFinished / 1000) % 60;

                    String days = String.valueOf(longDays);
                    String hours;
                    String minutes;
                    String seconds;

                    // Translate those numerical values to string values.
                    if (longHours < 10) {
                        hours = "0" + String.valueOf(longHours);
                    } else {
                        hours = String.valueOf(longHours);
                    }

                    if (longMins < 10) {
                        minutes = "0" + String.valueOf(longMins);
                    } else {
                        minutes = String.valueOf(longMins);
                    }

                    if (longSeconds < 10) {
                        seconds = "0" + String.valueOf(longSeconds);
                    } else {
                        seconds = String.valueOf(longSeconds);
                    }


                    // Update the views
                    if (Integer.valueOf(days) > 0) {
                        countdownDays.setText(days);
                    } else {
                        countdownDays.setText("- -");
                    }

                    if (Integer.valueOf(hours) > 0) {
                        countdownHours.setText(hours);
                    } else if (Integer.valueOf(days) > 0) {
                        countdownHours.setText("00");
                    } else {
                        countdownHours.setText("- -");
                    }

                    if (Integer.valueOf(minutes) > 0) {
                        countdownMinutes.setText(minutes);
                    } else if (Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                        countdownMinutes.setText("00");
                    } else {
                        countdownMinutes.setText("- -");
                    }

                    if (Integer.valueOf(seconds) > 0) {
                        countdownSeconds.setText(seconds);
                    } else if (Integer.valueOf(minutes) > 0 || Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                        countdownSeconds.setText("00");
                    } else {
                        countdownSeconds.setText("- -");
                    }
                }
            }.start();
        } else {
            countdownView.setVisibility(View.GONE);
        }

    }

    private String getStatus(Integer status) {
        switch (status) {
            case 0:
                break;
            case 1:
                return "Launch is GO";
            case 2:
                return "Launch is NO-GO";
            case 3:
                return "Launch was a SUCCESS";
            case 4:
                return "Launch failed";
        }
        return "Unknown Launch Status";
    }

    @OnClick(R.id.explore_button)
    public void onViewClicked() {
        sendMessage(launch.getId());
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

    private void sendMessage(final int launchID) {
        if (nodeId != null) {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(launchID);

            byte[] result = b.array();
            Task<Integer> sendTask = Wearable.getMessageClient(getApplicationContext()).sendMessage(
                    nodeId, START_ACTIVITY, result);

            sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    Timber.v("Successfully sent!");
                    Intent openOnPhoneIntent = new Intent(context, ConfirmationActivity.class);
                    openOnPhoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    openOnPhoneIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                    startActivity(openOnPhoneIntent);
                }
            });
            sendTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                @Override
                public void onComplete(@NonNull Task<Integer> task) {
                    Timber.v("Successfully Completed!");
                }
            });
            sendTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LaunchDetail.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Timber.e(e);
                }
            });
        } else {
            openAppInStoreOnPhone();
        }
    }

    private void openAppInStoreOnPhone() {
        Timber.d("openAppInStoreOnPhone()");

        int playStoreAvailabilityOnPhone =
                PlayStoreAvailability.getPlayStoreAvailabilityOnPhone(getApplicationContext());

        switch (playStoreAvailabilityOnPhone) {

            // Android phone with the Play Store.
            case PlayStoreAvailability.PLAY_STORE_ON_PHONE_AVAILABLE:
                Timber.d("\tPLAY_STORE_ON_PHONE_AVAILABLE");

                // Create Remote Intent to open Play Store listing of app on remote device.
                Intent intentAndroid = new Intent(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE).setData(Uri.parse(PLAY_STORE_APP_URI));

                RemoteIntent.startRemoteActivity(
                        getApplicationContext(),
                        intentAndroid,
                        mResultReceiver
                );
                break;

            // Assume iPhone (iOS device) or Android without Play Store (not supported right now).
            case PlayStoreAvailability.PLAY_STORE_ON_PHONE_UNAVAILABLE:
                Timber.d("\tPLAY_STORE_ON_PHONE_UNAVAILABLE");
                break;

            case PlayStoreAvailability.PLAY_STORE_ON_PHONE_ERROR_UNKNOWN:
                Timber.d("\tPLAY_STORE_ON_PHONE_ERROR_UNKNOWN");
                break;
        }
    }

    // Result from sending RemoteIntent to phone to open app in play/app store.
    private final ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == RemoteIntent.RESULT_OK) {
                new ConfirmationOverlay().setType(ConfirmationOverlay.OPEN_ON_PHONE_ANIMATION).showOn(LaunchDetail.this);

            } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                new ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .showOn(LaunchDetail.this);

            } else {
                throw new IllegalStateException("Unexpected result " + resultCode);
            }
        }
    };

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        Timber.v("onCapabilityChanged - %s", capabilityInfo.getName());
        nodeId = pickBestNodeId(capabilityInfo.getNodes());
        if (nodeId != null) {
            exploreButton.setText("EXPLORE");
        } else {
            exploreButton.setText("GET PHONE APP");
        }
    }
}
