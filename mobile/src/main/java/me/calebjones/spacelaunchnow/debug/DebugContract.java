package me.calebjones.spacelaunchnow.debug;

import me.calebjones.spacelaunchnow.common.BasePresenter;
import me.calebjones.spacelaunchnow.common.BaseView;

public interface DebugContract {

    interface View extends BaseView<Presenter> {

        void showSupporterSnackbar();

        void showDebugSnackbar();

        void toggleSupporter();

    }

    interface Presenter extends BasePresenter {

        void toggleSupporterClicked();

        void toggleDebugLaunchesClicked();

        void syncNextLaunchClicked();

        void syncBackgroundSyncClicked();

        void syncVehiclesClicked();

        void downloadLogsClicked();

        void deleteFilesClicked();

    }
}
