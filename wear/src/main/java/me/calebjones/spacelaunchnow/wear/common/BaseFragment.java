package me.calebjones.spacelaunchnow.wear.common;

import androidx.core.app.Fragment;
import timber.log.Timber;

public class BaseFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        Timber.v("BaseFragment - onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.v("BaseFragment - onStop");
    }
}
