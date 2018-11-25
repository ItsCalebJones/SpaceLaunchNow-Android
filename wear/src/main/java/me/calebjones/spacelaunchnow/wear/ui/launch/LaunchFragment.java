package me.calebjones.spacelaunchnow.wear.ui.launch;

import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.CircularButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.wear.widget.WearableRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.wear.R;
import me.calebjones.spacelaunchnow.wear.common.DividerItemDecoration;
import me.calebjones.spacelaunchnow.wear.content.ContentManager;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.wear.model.Constants.BUTTON_ADD_WIFI;
import static me.calebjones.spacelaunchnow.wear.model.Constants.BUTTON_REQUEST_HIGHBANDWIDTH;
import static me.calebjones.spacelaunchnow.wear.model.Constants.NETWORK_CONNECTED;
import static me.calebjones.spacelaunchnow.wear.model.Constants.NETWORK_CONNECTED_SLOW;
import static me.calebjones.spacelaunchnow.wear.model.Constants.NETWORK_UNAVAILABLE;
import static me.calebjones.spacelaunchnow.wear.model.Constants.UI_STATE_CONNECTION_TIMEOUT;
import static me.calebjones.spacelaunchnow.wear.model.Constants.UI_STATE_NETWORK_CONNECTED;
import static me.calebjones.spacelaunchnow.wear.model.Constants.UI_STATE_REQUESTING_NETWORK;
import static me.calebjones.spacelaunchnow.wear.model.Constants.UI_STATE_REQUEST_NETWORK;

public class LaunchFragment extends Fragment implements ContentManager.ContentCallback, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.connectivity_icon)
    ImageView mConnectivityIcon;
    @BindView(R.id.connectivity_text)
    TextView mConnectivityText;
    @BindView(R.id.connectivity_indicator)
    RelativeLayout mConnectivityIndicator;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.button_label)
    TextView buttonLabel;
    @BindView(R.id.button_icon)
    CircularButton buttonRequestNetwork;
    @BindView(R.id.button_icon_no)
    CircularButton buttonCancel;
    @BindView(R.id.button)
    LinearLayout buttonContainer;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private LaunchAdapter launchAdapter;
    private ContentManager contentManager;
    private int buttonState = BUTTON_REQUEST_HIGHBANDWIDTH;

    public LaunchFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_launch, container, false);
        ButterKnife.bind(this, rootView);
        swipeRefresh.setOnRefreshListener(this);
        contentManager = new ContentManager(getContext(), this);
        contentManager.init();
        launchAdapter = new LaunchAdapter(getContext(), getArguments() != null ? getArguments().getInt("category") : 0, contentManager);
        recyclerView.setAdapter(launchAdapter);
        WearableRecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setElevation(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    public void updateCategories(int category) {
        launchAdapter.setCategory(category);
    }

    @Override
    public void dataLoaded() {
        swipeRefresh.setRefreshing(false);
        launchAdapter.loadData();
    }

    @Override
    public void errorLoading(String error) {
        Timber.e(error);
        swipeRefresh.setRefreshing(false);
        launchAdapter.loadData();
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        contentManager.init();
    }

    @Override
    public void onStop() {
        contentManager.releaseHighBandwidthNetwork();
        super.onStop();
    }

    @Override
    public void networkState(int uiState) {
        switch (uiState) {
            case UI_STATE_REQUEST_NETWORK:
                showConnectivityStatus();
                checkHighBandwidth();
                showConnectivityRequest();
                break;

            case UI_STATE_REQUESTING_NETWORK:
                showConnectivityStatus();
                mConnectivityText.setText(R.string.network_connecting);

                mProgressBar.setVisibility(View.VISIBLE);
                hideConnectivityRequest();
                hideContent();

                break;

            case UI_STATE_NETWORK_CONNECTED:
                showConnectivityStatus();
                checkHighBandwidth();
                hideConnectivityRequest();
                hideConnectivityStatus();
                showContent();

                mProgressBar.setVisibility(View.GONE);

                break;

            case UI_STATE_CONNECTION_TIMEOUT:
                showConnectivityStatus();
                mConnectivityIcon.setImageResource(R.drawable.ic_cloud_disconnected);
                mConnectivityText.setText(R.string.network_disconnected);

                mProgressBar.setVisibility(View.GONE);
                showConnectivityWiFiRequest();
                break;
        }
    }

    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void hideContent() {
        recyclerView.setVisibility(View.GONE);
    }

    private void checkHighBandwidth() {
        switch (contentManager.isNetworkHighBandwidth()) {
            case NETWORK_CONNECTED:
                mConnectivityIcon.setImageResource(R.drawable.ic_cloud_happy);
                mConnectivityText.setText(R.string.network_fast);
                break;
            case NETWORK_CONNECTED_SLOW:
                mConnectivityIcon.setImageResource(R.drawable.ic_cloud_sad);
                mConnectivityText.setText(R.string.network_slow);
                break;
            case NETWORK_UNAVAILABLE:
                mConnectivityIcon.setImageResource(R.drawable.ic_cloud_disconnected);
                mConnectivityText.setText(R.string.network_disconnected);
                break;
        }
    }

    private void hideConnectivityStatus() {
        mConnectivityIndicator.postDelayed(new Runnable() {
            public void run() {
                mConnectivityIndicator.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void showConnectivityStatus() {
        mConnectivityIndicator.postDelayed(new Runnable() {
            public void run() {
                mConnectivityIndicator.setVisibility(View.VISIBLE);
            }
        }, 0);
    }

    private void showConnectivityWiFiRequest() {
        buttonState = BUTTON_ADD_WIFI;
        recyclerView.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.VISIBLE);
        buttonRequestNetwork.setImageResource(R.drawable.ic_wifi_network);
        buttonLabel.setText(R.string.button_add_wifi);
    }

    private void showConnectivityRequest() {
        buttonState = BUTTON_REQUEST_HIGHBANDWIDTH;
        recyclerView.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.VISIBLE);
    }

    private void hideConnectivityRequest() {

        recyclerView.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
        hideConnectivityStatus();
    }

    @OnClick({R.id.button_icon, R.id.button_icon_no})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_icon:
                if (buttonState == BUTTON_REQUEST_HIGHBANDWIDTH) {
                    contentManager.requestHighBandwidthNetwork();
                } else if (buttonState == BUTTON_ADD_WIFI) {
                    contentManager.addWifiNetwork();
                }
                break;
            case R.id.button_icon_no:
                hideConnectivityRequest();
                break;
        }
    }

    @Override
    public void onRefresh() {
        contentManager.getFreshData(launchAdapter.getCategory());
    }
}
