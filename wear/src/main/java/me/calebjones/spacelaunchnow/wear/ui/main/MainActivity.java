package me.calebjones.spacelaunchnow.wear.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.support.wearable.view.ConfirmationOverlay;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.wearable.intent.RemoteIntent;
import com.google.android.wearable.playstore.PlayStoreAvailability;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.wear.R;
import me.calebjones.spacelaunchnow.wear.common.BaseActivity;
import me.calebjones.spacelaunchnow.wear.ui.launch.LaunchActivity;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, CapabilityClient.OnCapabilityChangedListener {

    private Node mAndroidPhoneNodeWithApp;

    private GoogleApiClient mGoogleApiClient;

    // Name of capability listed in Phone app's wear.xml.
    // IMPORTANT NOTE: This should be named differently than your Wear app's capability.
    private static final String CAPABILITY_PHONE_APP = "verify_remote_launch_spacelaunchnow_phone_app";

    private static final String WELCOME_MESSAGE = "Welcome to Space Launch Now!\n\n";

    private static final String CHECKING_MESSAGE =
            WELCOME_MESSAGE + "Checking for Mobile app...\n";

    private static final String MISSING_MESSAGE =
            WELCOME_MESSAGE
                    + "You are missing the required phone app, please click on the button below to "
                    + "install it on your phone.\n";

    private static final String INSTALLED_MESSAGE =
            WELCOME_MESSAGE
                    + "Mobile app installed on your %s!\n\nYou can now use MessageApi, "
                    + "DataApi, etc.";

    // Links to mobile app for Android (Play Store).
    // TODO: Replace with your links/packages.
    private static final String PLAY_STORE_APP_URI = "market://details?id=me.calebjones.spacelaunchnow";

    @BindView(R.id.information_text_view)
    TextView mInformationTextView;
    @BindView(R.id.remote_open_button)
    Button mRemoteOpenButton;

    @OnClick(R.id.remote_open_button)
    void remoteButton(){
        Timber.v("Remote OnClick");
        openAppInStoreOnPhone();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intro);
        ButterKnife.bind(this);

        setAmbientEnabled();

        mInformationTextView = findViewById(R.id.information_text_view);
        mRemoteOpenButton =  findViewById(R.id.remote_open_button);

        mInformationTextView.setText(CHECKING_MESSAGE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onPause() {
        Timber.d("onPause()");
        super.onPause();

        if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {
            Wearable.CapabilityApi.removeCapabilityListener(
                    mGoogleApiClient,
                    this,
                    CAPABILITY_PHONE_APP
            );

            mGoogleApiClient.disconnect();
        }
    }


    @Override
    protected void onResume() {
        Timber.d("onResume()");
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        Timber.d("onCapabilityChanged(): %s", capabilityInfo);

        mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
        verifyNodeAndUpdateUI();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Set up listeners for capability changes (install/uninstall of remote app).
        Wearable.CapabilityApi.addCapabilityListener(
                mGoogleApiClient,
                this,
                CAPABILITY_PHONE_APP
        );
        checkIfPhoneHasApp();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.d("onConnectionSuspended(): connection to location client suspended: %s", i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("onConnectionFailed(): %s", connectionResult);
    }

    // Result from sending RemoteIntent to phone to open app in play/app store.
    private final ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == RemoteIntent.RESULT_OK) {
                new ConfirmationOverlay().showOn(MainActivity.this);

            } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                new ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .showOn(MainActivity.this);

            } else {
                throw new IllegalStateException("Unexpected result " + resultCode);
            }
        }
    };

    private void checkIfPhoneHasApp() {
        Timber.v("Check if phone has app.");

        PendingResult<CapabilityApi.GetCapabilityResult> pendingResult =
                Wearable.CapabilityApi.getCapability(
                        mGoogleApiClient,
                        CAPABILITY_PHONE_APP,
                        CapabilityApi.FILTER_ALL
                );

        pendingResult.setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {

            @Override
            public void onResult(@NonNull CapabilityApi.GetCapabilityResult getCapabilityResult) {
                Timber.d("onResult(): %s", getCapabilityResult);

                if (getCapabilityResult.getStatus().isSuccess()) {
                    CapabilityInfo capabilityInfo = getCapabilityResult.getCapability();
                    mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
                    verifyNodeAndUpdateUI();

                } else {
                    Timber.d("Failed CapabilityApi: %s", getCapabilityResult.getStatus());
                }
            }
        });
    }

    private void verifyNodeAndUpdateUI() {

        if (mAndroidPhoneNodeWithApp != null) {

            // TODO: Add your code to communicate with the phone app via
            // Wear APIs (MessageApi, DataApi, etc.)

            String installMessage =
                    String.format(INSTALLED_MESSAGE, mAndroidPhoneNodeWithApp.getDisplayName());
            Timber.d(installMessage);
            mInformationTextView.setText(installMessage);
            mRemoteOpenButton.setVisibility(View.INVISIBLE);

            startActivity(new Intent(this, LaunchActivity.class));

        } else {
            Timber.d(MISSING_MESSAGE);
            mInformationTextView.setText(MISSING_MESSAGE);
            mRemoteOpenButton.setVisibility(View.VISIBLE);
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
                Intent intentAndroid =
                        new Intent(Intent.ACTION_VIEW)
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                .setData(Uri.parse(PLAY_STORE_APP_URI));

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

    /*
     * There should only ever be one phone in a node set (much less w/ the correct capability), so
     * I am just grabbing the first one (which should be the only one).
     */
    private Node pickBestNodeId(Set<Node> nodes) {
        Timber.d("pickBestNodeId(): %s", nodes);

        Node bestNodeId = null;
        // Find a nearby node/phone or pick one arbitrarily. Realistically, there is only one phone.
        for (Node node : nodes) {
            bestNodeId = node;
        }
        return bestNodeId;
    }
}
