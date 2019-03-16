package me.calebjones.spacelaunchnow.ui.debug;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import androidx.appcompat.widget.AppCompatSpinner;
import me.calebjones.spacelaunchnow.local.common.BaseNavigator;
import me.calebjones.spacelaunchnow.local.common.BasePresenter;
import me.calebjones.spacelaunchnow.local.common.BaseView;

public interface DebugContract {

    interface Navigator extends BaseNavigator {

        void goHome();

    }

    interface NavigatorProvider {

        @NonNull
        Navigator getNavigator(DebugContract.Presenter presenter);
    }

    interface View extends BaseView<Presenter> {

        void showDebugLaunchSnackbar(boolean state);

        void showSupporterSnackbar(boolean state);

        void setSupporterSwitch(boolean state);

        void showSnackbarMessage(String message);

    }

    interface Presenter extends BasePresenter {

        void onHomeClicked();

        void setNavigator(@NonNull Navigator navigator);

        void toggleSupporterSwitch(boolean selected);

        void endpointSelectorClicked(String selection);

        void syncNextLaunchClicked(Context context);

        void jobEventButtonClicked(Context context);

        void syncBackgroundSyncClicked(Context context);

        void syncVehiclesClicked(Context context);

        void downloadLogsClicked(Activity activity);

        void deleteFilesClicked(Context context);

        boolean getSupporterStatus();

    }
}
