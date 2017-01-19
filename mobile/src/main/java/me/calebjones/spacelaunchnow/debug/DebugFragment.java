package me.calebjones.spacelaunchnow.debug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.calebjones.spacelaunchnow.R;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_debug, container, false);
        return root;
    }

    @Override
    public void setPresenter(DebugContract.Presenter presenter) {

    }

    @Override
    public void showSupporterSnackbar() {

    }

    @Override
    public void showDebugSnackbar() {

    }

    @Override
    public void toggleSupporter() {

    }
}
