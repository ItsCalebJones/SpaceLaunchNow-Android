package me.calebjones.spacelaunchnow.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;

abstract public class CustomFragment extends Fragment {

    public void startActivity(Intent intent, Bundle bundle) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().startActivity(intent, bundle);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle bundle) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().startActivityForResult(intent, requestCode, bundle);
        }
    }
}
