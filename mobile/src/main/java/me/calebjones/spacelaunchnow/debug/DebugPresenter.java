package me.calebjones.spacelaunchnow.debug;



/**
 * Created by cjones on 1/18/17.
 */

public class DebugPresenter implements DebugContract.Presenter{

    private final DebugContract.View debugView;

    public DebugPresenter(DebugContract.View view){
        debugView = view;
        debugView.setPresenter(this);
    }

    @Override
    public void toggleSupporterClicked() {

    }

    @Override
    public void toggleDebugLaunchesClicked() {

    }

    @Override
    public void syncNextLaunchClicked() {

    }

    @Override
    public void syncBackgroundSyncClicked() {

    }

    @Override
    public void syncVehiclesClicked() {

    }

    @Override
    public void downloadLogsClicked() {

    }

    @Override
    public void deleteFilesClicked() {

    }

    @Override
    public void start() {

    }
}
