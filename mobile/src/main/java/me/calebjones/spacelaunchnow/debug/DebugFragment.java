package me.calebjones.spacelaunchnow.debug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by cjones on 1/18/17.
 */

public class DebugFragment extends Fragment implements DebugContract.View{

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
    public void setPresenter(DebugContract.Presenter presenter) {

    }
}
