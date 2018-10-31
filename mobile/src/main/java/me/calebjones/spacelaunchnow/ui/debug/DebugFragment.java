package me.calebjones.spacelaunchnow.ui.debug;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;

public class DebugFragment extends Fragment implements DebugContract.View {

    @BindView(R.id.supporter_switch)
    SwitchCompat supporterSwitch;
    @BindView(R.id.debug_launches_switch)
    SwitchCompat debugLaunchesSwitch;
    @BindView(R.id.next_launch_button)
    AppCompatButton nextLaunchButton;
    @BindView(R.id.background_sync_button)
    AppCompatButton backgroundSyncButton;
    @BindView(R.id.vehicle_sync_button)
    AppCompatButton vehicleSyncButton;
    @BindView(R.id.download_file)
    AppCompatButton downloadFileButton;
    @BindView(R.id.delete_file)
    AppCompatButton deleteFileButton;
    @BindView(R.id.job_event_button)
    AppCompatButton jobEventButton;
    @BindView(R.id.debug_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private DebugContract.Presenter debugPresenter;

    public DebugFragment() {
        // Requires empty public constructor
    }

    public static DebugFragment newInstance() {
        return new DebugFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_debug, container, false);
        ButterKnife.bind(this, root);
        initializeViews();
        return root;
    }

    @Override
    public void setPresenter(DebugContract.Presenter presenter) {
        debugPresenter = presenter;
    }

    @Override
    public void showDebugLaunchSnackbar(boolean state) {
        SnackbarHandler.showInfoSnackbar(getContext(), coordinatorLayout, "Debug Launch Status: " + state);
    }

    @Override
    public void showSupporterSnackbar(boolean state) {
        SnackbarHandler.showInfoSnackbar(getContext(), coordinatorLayout, "Supporter Status: " + state);
    }

    @Override
    public void setSupporterSwitch(boolean state) {
        supporterSwitch.setChecked(state);
    }

    @Override
    public void setDebugLaunches(boolean state) {
        debugLaunchesSwitch.setChecked(state);
    }

    @Override
    public void showSnackbarMessage(String message) {
        SnackbarHandler.showInfoSnackbar(getContext(), coordinatorLayout, message);
    }

    //UI Button and Switches
    @OnClick(R.id.supporter_switch)
    void supportSwitchClicked(SwitchCompat view) {
        debugPresenter.toggleSupporterSwitch(view.isChecked());
    }

    @OnClick(R.id.debug_launches_switch)
    void debugSwitchCLicked(SwitchCompat view) {
        debugPresenter.toggleDebugLaunchesClicked(view.isChecked(), getContext());
    }

    @OnClick(R.id.next_launch_button)
    void nextLaunchClicked() {
        debugPresenter.syncNextLaunchClicked(getContext());
    }

    @OnClick(R.id.background_sync_button)
    void backgroundSyncClicked() {
        debugPresenter.syncBackgroundSyncClicked(getContext());
    }

    @OnClick(R.id.vehicle_sync_button)
    void vehicleSyncClicked() {
        debugPresenter.syncVehiclesClicked(getContext());
    }

    @OnClick(R.id.download_file)
    void downloadFileClicked() {
        debugPresenter.downloadLogsClicked(getActivity());
    }

    @OnClick(R.id.delete_file)
    void deleteFileClicked() {
        debugPresenter.deleteFilesClicked(getContext());
    }

    @OnClick(R.id.job_event_button)
    void jobEventClicked(){
        debugPresenter.jobEventButtonClicked(getContext());
    }

    public void initializeViews() {
        supporterSwitch.setChecked(debugPresenter.getSupporterStatus());
        debugLaunchesSwitch.setChecked(debugPresenter.getDebugStatus());
    }
}
